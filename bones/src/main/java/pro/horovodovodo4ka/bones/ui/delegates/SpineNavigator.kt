package pro.horovodovodo4ka.bones.ui.delegates

import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Spine
import pro.horovodovodo4ka.bones.Spine.TransitionType.DISMISSING
import pro.horovodovodo4ka.bones.Spine.TransitionType.NONE
import pro.horovodovodo4ka.bones.Spine.TransitionType.PRESENTING
import pro.horovodovodo4ka.bones.ui.SpineNavigatorInterface
import pro.horovodovodo4ka.bones.ui.extensions.freezeSnapshotAsBackground

/**
 * Delegate that implements default Spine navigation.
 * Uses support library.
 */
class SpineNavigator<T : Spine> : SpineNavigatorInterface<T> {
    override val containerId: Int = android.R.id.content
    override lateinit var bone: T
    override var managerProvider: (() -> FragmentManager)? = null

    override fun refreshUI(from: Bone?, to: Bone?) {

        val manager = (managerProvider ?: return)()
        val fromFragment = from?.sibling
        val toFragment = to?.sibling

        when {
            toFragment is DialogFragment && bone.transitionType == PRESENTING -> {
                toFragment.show(manager, null)
                super.refreshUI(from, to)
                bone.skull.sibling?.refreshUI()
            }

            fromFragment is DialogFragment && bone.transitionType == DISMISSING -> {
                fromFragment.dismiss()
                super.refreshUI(from, to)
                bone.skull.sibling?.refreshUI()
            }

            toFragment is Fragment -> {
                val transition = when (bone.transitionType) {
                    PRESENTING -> FragmentTransaction.TRANSIT_FRAGMENT_OPEN
                    DISMISSING -> FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
                    else -> FragmentTransaction.TRANSIT_NONE
                }

                // make screenshot and place background due android strange behavior with nested fragments
                if (bone.transitionType != NONE) {
                    manager.fragments.lastOrNull { it.isVisible }?.freezeSnapshotAsBackground()
                }

                manager
                    .beginTransaction()
                    .setTransition(transition)
                    .replace(containerId, toFragment)
                    .runOnCommit {
                        super.refreshUI(from, to)
                        bone.skull.sibling?.refreshUI()
                    }
                    .commit()
            }
        }
    }
}

fun Spine.attachTo(fragmentManager: FragmentManager) {
    fragmentManager
        .beginTransaction()
        .replace(android.R.id.content, skull.sibling as Fragment)
        .runOnCommit {
            skull.sibling?.refreshUI()
        }
        .commit()

}

fun Spine.detachFrom(fragmentManager: FragmentManager) {
    fragmentManager
        .beginTransaction()
        .remove(skull.sibling as Fragment)
        .commit()
}