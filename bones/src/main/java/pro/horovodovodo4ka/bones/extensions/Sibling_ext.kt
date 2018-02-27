package pro.horovodovodo4ka.bones.extensions

import android.support.annotation.CallSuper
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.ui.FragmentSibling

/**
 * @return **false** if processed backPress
 */
@CallSuper
fun Bone.processBackPress(): Boolean {
    return (sibling as? FragmentSibling<*>)?.processBackPress() ?: true
}