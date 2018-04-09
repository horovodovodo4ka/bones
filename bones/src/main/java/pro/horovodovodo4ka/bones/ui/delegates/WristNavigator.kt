package pro.horovodovodo4ka.bones.ui.delegates

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.R
import pro.horovodovodo4ka.bones.Wrist
import pro.horovodovodo4ka.bones.Wrist.TransitionType.DECREMENTING
import pro.horovodovodo4ka.bones.Wrist.TransitionType.INCREMENTING
import pro.horovodovodo4ka.bones.Wrist.TransitionType.NONE
import pro.horovodovodo4ka.bones.ui.WristNavigatorInterface
import pro.horovodovodo4ka.bones.ui.extensions.freezeSnapshotAsBackground

/**
 * Delegate that implements default wrist navigation.
 * Uses support library.
 */
class WristNavigator<T : Wrist>(override val containerId: Int, private val animated: Boolean = false) : WristNavigatorInterface<T>, NavigatorDelayedTransactions {
    override lateinit var bone: T
    override var managerProvider: (() -> FragmentManager)? = null

    override fun refreshUI(from: Bone?, to: Bone?) {

        fun execute(bone: Wrist) {
            with(bone.sibling as WristNavigatorInterface<*>) {

                val manager = (this@with.managerProvider ?: return)()

                val tabFragment = bone.activeBone.sibling as? Fragment ?: return

                // make screenshot and place background due android strange behavior with nested fragments
                if (bone.transitionType != NONE) {
                    manager.fragments.lastOrNull { it.isVisible }?.freezeSnapshotAsBackground()
                }

                manager
                    .beginTransaction()
                    .apply {
                        if (animated) {
                            when (bone.transitionType) {
                                INCREMENTING -> setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out)
                                DECREMENTING -> setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out)
                                else -> Unit
                            }
                        }
                    }
                    .replace(containerId, tabFragment)
                    .runOnCommit {
                        super.refreshUI(from, to)
                        bone.activeBone.sibling?.refreshUI()
                    }
                    .commit()
            }
        }

        val manager = (managerProvider ?: return)()

        if (manager.isStateSaved) {
            post {
                execute(bone)
            }
        } else {
            execute(bone)
        }
    }
}
