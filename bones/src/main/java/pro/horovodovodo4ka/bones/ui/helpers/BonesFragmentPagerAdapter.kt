package pro.horovodovodo4ka.bones.ui.helpers

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import pro.horovodovodo4ka.bones.Bone

/**
 * Wrapper for collection of bones targeted on optimized use with PagerAdapter.
 * Used as is
 */
class BonesFragmentPagerAdapter(private val fragmentManager: FragmentManager, private val tabs: List<Pair<String, Bone>>, behavior: Int = BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) :
    FragmentPagerAdapter(fragmentManager, behavior) {
    override fun getItem(position: Int): Fragment = tabs[position].second.also { it.isActive = true }.sibling as Fragment
    override fun getPageTitle(position: Int): CharSequence? = tabs[position].first
    override fun getCount(): Int = tabs.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        val fragment = tabs[position].second.sibling as? Fragment ?: return
        fragmentManager.beginTransaction().remove(fragment).commitNow()
        tabs[position].second.isActive = false
    }
}
