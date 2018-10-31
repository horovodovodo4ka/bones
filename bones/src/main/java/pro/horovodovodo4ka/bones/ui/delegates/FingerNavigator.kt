package pro.horovodovodo4ka.bones.ui.delegates

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Finger.TransitionType
import pro.horovodovodo4ka.bones.Finger.TransitionType.None
import pro.horovodovodo4ka.bones.Finger.TransitionType.Popping
import pro.horovodovodo4ka.bones.Finger.TransitionType.Pushing
import pro.horovodovodo4ka.bones.Finger.TransitionType.Replacing
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
    override var transactionSetup: (FragmentTransaction.(targetFragment: Fragment) -> Unit)? = null

    override fun refreshUI() {

        fun execute(bone: Finger, transaction: TransitionType) {

            with(bone.sibling as FingerNavigatorInterface<*>) {

                val manager = (this@with.managerProvider ?: return)()

                val fragment = bone.fingertip?.sibling as? Fragment ?: return

                val transition = when (transaction) {
                    is Pushing -> FragmentTransaction.TRANSIT_FRAGMENT_OPEN
                    is Popping -> FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
                    is Replacing -> FragmentTransaction.TRANSIT_FRAGMENT_OPEN
                    else -> FragmentTransaction.TRANSIT_NONE
                }

                // make screenshot and place background due android strange behavior with nested fragments
                if (bone.transitionType != None) {
                    manager.fragments.lastOrNull { it.isVisible }?.freezeSnapshotAsBackground()
                }

                manager
                    .beginTransaction()
                    .setTransition(transition)
                    .apply {
                        transactionSetup?.invoke(this, fragment)
                    }
                    .replace(containerId, fragment)
                    .runOnCommit {
                        super.refreshUI()
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
