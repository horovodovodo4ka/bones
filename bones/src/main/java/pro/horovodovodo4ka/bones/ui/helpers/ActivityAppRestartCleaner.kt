package pro.horovodovodo4ka.bones.ui.helpers

import android.os.Bundle
import android.support.v4.app.FragmentManager

/**
 * Simple interface which destroy all fragments loaded from bundle.
 * We must do it when application died but activity has saved data which leads to crash when restoring bones hierarchy -
 * there are just no bones any more, we must create new.
 */
interface ActivityAppRestartCleaner {
    fun getSupportFragmentManager(): FragmentManager
    fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        getSupportFragmentManager().apply {
            fragments.forEach {
                beginTransaction().remove(it).commitNow()
            }
        }
    }
}