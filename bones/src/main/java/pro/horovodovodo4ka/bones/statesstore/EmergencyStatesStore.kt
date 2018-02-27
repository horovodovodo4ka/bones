package pro.horovodovodo4ka.bones.statesstore

import android.os.Bundle
import pro.horovodovodo4ka.bones.extensions.uuid

interface EmergencyPersisterInterface<T> {
    fun emergencyPin(outState: Bundle)
    fun emergencyLoad(savedInstanceState: Bundle?, instance: T): Boolean
    fun emergencySave(block: (T) -> Unit)
    fun emergencyRemovePin()
}

class EmergencyPersister<T : Any> : EmergencyPersisterInterface<T> {

    private val persister = Persister<T>()

    override fun emergencyPin(outState: Bundle) = persister.pin(outState)
    override fun emergencyLoad(savedInstanceState: Bundle?, instance: T): Boolean = persister.load(savedInstanceState, instance)
    override fun emergencySave(block: (T) -> Unit) = persister.save(block)
    override fun emergencyRemovePin() = persister.unpin()
}

class Persister<T> {
    private var stateId = String.uuid()

    fun pin(bundle: Bundle?) = StatesStore.pinState(bundle, stateId)

    fun unpin() = StatesStore.clearPin(stateId)

    fun save(restorationBlock: (T) -> Unit) = StatesStore.saveIfPinned(stateId, restorationBlock)

    fun load(bundle: Bundle?, into: T): Boolean {
        val newStateId = StatesStore.restorePinnedState(bundle, into)
        stateId = newStateId ?: stateId
        return newStateId != null
    }
}