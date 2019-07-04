package pro.horovodovodo4ka.bones.statesstore

import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling

inline fun <reified V : Bone, T : BoneSibling<V>> EmergencyPersisterInterface<T>.saveBones(bone: V) {
    with(bone) {
        sibling = null // remove strong pointer to existing activity instance
        saveBones { it.bone = this }
    }

    bone.isActive = false
}