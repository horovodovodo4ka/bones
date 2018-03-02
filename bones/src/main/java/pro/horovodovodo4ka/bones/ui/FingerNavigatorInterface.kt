package pro.horovodovodo4ka.bones.ui

import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.extensions.processBackPress

/**
 * Default implementation for fragments which are siblings of finger.
 */
interface FingerNavigatorInterface<T : Finger> : ContainerFragmentSibling<T> {
    override fun processBackPress(): Boolean {
        if (bone.fingertip?.processBackPress() == false) return false

        val oldBone = bone.fingertip
        bone.pop()
        return oldBone == bone.fingertip
    }
}