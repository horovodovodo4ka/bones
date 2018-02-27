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
import pro.horovodovodo4ka.bones.extensions.present
import pro.horovodovodo4ka.bones.sample.R
import pro.horovodovodo4ka.bones.sample.presentation.widget.TestWidget.WidgetBone
import java.text.SimpleDateFormat
import java.util.Date

class TestWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr),
    BoneSibling<WidgetBone> {

    inner class WidgetBone : Bone(persistSibling = true) {
        override val seed = { this@TestWidget }
        override val siblingId: Int by lazy { this@TestWidget.id }

        var value: Date? = null

        fun pickDate() {
            val dlg = WidgetDialogBone(value) {
                value = it
                dismiss()
                notifyChange()
            }
            bone.present(dlg)
        }
    }

    override var bone = WidgetBone()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_test_widget, this, true)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        setOnClickListener {
            bone.pickDate()
        }
    }

    var value: Date?
        get() = bone.value
        set(value) {
            bone.value = value
            refreshText()
        }

    private fun refreshText() {
        date_label.text = value?.let { formatter.format(it) } ?: "Choose date"
    }

    override fun onBoneChanged() {
        refreshText()
    }

    override fun onRefresh() {
        super.onRefresh()
        refreshText()
    }

    companion object {
        @SuppressLint("SimpleDateFormat")
        private val formatter = SimpleDateFormat("dd.MM.yyyy")
    }
}