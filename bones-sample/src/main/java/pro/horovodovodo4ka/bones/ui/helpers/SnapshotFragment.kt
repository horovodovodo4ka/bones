package pro.horovodovodo4ka.bones.ui.helpers

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pro.horovodovodo4ka.bones.ui.extensions.takeScreenshot

class SnapshotFragment : Fragment() {
    companion object {
        fun snapshotFrom(view: View) : SnapshotFragment {
            val bg = BitmapDrawable(Resources.getSystem(), view.takeScreenshot())
            return SnapshotFragment().also { it.background = bg }
        }
    }

    private var background: BitmapDrawable? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View(context)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.background = this.background
    }
}