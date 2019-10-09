package pt.br.lib.blaklister.repository

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pt.br.lib.blaklister.config.BlacklistSpec
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface DataEntity<K> {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(DataEntity::class.java)

        private fun blacklist(): Map<String, List<String>> {
            val config = Config { addSpec(BlacklistSpec) }
                .from.yaml.file("blacklist.yml", true)
                .from.yaml.resource("/blacklist.yml", true)
                .from.env()
                .from.systemProperties()[BlacklistSpec.properties]
            logger.debug("config: $config")
            return config.associate { it.className to it.names }
        }

        private var blacklist = blacklist()
    }

    fun refreshConfig() {
        blacklist = blacklist()
    }

    fun ignore(clazz: KClass<*>, name: String) =
        blacklist[clazz.java.name]?.any { it == name } == true

    val id: K
}

interface DataRepository<K, V, D : DataEntity<K>> {

    operator fun setValue(entity: D, property: KProperty<*>, value: V?)

    operator fun getValue(entity: D, property: KProperty<*>): V?
}
