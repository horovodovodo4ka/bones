package pro.horovodovodo4ka.bones.ui.delegates

import android.os.Build
import android.util.Log
import android.view.View.GONE
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import pro.horovodovodo4ka.bones.BoneSibling
import pro.horovodovodo4ka.bones.Spine
import pro.horovodovodo4ka.bones.Spine.TransitionType
import pro.horovodovodo4ka.bones.Spine.TransitionType.Dismissing
import pro.horovodovodo4ka.bones.Spine.TransitionType.Presenting
import pro.horovodovodo4ka.bones.ui.SpineNavigatorInterface
import pro.horovodovodo4ka.bones.ui.helpers.BoneAppCompatDialogFragment
import pro.horovodovodo4ka.bones.ui.helpers.BoneBottomSheetDialogFragment
import pro.horovodovodo4ka.bones.ui.helpers.BoneDialogFragment

/**
 * Delegate that implements default Spine navigation.
 * Uses support library.
 */
class SpineNavigator<T : Spine>(override val containerId: Int = android.R.id.content) : SpineNavigatorInterface<T>, NavigatorDelayedTransactions {
    override lateinit var bone: T
    override var managerProvider: (() -> FragmentManager)? = null
    override var transactionSetup: (FragmentTransaction.(targetFragment: Fragment) -> Unit)? = null

    override fun refreshUI() {

        val fromFragment = (bone.transitionType as? Dismissing)?.from?.sibling as? Fragment

        fun execute(bone: Spine, transaction: TransitionType) {
            with(bone.sibling as SpineNavigatorInterface<*>) {
                val manager = (this@with.managerProvider ?: return)()

                when (transaction) {
                    is Dismissing -> {
                        // 1) after restoring from state new sibling bound even if bone removed it's sibling, 2) if null then no restart happen and use old sibling
                        val realFromFragment = (transaction.from?.sibling as? Fragment ?: fromFragment)?.takeIf { !it.isDetached } ?: return

                        val idx = manager.fragments.indexOf(realFromFragment)

                        // waiting for androidx. 1.2.0 - animation playing on top of all views
                        if (idx < manager.fragments.lastIndex) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                realFromFragment.view?.elevation = -0.1f
                            } else {
                                realFromFragment.view?.visibility = GONE
                            }
                        }

                        manager
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .remove(realFromFragment)
                            .runOnCommit {
                                super.refreshUI()
                                (realFromFragment as? BoneSibling<*>)?.refreshUI()
                                transaction.to?.sibling?.refreshUI()
                            }
                            .commitNow()

                    }
                    is Presenting -> {
                        val toRealFragment = transaction.to?.sibling as? Fragment ?: return

                        manager
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .apply {
                                transactionSetup?.invoke(this, toRealFragment)
                            }
                            .add(fragment = toRealFragment, to = containerId)
                            .runOnCommit {
                                super.refreshUI()
                                transaction.from?.sibling?.refreshUI()
                                transaction.to.sibling?.refreshUI()
                            }
                            .commitNow()
                    }
                    else -> {
                        manager.fragments.forEach {
                            manager.beginTransaction().remove(it).commitNow()
                        }
                        bone.vertebrae.forEach { b ->
                            manager
                                .beginTransaction()
                                .add(fragment = b.sibling as Fragment, to = containerId)
                                .runOnCommit {
                                    b.sibling?.refreshUI()
                                }
                                .commitNow()
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

private fun FragmentTransaction.add(fragment: Fragment, to: Int): FragmentTransaction {
    when (fragment) {
        is BoneDialogFragment<*> -> add(fragment, fragment.tag)
        is BoneAppCompatDialogFragment<*> -> add(fragment, fragment.tag)
        is BoneBottomSheetDialogFragment<*> -> add(fragment, fragment.tag)
        is DialogFragment -> add(to, fragment)
            .also {
                Log.w(
                    "Bones",
                    "Using of DialogFragment (or it's descendant) is not recommended because it doesn't handle cancelling as dismiss. Use BoneDialogFragment instead."
                )
            }
        else -> add(to, fragment)
    }
    return this
}