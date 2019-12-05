package pro.horovodovodo4ka.bones.sample.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_navigation_stack.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.BoneStateValue
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Primacy
import pro.horovodovodo4ka.bones.extensions.dismiss
import pro.horovodovodo4ka.bones.extensions.present
import pro.horovodovodo4ka.bones.persistance.BonePersisterInterface
import pro.horovodovodo4ka.bones.sample.R
import pro.horovodovodo4ka.bones.sample.presentation.TestScreen
import pro.horovodovodo4ka.bones.ui.FingerNavigatorInterface
import pro.horovodovodo4ka.bones.ui.delegates.FingerNavigator
import pro.horovodovodo4ka.bones.ui.extensions.addNavigationToToolbar
import pro.horovodovodo4ka.bones.ui.extensions.removeNavigationFromToolbar

interface NavigationStackPresentable {
    val fragmentTitle: String
}

open class NavigationStack(rootPhalanx: Bone? = null) : Finger(rootPhalanx) {
    override val seed = { NavigationStackFragment() }
}

open class NavigationStackFragment : Fragment(),
    BonePersisterInterface<NavigationStack>,
    FingerNavigatorInterface<NavigationStack> by FingerNavigator(R.id.stack_fragment_container) {

    // region ContainerFragmentSibling

    override fun onAttach(context: Context) {
        super.onAttach(context)
        managerProvider = ::getChildFragmentManager
    }

    override fun onDetach() {
        super.onDetach()
        managerProvider = null
    }

    // endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_navigation_stack, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        push_button.setOnClickListener {
            bone.push(TestScreen())
        }

        pop_button.setOnClickListener {
            bone.pop()
        }

        to_root_button.setOnClickListener {
            bone.popToRoot()
        }

        replace_button.setOnClickListener {
            bone.replace(with = TestScreen())
        }

        modal_button.setOnClickListener {
            bone.present(TestScreen())
        }

        delayed_button.setOnClickListener {
            GlobalScope.launch(Main) {
                delay(2_000)

                val first = TestScreen()
                bone.present(first)

                delay(2_000)

                bone.present(TestScreen())

                delay(2_000)
                first.dismiss()
            }
        }

        refreshUI()
    }

    override fun onRefresh() {

        if (view == null) return

        val title = (bone.fingertip as? NavigationStackPresentable)?.fragmentTitle
        when (title) {
            null -> toolbar.visibility = View.GONE
            else -> {
                toolbar.visibility = View.VISIBLE
                toolbar.title = title

                if (bone.phalanxes.size > 1) addNavigationToToolbar(toolbar, R.drawable.ic_arrow_back_white)
                else removeNavigationFromToolbar(toolbar)
            }
        }
    }

    override fun onBoneStateChange(state: BoneStateValue) {
        if (state is Primacy) Log.d("primacy", "finger  ${bone.id.substring(0..7)} -> ${bone.isPrimary}")
    }

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
}