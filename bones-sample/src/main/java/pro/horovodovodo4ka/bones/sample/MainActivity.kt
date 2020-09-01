package pro.horovodovodo4ka.bones.sample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Spine
import pro.horovodovodo4ka.bones.Wrist
import pro.horovodovodo4ka.bones.extensions.processBackPress
import pro.horovodovodo4ka.bones.sample.navigation.NavigationStack
import pro.horovodovodo4ka.bones.sample.navigation.TabBar
import pro.horovodovodo4ka.bones.sample.presentation.TestForm
import pro.horovodovodo4ka.bones.sample.presentation.TestScreen
import pro.horovodovodo4ka.bones.statesstore.EmergencyPersister
import pro.horovodovodo4ka.bones.statesstore.EmergencyPersisterInterface
import pro.horovodovodo4ka.bones.statesstore.loadBones
import pro.horovodovodo4ka.bones.statesstore.saveBones
import pro.horovodovodo4ka.bones.ui.SpineNavigatorInterface
import pro.horovodovodo4ka.bones.ui.delegates.NavigatorDelayedTransactions
import pro.horovodovodo4ka.bones.ui.delegates.SpineNavigator
import pro.horovodovodo4ka.bones.ui.helpers.ActivityAppRestartCleaner

/**
 * Demo bone. Realize support of exiting from app and back presses.
 */
class RootBone(root: Bone) :
    Spine(root),
    Wrist.Listener,
    Finger.Listener,
    Spine.Listener {

    init {
        persistSibling = true
    }

    private var canExit = false

    fun dropExitStatus() {
        canExit = false
    }

    override fun fingerSwitched(transition: Wrist.TransitionType) = dropExitStatus()
    override fun phalanxSwitched(transition: Finger.TransitionType) = dropExitStatus()
    override fun boneSwitched(transition: Spine.TransitionType) = dropExitStatus()

    fun processBack(preExitCallback: () -> Unit): Boolean {
        if (processBackPress()) {
            if (!canExit) {
                preExitCallback()
                canExit = true
                return false
            }
        }
        return canExit
    }
}

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
    SpineNavigatorInterface<RootBone> by SpineNavigator(),
    EmergencyPersisterInterface<MainActivity> by EmergencyPersister(),
    ActivityAppRestartCleaner {

    init {
        managerProvider = ::getSupportFragmentManager
    }

    override fun onBackPressed() {
        if (bone.processBack { Toast.makeText(this, """Press "back" button again to exit.""", Toast.LENGTH_LONG).show() }) finish()
    }

    override fun onResume() {
        super.onResume()

        emergencyUnpin()

        bone.dropExitStatus()

        NavigatorDelayedTransactions.executePendingTransactions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadBones(savedInstanceState) {
            RootBone(
                TabBar(
                    NavigationStack(TestScreen()),
                    TestForm(),
                    TestScreen()
                )
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        emergencyPin(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        saveBones()
    }
}

