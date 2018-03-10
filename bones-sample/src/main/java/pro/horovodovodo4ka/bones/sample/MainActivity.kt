package pro.horovodovodo4ka.bones.sample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Spine
import pro.horovodovodo4ka.bones.Wrist
import pro.horovodovodo4ka.bones.extensions.glueWith
import pro.horovodovodo4ka.bones.extensions.processBackPress
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

    override fun fingerSwitched(from: Int, to: Int) = dropExitStatus()
    override fun phalanxSwitched(from: Bone, to: Bone?, type: Finger.TransitionType) = dropExitStatus()
    override fun boneSwitched(from: Bone, to: Bone, type: Spine.TransitionType) = dropExitStatus()

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

        emergencyRemovePin()

        bone.dropExitStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)

        if (!emergencyLoad(savedInstanceState, this)) {

            super<ActivityAppRestartCleaner>.onCreate(savedInstanceState)

            bone = RootBone(
                TabBar(
                    NavigationStack(TestScreen()),
                    TestForm(),
                    TestScreen()
                )
            )

            glueWith(bone)
            bone.isActive = true

            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, bone.vertebrae.first().sibling as Fragment)
                .commit()
        } else {
            glueWith(bone)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        emergencyPin(outState)
    }

    override fun onDestroy() {
        super.onDestroy()

        val storedBone = bone
        storedBone.sibling = null // remove strong pointer to existing activity instance
        emergencySave {
            it.bone = storedBone
        }
    }
}
