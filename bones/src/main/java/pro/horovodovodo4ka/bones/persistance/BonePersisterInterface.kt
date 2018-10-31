package pro.horovodovodo4ka.bones.persistance

import android.os.Bundle
import androidx.annotation.CallSuper
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling

private const val BONE_ID_KEY = "BONE_ID_KEY"

/**
 * Helper interface for save id of bone on configuration change and relink bone with current sibling after that change.
 * Must be used if persistence and consistency of pair bone-sibling is required
 */
interface BonePersisterInterface<T : Bone> : BoneSibling<T> {

    @CallSuper
    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(BONE_ID_KEY, bone.id)
    }

    @CallSuper
    fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState
            ?.getString(BONE_ID_KEY)
            ?.let { Bone[it] }
            ?.also {
                @Suppress("UNCHECKED_CAST")
                bone = it as T
                bone.sibling = this
            }
    }
}