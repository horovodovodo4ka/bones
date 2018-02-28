package pro.horovodovodo4ka.bones.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Spine
import pro.horovodovodo4ka.bones.Wrist
import pro.horovodovodo4ka.bones.sample.MainActivity.Root
import pro.horovodovodo4ka.bones.sample.navigation.NavigationStack
import pro.horovodovodo4ka.bones.sample.navigation.TabBar
import pro.horovodovodo4ka.bones.sample.presentation.TestForm
import pro.horovodovodo4ka.bones.sample.presentation.TestScreen
import pro.horovodovodo4ka.bones.statesstore.EmergencyPersister
import pro.horovodovodo4ka.bones.statesstore.EmergencyPersisterInterface
import pro.horovodovodo4ka.bones.ui.SpineNavigatorInterface
import pro.horovodovodo4ka.bones.ui.delegates.SpineNavigator
import pro.horovodovodo4ka.bones.ui.helpers.ActivityAppRestartCleaner

/**
 * Demo activity.
 * Uses [EmergencyPersister] for bone survive between configuration changes.
 * Also uses [ActivityAppRestartCleaner] for cleanup fragments when activity restarts with some bundle data and cannot
 * load bones which died on application terminate.
 *
 * @see EmergencyPersisterInterface
 * @see ActivityAppRestartCleaner
 */
class MainActivity : AppCompatActivity(),
    SpineNavigatorInterface<Root> by SpineNavigator(),
    EmergencyPersisterInterface<MainActivity> by EmergencyPersister(),
    ActivityAppRestartCleaner {

    init {
        managerProvider = ::getSupportFragmentManager
    }

    inner class Root(root: Bone) :
        Spine(root),
        Wrist.Listener,
        Finger.Listener,
        Spine.Listener {

        init {
            persistSibling = true
        }

        override val seed = { this@MainActivity }

        var canExit = false

        override fun fingerSwitched(from: Int, to: Int) {
            canExit = false
        }

        override fun phalanxSwitched(from: Bone, to: Bone?, type: Finger.TransitionType) {
            canExit = false
        }

        override fun boneSwitched(from: Bone, to: Bone, type: Spine.TransitionType) {
            canExit = false
        }

        fun processBack(): Boolean {
            if (processBackPress()) {
                if (!canExit) {
                    Toast.makeText(this@MainActivity, """Press "back" button again to exit.""", Toast.LENGTH_LONG).show()
                    canExit = true
                    return false
                }
            }
            return canExit
        }
    }

    override fun onBackPressed() {
        if (bone.processBack()) finish()
    }

    override fun onResume() {
        super.onResume()

        emergencyRemovePin()

        bone.canExit = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        super<ActivityAppRestartCleaner>.onCreate(savedInstanceState)

        if (!emergencyLoad(savedInstanceState, this)) {
            bone = Root(
                TabBar(
                    NavigationStack(TestScreen()),
                    TestForm(),
                    TestScreen()
                )
            )
        }

        bone.sibling = this
        bone.isActive = true

        refreshUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        emergencyPin(outState)
    }

    override fun onDestroy() {
        super.onDestroy()

        val storedBone = bone
        emergencySave {
            it.bone = storedBone
        }
    }
}

