package pro.horovodovodo4ka.bones.sample.presentation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_form_screen.*
import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.sample.R
import pro.horovodovodo4ka.bones.ui.FragmentSibling
import pro.horovodovodo4ka.bones.ui.delegates.Page

class TestForm : Phalanx() {
    override val seed = { FormScreen() }
}

class FormScreen : Fragment(),
    FragmentSibling<TestForm> by Page(),
    BonePersisterInterface<TestForm> {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_form_screen, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bone.add(dateField1.bone, true)
        bone.add(dateField2.bone, true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super<BonePersisterInterface>.onSaveInstanceState(outState)
        super<Fragment>.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BonePersisterInterface>.onCreate(savedInstanceState)
        super<Fragment>.onCreate(savedInstanceState)
    }
}