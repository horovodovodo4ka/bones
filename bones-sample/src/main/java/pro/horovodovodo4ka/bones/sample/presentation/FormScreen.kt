package pro.horovodovodo4ka.bones.sample.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_form_screen.*
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.sample.R
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page
import java.text.SimpleDateFormat

class TestForm : Phalanx() {
    override val seed = { FormScreen() }

    fun addFields(vararg bone: Bone) {
        bone.forEach {
            add(it)
            subscribe(it)
        }
    }

    override fun onBoneChanged(bone: Bone) = notifyChange()
}

class FormScreen : Fragment(),
    FragmentSibling<TestForm> by Page(),
    BonePersisterInterface<TestForm> {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_form_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bone.addFields(dateField1.bone, dateField2.bone)
        refresh()
    }

    // region BoneSibling

    override fun onBoneChanged() = refresh()

    // endregion

    // region BonePersisterInterface

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<Fragment>.onCreate(savedInstanceState)
    }

    // endregion

    // region Bone subscribe sample

    @SuppressLint("SetTextI18n")
    private fun refresh() {
        intervalLabel.text = when {
            dateField1.value != null && dateField2.value != null ->
                formatter.format(dateField1.value) + " - " + formatter.format(dateField2.value)
            else ->
                "Choose both dates"
        }
    }

    companion object {
        @SuppressLint("SimpleDateFormat")
        private val formatter = SimpleDateFormat("dd.MM.yyyy")
    }

    // endregion
}