package pt.br.lib.blaklister.config

import com.uchuhimo.konf.ConfigSpec

object BlacklistSpec : ConfigSpec() {
    val properties by optional<List<PropertyNames>>(listOf())
}

data class PropertyNames(
    val className: String,
    val names: List<String>
)
