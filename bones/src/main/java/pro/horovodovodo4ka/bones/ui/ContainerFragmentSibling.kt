package pro.horovodovodo4ka.bones.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Spine
import pro.horovodovodo4ka.bones.Wrist
import pro.horovodovodo4ka.bones.ui.delegates.FingerNavigator
import pro.horovodovodo4ka.bones.ui.delegates.SpineNavigator
import pro.horovodovodo4ka.bones.ui.delegates.WristNavigator

/**
 * Base interface for fragments which are siblings of bone with navigation capabilities eg [Finger], [Spine], [Wrist].
 * Describes place where nested siblings will be inserted.
 * Uses support library component by default [FragmentManager]
 */
interface ContainerFragmentSibling<T : Bone> : FragmentSibling<T> {
    /**
     * ID of view in which content of nested siblings will be inserted
     */
    val containerId: Int

    /**
     * Lambda that provides FragmentManager for use with transitions.
     * Primarily used by navigation delegates eg [SpineNavigator], [WristNavigator], [FingerNavigator]
     */
    var managerProvider: (() -> FragmentManager)?

    /**
     * Lambda called for additional transaction setup
     */
    var transactionSetup: (FragmentTransaction.(targetFragment: Fragment) -> Unit)?
}
