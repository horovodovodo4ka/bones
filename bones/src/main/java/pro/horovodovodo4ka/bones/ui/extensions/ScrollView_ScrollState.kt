package pro.horovodovodo4ka.bones.ui.extensions

import android.widget.ScrollView

class ScrollState(var x: Int = 0, var y: Int = 0)

/**
 * Sync [ScrollState] value with view's scroll and vice versa. Used to store portion of UI state of sibling and restore it when recreate sibling.
 */
fun ScrollView.sync(scrollState: ScrollState) {
    post {
        scrollTo(scrollState.x, scrollState.y)
    }

    viewTreeObserver.addOnScrollChangedListener {
        scrollState.x = scrollX
        scrollState.y = scrollY
    }
}