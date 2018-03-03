package pro.horovodovodo4ka.bones.ui.delegates

import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Spine
import pro.horovodovodo4ka.bones.Spine.TransitionType.DISMISSING
import pro.horovodovodo4ka.bones.Spine.TransitionType.PRESENTING
import pro.horovodovodo4ka.bones.ui.SpineNavigatorInterface

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
        if (manager.isStateSaved) return

        val fromFragment = from?.sibling as? Fragment
        val toFragment = to?.sibling as? Fragment

        when (bone.transitionType) {
            PRESENTING -> {
                manager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .also {
                        when (toFragment) {
                            is DialogFragment -> it.add(toFragment, null)
                            else -> it.add(containerId, toFragment)
                        }
                    }
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
                // Rebuild all spine stack. Weird, I know.
                val fragments = bone.vertebrae.mapNotNull { it.sibling as? Fragment }

                manager.fragments.forEach {
                    manager
                        .beginTransaction()
                        .also { transaction ->
                            transaction.remove(it)
                        }
                        .commitNow()
                }

                fragments.forEach {
                    manager
                        .beginTransaction()
                        .also { transaction ->
                            when (it) {
                                is DialogFragment -> transaction.add(it, null)
                                else -> transaction.add(containerId, it)
                            }
                        }
                        .commitNow()
                }

                super.refreshUI(from, to)
                bone.skull.sibling?.refreshUI()
            }
        }
    }
}
