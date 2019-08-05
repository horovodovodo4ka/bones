package pro.horovodovodo4ka.bones.statesstore

import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import pro.horovodovodo4ka.bones.extensions.uuid

/**
 * Technical interface for storing instance data without serialization in bundle. Helps data survive on configuration changes etc.
 */
interface EmergencyPersisterInterface<T> {
    /**
     * Save technical data that sibling **could** be recreated. Must be called in *onSaveInstanceState*.
     *
     * @param outState bundle in which data possibly will be stored
     */
    fun emergencyPin(outState: Bundle)

    /**
     * Removes technical data. **Must** be called when sure instance will not be recreated. Must be called in *onStart* or *onResume*.
     */
    fun emergencyUnpin()

    /**
     * Executes closure previously set with [saveBones]. **Must** be called in *onCreate*.
     *
     * @param savedInstanceState bundle in which emergency data could be.
     * @param instance new instance in which data should be restored
     * @return **true** if emergency data existed and applied
     */
    fun emergencyLoad(savedInstanceState: Bundle?, instance: T): Boolean

    /**
     * Sets **closure** which will be called when [emergencyLoad] will be called next time.
     *
     * @param block **closure** which captures needed values so they can be applied on [emergencyLoad] call. Closure argument is a new instance.
     */
    fun saveBones(block: (T) -> Unit)
}

/**
 * Default implementation for [EmergencyPersisterInterface]. Could be used as is ad delegate for any instance eg [ActivityCompat], [Fragment]
 */
class EmergencyPersister<T : Any> : EmergencyPersisterInterface<T> {

    private class Persister<T> {
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

    private val persister = Persister<T>()

    override fun emergencyPin(outState: Bundle) = persister.pin(outState)
    override fun emergencyLoad(savedInstanceState: Bundle?, instance: T): Boolean = persister.load(savedInstanceState, instance)
    override fun saveBones(block: (T) -> Unit) = persister.save(block)
    override fun emergencyUnpin() = persister.unpin()
}
