package pro.horovodovodo4ka.bones.ui.helpers

import android.os.Bundle
import android.support.v4.app.FragmentManager

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