package pro.horovodovodo4ka.bones.ui

import android.content.Context
import android.content.ContextWrapper
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling

class BoneContext<T : Bone>(val bone: T, base: Context) : ContextWrapper(base)

fun <T : Bone> T.contextOf(context: Context) = BoneContext(this, context)

@Suppress("UNCHECKED_CAST", "LeakingThis")
open class ContextSibling<T : Bone>(context: Context) : BoneSibling<T> {
    override var bone = (context as? BoneContext<T>)?.bone
        ?: throw Exception("Context of this sibling must be instance of ContextSibling<*>")
}