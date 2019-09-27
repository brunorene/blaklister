package pt.br.lib.blaklister.repository

import com.oath.halodb.HaloDB
import com.oath.halodb.HaloDBOptions
import pt.br.lib.blaklister.helper.joinAsByteArray
import pt.br.lib.blaklister.helper.toByteArray
import java.io.File
import java.util.UUID
import kotlin.reflect.KProperty

class HaloDatabase(directory: File = File("./halodb")) {

    val access: HaloDB

    init {
        // Open a db with default options.
        val options = HaloDBOptions()

        // Size of each data file will be 1GB.
        options.maxFileSize = 1024 * 1024 * 1024

        // Size of each tombstone file will be 64MB
        // Large file size mean less file count but will slow down db open time. But if set
        // file size too small, it will result large amount of tombstone files under db folder
        options.maxTombstoneFileSize = 64 * 1024 * 1024

        // Set the number of threads used to scan index and tombstone files in parallel
        // to build in-memory index during db open. It must be a positive number which is
        // not greater than Runtime.getRuntime().availableProcessors().
        // It is used to speed up db open time.
        options.buildIndexThreads = 8

        // The threshold at which page cache is synced to disk.
        // data will be durable only if it is flushed to disk, therefore
        // more data will be lost if this value is set too high. Setting
        // this value too low might interfere with read and write performance.
        options.flushDataSizeBytes = 10 * 1024 * 1024

        // The percentage of stale data in a data file at which the file will be compacted.
        // This value helps control write and space amplification. Increasing this value will
        // reduce write amplification but will increase space amplification.
        // This along with the compactionJobRate below is the most important setting
        // for tuning HaloDB performance. If this is set to x then write amplification
        // will be approximately 1/x.
        options.compactionThresholdPerFile = 0.7

        // Controls how fast the compaction job should run.
        // This is the amount of data which will be copied by the compaction thread per second.
        // Optimal value depends on the compactionThresholdPerFile option.
        options.compactionJobRate = 50 * 1024 * 1024

        // Setting this value is important as it helps to preallocate enough
        // memory for the off-heap cache. If the value is too low the db might
        // need to rehash the cache. For a db of size n set this value to 2*n.
        options.numberOfRecords = 100_000_000

        // Delete operation for a key will write a tombstone record to a tombstone file.
        // the tombstone record can be removed only when all previous version of that key
        // has been deleted by the compaction job.
        // enabling this option will delete during startup all tombstone records whose previous
        // versions were removed from the data file.
        options.isCleanUpTombstonesDuringOpen = true

        // HaloDB does native memory allocation for the in-memory index.
        // Enabling this option will release all allocated memory back to the kernel when the db is closed.
        // This option is not necessary if the JVM is shutdown when the db is closed, as in that case
        // allocated memory is released automatically by the kernel.
        // If using in-memory index without memory pool this option,
        // depending on the number of records in the database,
        // could be a slow as we need to call _free_ for each record.
        options.isCleanUpInMemoryIndexOnClose = false

        // ** settings for memory pool **
        options.isUseMemoryPool = true

        // Hash table implementation in HaloDB is similar to that of ConcurrentHashMap in Java 7.
        // Hash table is divided into segments and each segment manages its own native memory.
        // The number of segments is twice the number of cores in the machine.
        // A segment's memory is further divided into chunks whose size can be configured here.
        options.memoryPoolChunkSize = 2 * 1024 * 1024

        // using a memory pool requires us to declare the size of keys in advance.
        // Any write request with key length greater than the declared value will fail, but it
        // is still possible to store keys smaller than this declared size.
        options.fixedKeySize = 8

        // Open the database. Directory will be created if it doesn't exist.
        // If we are opening an existing database HaloDB needs to scan all the
        // index files to create the in-memory index, which, depending on the db size, might take a few minutes.
        access = HaloDB.open(directory, options)
    }
}

abstract class HaloDbRepository<V, D : UUIDEntity>(private val db: HaloDB) : DataRepository<UUID, V, D> {

    abstract fun encodeValue(value: V): ByteArray?

    abstract fun decodeValue(value: ByteArray): V

    private fun encodeKey(entity: D, property: KProperty<*>) =
        listOf(entity.javaClass.simpleName.toByteArray(),
            entity.id.toByteArray(),
            property.name.toByteArray()).joinAsByteArray()

    override fun setValue(entity: D, property: KProperty<*>, value: V) {
        encodeValue(value)?.let { db.put(encodeKey(entity, property), it) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(entity: D, property: KProperty<*>): V? =
        decodeValue(db[encodeKey(entity, property)])
}

class StringHaloDbDataRepository<D : UUIDEntity>(db: HaloDB)
    : HaloDbRepository<String?, D>(db) {
    override fun encodeValue(value: String?) = value?.toByteArray()

    override fun decodeValue(value: ByteArray) = value.contentToString()
}

class BooleanHaloDbDataRepository<D : UUIDEntity>(db: HaloDB)
    : HaloDbRepository<Boolean?, D>(db) {
    override fun encodeValue(value: Boolean?): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decodeValue(value: ByteArray): Boolean? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

