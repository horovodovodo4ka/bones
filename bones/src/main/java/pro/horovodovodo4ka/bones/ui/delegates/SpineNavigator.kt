package pro.horovodovodo4ka.bones.ui.delegates

import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Spine
import pro.horovodovodo4ka.bones.Spine.TransitionType.DISMISSING
import pro.horovodovodo4ka.bones.Spine.TransitionType.PRESENTING
import pro.horovodovodo4ka.bones.ui.SpineNavigatorInterface
import pro.horovodovodo4ka.bones.ui.helpers.BoneDialogFragment

/**
 * Delegate that implements default Spine navigation.
 * Uses support library.
 */
class SpineNavigator<T : Spine>(containerId: Int = android.R.id.content) : SpineNavigatorInterface<T>, NavigatorDelayedTransactions {
    override val containerId: Int = containerId
    override lateinit var bone: T
    override var managerProvider: (() -> FragmentManager)? = null

    override fun refreshUI(from: Bone?, to: Bone?) {
        super.refreshUI(from, to)

        val fromFragment = from?.sibling as? Fragment
        val toFragment = to?.sibling as? Fragment

        fun execute(bone: Spine, transaction: Any) {
            with(bone.sibling as SpineNavigatorInterface<*>) {
                val manager = (this@with.managerProvider ?: return)()

                // 1) after restoring from state new sibling bound even if bone removed it's sibling, 2) if null then no restart happen and use old sibling
                val realFromFragment = from?.sibling as? Fragment ?: fromFragment

                when (transaction) {
                    PRESENTING -> {
                        manager
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .also {
                                when (toFragment) {
                                    is BoneDialogFragment<*> -> it.add(toFragment, null)
                                    is DialogFragment -> it.add(containerId, toFragment)
                                        .also {
                                            Log.w(
                                                "Bones",
                                                "Using of DialogFragment is not recommended because it doesn't handle cancelling as dismiss. Use BoneDialogFragment instead."
                                            )
                                        }
                                    else -> it.add(containerId, toFragment)
                                }
                            }
                            .commit()
                    }
                    DISMISSING -> {
                        manager
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .remove(realFromFragment)
                            .commit()
                    }
                    else -> {
                        manager.fragments.forEach {
                            manager.beginTransaction().remove(it).commitNow()
                        }
                        bone.vertebrae.forEach {
                            manager.beginTransaction().add(containerId, it.sibling as Fragment).commitNow()
                        }
                    }
                }
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
