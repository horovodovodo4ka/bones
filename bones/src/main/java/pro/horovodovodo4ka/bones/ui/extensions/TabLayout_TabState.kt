package pro.horovodovodo4ka.bones.ui.extensions

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab

data class TabState(var tab: Int = 0)

/**
 *  Sync [TabState] value with [TabLayout] active tab and vice versa. Used to store portion of UI state of sibling and restore it when recreate sibling.
 */
fun TabLayout.sync(state: TabState) {
    this.getTabAt(state.tab)?.select()

    addOnTabSelectedListener(object : OnTabSelectedListener {
        override fun onTabSelected(tab: Tab) {
            state.tab = tab.position
        }

        override fun onTabUnselected(tab: Tab) {}
        override fun onTabReselected(tab: Tab) {}
    })
}