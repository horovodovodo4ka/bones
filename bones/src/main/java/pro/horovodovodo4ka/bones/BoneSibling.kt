package pro.horovodovodo4ka.bones

import android.support.annotation.CallSuper

/**
 * Base interface representing bone sibling.
 */
interface BoneSibling<T : Bone> {
    /**
     * Bone, that holds that sibling
     */
    var bone: T

    /**
     * Called by bone on different state changes that requires updating of UI
     */
    @CallSuper
    fun refreshUI(from: Bone? = null, to: Bone? = null) {
        bone.sibling?.onRefresh()
    }

    /**
     * Called after [BoneSibling.refreshUI] called.
     *
     * Default implementation notifies parent bone sibling about UI changes on current level of hierarchy.
     */
    @CallSuper
    fun onRefresh() {
        bone.parentBone?.sibling?.onRefresh()
    }

    /**
     * Should not be used directly. Used by internal API, links bone with it's sibling.
     */
    fun <V : Bone> link(bone: V) {
        @Suppress("UNCHECKED_CAST")
        (bone as? T)?.also { this.bone = it }
    }

    /**
     * Helper method. Can be used by bone to notify it's sibling about changes.
     */
    fun onBoneChanged() {}
}
