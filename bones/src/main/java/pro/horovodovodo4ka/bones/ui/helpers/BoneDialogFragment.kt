package pro.horovodovodo4ka.bones.ui.helpers

import android.content.DialogInterface
import android.support.v4.app.DialogFragment
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling
import pro.horovodovodo4ka.bones.extensions.dismiss

abstract class BoneDialogFragment<T : Bone> : DialogFragment(), BoneSibling<T> {

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)

        bone.dismiss()
    }

}