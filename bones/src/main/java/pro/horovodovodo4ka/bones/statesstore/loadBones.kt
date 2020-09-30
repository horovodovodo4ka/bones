package pro.horovodovodo4ka.bones.statesstore

import android.os.Bundle
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling
import pro.horovodovodo4ka.bones.extensions.glueWith
import pro.horovodovodo4ka.bones.ui.helpers.ActivityAppRestartCleaner

/**
 * Creates bone with passed fabric lambda or restore previous state of linked bone
 */
inline fun <reified V : Bone, T> T.loadBones(savedInstanceState: Bundle?, producer: () -> V)
        where T : EmergencyPersisterInterface<T>, T : BoneSibling<V>, T : ActivityAppRestartCleaner {

    if (!emergencyLoad(savedInstanceState, this)) {
        savedInstanceState?.also { clearFragments() }

        bone = producer()

        glueWith(bone)
        bone.isActive = true

        refreshUI()
    } else {
        glueWith(bone)
        bone.isActive = true
    }
}
