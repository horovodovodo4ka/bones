package pro.horovodovodo4ka.bones.extensions

import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling

/**
 * Force link bone with sibling.
 */
inline fun <reified T : Bone> BoneSibling<T>.glueWith(bone: T) {
    this.bone = bone
    bone.sibling = this
}
