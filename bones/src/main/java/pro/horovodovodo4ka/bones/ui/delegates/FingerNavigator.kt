package pro.horovodovodo4ka.bones.ui.delegates

import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Finger.TransitionType
import pro.horovodovodo4ka.bones.Finger.TransitionType.NONE
import pro.horovodovodo4ka.bones.Finger.TransitionType.POPPING
import pro.horovodovodo4ka.bones.Finger.TransitionType.PUSHING
import pro.horovodovodo4ka.bones.Finger.TransitionType.REPLACING
import pro.horovodovodo4ka.bones.ui.FingerNavigatorInterface
import pro.horovodovodo4ka.bones.ui.extensions.freezeSnapshotAsBackground

/**
 * Delegate that implements default finger navigation.
 * Uses support library.
 * Also support some specific cases such using [DialogFragment]
 */
class FingerNavigator<T : Finger>(override val containerId: Int) : FingerNavigatorInterface<T>, NavigatorDelayedTransactions {

    override lateinit var bone: T
    override var managerProvider: (() -> FragmentManager)? = null

    override fun refreshUI(from: Bone?, to: Bone?) {

        fun execute(bone: Finger, transaction: TransitionType) {

            with(bone.sibling as FingerNavigatorInterface<*>) {

                val manager = (this@with.managerProvider ?: return)()

                val fragment = bone.fingertip?.sibling as? Fragment ?: return

                val transition = when (transaction) {
                    PUSHING -> FragmentTransaction.TRANSIT_FRAGMENT_OPEN
                    POPPING -> FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
                    REPLACING -> FragmentTransaction.TRANSIT_FRAGMENT_FADE
                    else -> FragmentTransaction.TRANSIT_NONE
                }

                // make screenshot and place background due android strange behavior with nested fragments
                if (bone.transitionType != NONE) {
                    manager.fragments.lastOrNull { it.isVisible }?.freezeSnapshotAsBackground()
                }

                manager
                    .beginTransaction()
                    .setTransition(transition)
                    .replace(containerId, fragment)
                    .runOnCommit {
                        super.refreshUI(from, to)
                        bone.fingertip?.sibling?.refreshUI()
                    }
                    .commit()
            }
        }

        val manager = (managerProvider ?: return)()
        val transaction = bone.transitionType
        if (manager.isStateSaved) {
            post {
                execute(bone, transaction)
            }
        } else {
            execute(bone, transaction)
        }
    }
}
