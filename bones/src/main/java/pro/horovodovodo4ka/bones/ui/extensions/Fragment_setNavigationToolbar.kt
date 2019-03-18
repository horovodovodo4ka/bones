package pro.horovodovodo4ka.bones.ui.extensions

import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import pro.horovodovodo4ka.bones.BoneSibling
import pro.horovodovodo4ka.bones.extensions.goBack

/**
 * Adds navigation icon to the toolbar and callback on click on it.
 * Used as shortcut with fragments which are bone siblings.
 */
fun <T> T.addNavigationToToolbar(toolbar: Toolbar, iconResId: Int) where T : Fragment, T : BoneSibling<*> {
    toolbar.navigationIcon = ResourcesCompat.getDrawable(toolbar.context.resources, iconResId, null)
    toolbar.setNavigationOnClickListener { bone.goBack() }
}

/**
 * Removes navigation icon from the toolbar and click callback.
 * Used as shortcut with fragments which are bone siblings.
 */
fun <T> T.removeNavigationFromToolbar(toolbar: Toolbar) where T : Fragment, T : BoneSibling<*> {
    toolbar.navigationIcon = null
    toolbar.setNavigationOnClickListener(null)
}