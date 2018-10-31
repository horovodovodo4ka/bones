package pro.horovodovodo4ka.bones.ui.helpers

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatDialogFragment
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling
import pro.horovodovodo4ka.bones.extensions.dismiss

abstract class BoneAppCompatDialogFragment<T : Bone> : AppCompatDialogFragment(), BoneSibling<T> {

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)

        bone.dismiss()
    }
}