package pro.horovodovodo4ka.bones.sample.presentation.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.view_test_widget.view.*
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneSibling
import pro.horovodovodo4ka.bones.extensions.dismiss
import pro.horovodovodo4ka.bones.extensions.glueWith
import pro.horovodovodo4ka.bones.extensions.present
import pro.horovodovodo4ka.bones.sample.R
import pro.horovodovodo4ka.bones.ui.ViewBone
import pro.horovodovodo4ka.bones.ui.delegates.Content
import java.text.SimpleDateFormat
import java.util.Date

class WidgetBone : ViewBone() {

    var value: Date? = null
        set(value) {
            field = value
            notifyChange()
        }

    // prevent multiple dialogs on fast clicks
    private var dialog: WidgetDialogBone? = null

    fun pickDate() {
        dialog = dialog ?: WidgetDialogBone(value)
            .also {
                present(it)
                subscribe(it)
            }
    }

    override fun onBoneChanged(bone: Bone) {
        bone as WidgetDialogBone
        value = bone.value
        bone.dismiss()
        notifyChange()

        dialog = null
    }

}

class TestWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr),
    BoneSibling<WidgetBone> by Content() {

    init {
        glueWith(WidgetBone())

        LayoutInflater.from(context).inflate(R.layout.view_test_widget, this, true)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        setOnClickListener {
            bone.pickDate()
        }
    }

    // just simple accessor
    var value: Date?
        get() = bone.value
        set(value) {
            bone.value = value
        }

    private fun refresh() {
        date_label.text = value?.let { formatter.format(it) } ?: "Choose date"
    }

    override fun onBoneChanged() = refresh()

    companion object {
        @SuppressLint("SimpleDateFormat")
        private val formatter = SimpleDateFormat("dd.MM.yyyy")
    }
}