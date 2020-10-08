package pro.horovodovodo4ka.bones.sample.ui.helpers

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlin.DeprecationLevel.ERROR

class SnapshotFragment @Deprecated("Used before 29 android sdk", level = ERROR) constructor() : Fragment() {

    companion object {

        @Deprecated("Used before 29 android sdk", level = ERROR)
        fun snapshotFrom(view: View): SnapshotFragment {
            TODO()
        }
    }

    private var background: BitmapDrawable? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.background = this.background
    }
}