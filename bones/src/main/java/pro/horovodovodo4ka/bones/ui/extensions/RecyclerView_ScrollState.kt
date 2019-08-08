package pro.horovodovodo4ka.bones.ui.extensions

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.OnScrollListener

/**
 * Bind provided layout manager with [RecyclerView], and also sync [ScrollState] with recycler.
 * Used to store portion of UI state of sibling and restore it when recreate sibling.
 */
@Deprecated("Use layoutManager's `onSaveInstanceState` and `onRestoreInstanceState`")
fun <T : LayoutManager> RecyclerView.syncManager(scrollState: ScrollState, managerProvider: () -> T): T = managerProvider()
        .also {
            layoutManager = it

            post {
                scrollBy(scrollState.x, scrollState.y)

                addOnScrollListener(object : OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        scrollState.x += dx
                        scrollState.y += dy
                    }
                })
            }
        }

/**
 * Creates [LinearLayoutManager], bind with [RecyclerView], and also sync [ScrollState] with recycler.
 * Used to store portion of UI state of sibling and restore it when recreate sibling.
 */
@Deprecated("Use layoutManager's `onSaveInstanceState` and `onRestoreInstanceState`")
fun RecyclerView.syncLinear(context: Context?, scrollState: ScrollState): LinearLayoutManager =
        syncManager(scrollState) { LinearLayoutManager(context) }
