package pt.br.lib.blaklister.repository

import kotlin.reflect.KProperty

class MapDataRepository<K, V, D : DataEntity<K>>(
    private val map: MutableMap<K, MutableMap<String, Any>> = mutableMapOf()
) : DataRepository<K, V, D> {

    override fun setValue(entity: D, property: KProperty<*>, value: V?) {
        val key = entity.id
        if (key != null && !entity.ignore(entity::class, property.name)) {
            map.computeIfAbsent(key) { mutableMapOf() }[property.name] = value as Any
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(entity: D, property: KProperty<*>): V? = map[entity.id]?.get(property.name) as? V
}
