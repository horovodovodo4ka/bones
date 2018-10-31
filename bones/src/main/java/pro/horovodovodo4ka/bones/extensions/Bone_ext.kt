package pro.horovodovodo4ka.bones.extensions

import androidx.annotation.CallSuper
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneInterface
import pro.horovodovodo4ka.bones.BoneSibling
import pro.horovodovodo4ka.bones.NavigationBone
import pro.horovodovodo4ka.bones.Spine
import pro.horovodovodo4ka.bones.ui.FragmentSibling

/**
 * Finds closest bone (BoneInterface) in parents.
 *
 * @param filter optional filter if we need more specific selection either than just taking first bone.
 */
inline fun <reified T : BoneInterface> Bone.closest(filter: (T) -> Boolean = { true }): T? {
    val stack = arrayOf(this) + parents
    for (bone in stack) {
        (bone as? T)?.takeIf(filter)?.let { return it }
    }
    return null
}

/**
 * Shorthand for finding first Spine and presenting bone on it.
 *
 * @param bone bone to present
 *
 * @see [Spine.present]
 */
fun Bone.present(bone: Bone? = null) {
    closest<Spine>()?.present(bone ?: this)
}

/**
 * Shorthand for finding first Spine and dismiss bone from it.
 * If parameter not passed then trying dismiss bone on which this method called
 *
 * @param bone bone to dismiss
 *
 * @see [Spine.dismiss]
 */
fun Bone.dismiss(bone: Bone? = null) {
    closest<Spine>()?.dismiss(bone ?: this)
}

/**
 * Shorthand for NavigationBone - find first and show bone in navigation context.
 *
 * @see [NavigationBone.show]
 */
fun Bone.show(bone: Bone) {
    closest<NavigationBone>()?.show(bone)
}

/**
 * Shorthand for NavigationBone - find first and go back in navigation context.
 *
 * @see [NavigationBone.goBack]
 */
fun Bone.goBack() {
    closest<NavigationBone> { it.goBack() }
}

/**
 * @return **false** if processed backPress
 */
@CallSuper
fun Bone.processBackPress(): Boolean {
    return (sibling as? FragmentSibling<*>)?.processBackPress() ?: true
}

/**
 * Force link bone with sibling.
 */
inline fun <reified T : Bone> T.glueWith(sibling: BoneSibling<T>) {
    this.sibling = sibling
    sibling.bone = this
}
