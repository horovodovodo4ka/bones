package pro.horovodovodo4ka.bones.ui

import pro.horovodovodo4ka.bones.Wrist
import pro.horovodovodo4ka.bones.extensions.processBackPress

/**
 * Default implementation for fragments which are siblings of wrist.
 */
interface WristNavigatorInterface<T : Wrist> : ContainerFragmentSibling<T> {
    override fun processBackPress(): Boolean {
        return bone.activeBone.processBackPress()
    }
}
