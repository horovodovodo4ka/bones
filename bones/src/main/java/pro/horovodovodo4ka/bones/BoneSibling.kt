package pro.horovodovodo4ka.bones

import androidx.annotation.CallSuper

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
    fun refreshUI() {
        bone.sibling?.onRefresh()
    }

    /**
     * Called after [BoneSibling.refreshUI] called.
     *
     * Default implementation notifies parent bone sibling about UI changes on current level of hierarchy.
     */
    fun onRefresh() {}

    /**
     * Should not be used directly. Used by internal API, links bone with it's sibling.
     */
    fun <V : Bone> link(bone: V) {
        @Suppress("UNCHECKED_CAST")
        (bone as? T)?.also { this.bone = it }
    }

    /**
     * Called each time bone calls [Bone.notifyChange] method. Can be used to refresh sibling's content to new bone state.
     */
    fun onBoneChanged() {}

    /**
     * Same as [Bone.onBoneChanged] but for specific states, can be custom
     */
    fun onBoneStateChange(state: BoneStateValue) {}
}
