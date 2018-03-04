package pro.horovodovodo4ka.bones.sample.presentation.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_test_dialog.*
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.sample.R
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page
import pro.horovodovodo4ka.bones.ui.helpers.BoneDialogFragment
import java.util.Calendar
import java.util.Date

class WidgetDialogBone(value: Date?) : Phalanx() {
    override val seed = { TestDialog() }

    var value: Date? = value
        set(value) {
            field = value
            notifyChange()
        }

}

class TestDialog : BoneDialogFragment<WidgetDialogBone>(),
    FragmentSibling<WidgetDialogBone> by Page(),
    BonePersisterInterface<WidgetDialogBone> {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_test_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()

        bone.value?.also { calendar.time = it }

        datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, date ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, date)

            bone.value = calendar.time
        }
    }

    // region BonePersisterInterface

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<BoneDialogFragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<BoneDialogFragment>.onCreate(savedInstanceState)
    }

    // endregion
}