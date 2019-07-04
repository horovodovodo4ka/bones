package pro.horovodovodo4ka.bones.statesstore

import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling

inline fun <reified V : Bone, T> T.saveBones()
        where T : EmergencyPersisterInterface<T>, T : BoneSibling<V> {
    with(bone) {
        isActive = false
        saveBones { it.bone = this }
    }
}