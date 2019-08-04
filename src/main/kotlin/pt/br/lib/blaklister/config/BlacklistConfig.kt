package pt.br.lib.blaklister.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.InputStream
import kotlin.reflect.KProperty

data class PropertyNames(
    val className: String,
    val propertyNames: List<String>
)

open class BlacklistConfig(config: InputStream) {
    private val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
    private val propertiesToIgnore = mapper.readValue<List<PropertyNames>>(config)

    fun toBeIgnored(clazz: Class<*>, property: KProperty<*>) =
        propertiesToIgnore.any { it.className == clazz.name && it.propertyNames.any { ps -> ps == property.name } }
}
