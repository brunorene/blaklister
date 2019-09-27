package pt.br.lib.blaklister.repository

import pt.br.lib.blaklister.config.BlacklistConfig
import java.util.UUID
import kotlin.reflect.KProperty

interface DataEntity<K> {
    val id: K
    val blacklist: BlacklistConfig
}

interface DataRepository<K, V, D : DataEntity<K>> {

    fun setValue(entity: D, property: KProperty<*>, value: V)

    fun getValue(entity: D, property: KProperty<*>): V?
}

abstract class UUIDEntity(override val id: UUID = UUID.randomUUID()) : DataEntity<UUID>
