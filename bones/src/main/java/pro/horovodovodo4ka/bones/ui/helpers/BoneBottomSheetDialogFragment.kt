package pro.horovodovodo4ka.bones.ui.helpers

import android.content.DialogInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling
import pro.horovodovodo4ka.bones.extensions.dismiss

abstract class BoneBottomSheetDialogFragment<T : Bone> : BottomSheetDialogFragment(),
    BoneSibling<T> {

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)

        bone.dismiss()
    }
}