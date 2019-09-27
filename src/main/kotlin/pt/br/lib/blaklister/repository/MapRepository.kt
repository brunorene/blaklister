package pt.br.lib.blaklister.repository

import java.util.UUID
import kotlin.reflect.KProperty

abstract class MapDataRepository<V, D: UUIDEntity>(private val map: MutableMap<UUID, MutableMap<String, Any>> = mutableMapOf())
    : DataRepository<UUID, V, D> {

    override fun setValue(entity: D, property: KProperty<*>, value: V) {
        map.computeIfAbsent(entity.id) { mutableMapOf() }[property.name] = value as Any
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(entity: D, property: KProperty<*>): V? =
        map[entity.id]?.get(property.name) as? V
}

class StringMapDataRepository<D: UUIDEntity>(map: MutableMap<UUID, MutableMap<String, Any>> = mutableMapOf())
    : MapDataRepository<String?, D>(map)

class BooleanMapDataRepository<D: UUIDEntity>(map: MutableMap<UUID, MutableMap<String, Any>> = mutableMapOf())
    : MapDataRepository<Boolean?, D>(map)
