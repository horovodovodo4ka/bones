package pro.horovodovodo4ka.bones.ui.delegates

import android.annotation.SuppressLint
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Spine
import pro.horovodovodo4ka.bones.Spine.TransitionType.DISMISSING
import pro.horovodovodo4ka.bones.Spine.TransitionType.PRESENTING
import pro.horovodovodo4ka.bones.extensions.dismiss
import pro.horovodovodo4ka.bones.ui.SpineNavigatorInterface

/**
 * Delegate that implements default Spine navigation.
 * Uses support library.
 */
class SpineNavigator<T : Spine> : SpineNavigatorInterface<T> {
    override val containerId: Int = android.R.id.content
    override lateinit var bone: T
    override var managerProvider: (() -> FragmentManager)? = null

    @SuppressLint("CommitTransaction")
    override fun refreshUI(from: Bone?, to: Bone?) {

        val manager = (managerProvider ?: return)()
        if (manager.isStateSaved) return

        val fromFragment = from?.sibling as? Fragment
        val toFragment = to?.sibling as? Fragment

        when {
            toFragment is DialogFragment && bone.transitionType == PRESENTING -> {
                manager
                    .beginTransaction()
                    .runOnCommit {
                        toFragment.dialog.setOnDismissListener {
                            from?.dismiss()
                        }
                        toFragment.showsDialog = false
                        super.refreshUI(from, to)
                        bone.skull.sibling?.refreshUI()
                    }
                    .also {
                        toFragment.show(it, null)
                    }
            }

            fromFragment is DialogFragment && bone.transitionType == DISMISSING -> {
                fromFragment.dismiss()
                super.refreshUI(from, to)
                bone.skull.sibling?.refreshUI()
            }

            else -> {

//                // make screenshot and place background due android strange behavior with nested fragments
//                if (bone.transitionType != NONE) {
//                    fromFragment?.freezeSnapshotAsBackground()
//                }

                when (bone.transitionType) {
                    PRESENTING -> {
                        manager
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .add(containerId, toFragment as Fragment)
                            .runOnCommit {
                                super.refreshUI(from, to)
                                bone.skull.sibling?.refreshUI()
                            }
                            .commit()
                    }
                    DISMISSING -> {
                        manager
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .remove(fromFragment as Fragment)
                            .runOnCommit {
                                super.refreshUI(from, to)
                                bone.skull.sibling?.refreshUI()
                            }
                            .commit()
                    }
                    else -> {
                        // rebuild all spine stack
                        val fragments = bone.vertebrae
                            .mapNotNull { it.sibling as? Fragment }

                        fragments.forEach {
                            manager
                                .beginTransaction()
                                .also { transaction ->
                                    transaction.remove(it)
                                }
                                .commitNow()
                        }

                        fragments.forEachIndexed { index, fragment ->
                            manager
                                .beginTransaction()
                                .also { transaction ->
                                    transaction.add(containerId, fragment)
                                }
                                .commitNow()
                        }

                        super.refreshUI(from, to)
                        bone.skull.sibling?.refreshUI()
                    }
                }
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