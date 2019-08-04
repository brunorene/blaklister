package pt.br.lib.blaklister.delegate

import pt.br.lib.blaklister.repository.DataEntity
import pt.br.lib.blaklister.repository.DataRepository
import kotlin.reflect.KProperty

open class IgnorableProperty<K, V, D : DataEntity<K>>(private val repository: DataRepository<K, V, D>) {
    operator fun getValue(thisRef: D, property: KProperty<*>): V? = repository.getValue(thisRef, property)

    operator fun setValue(thisRef: D, property: KProperty<*>, value: V) {
        if (!thisRef.blacklist.toBeIgnored(thisRef.javaClass, property)) repository.setValue(thisRef, property, value)
    }
}
