package pro.horovodovodo4ka.bones.ui.extensions

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener

data class RecyclerState(var position: Int = 0)

/**
 * Creates [LinearLayoutManager], bind with [RecyclerView], and also sync [RecyclerState] with recycler's position. Used to store portion of UI state of sibling and restore it when recreate sibling.
 */
fun RecyclerView.syncLinear(context: Context?, scrollState: RecyclerState): LinearLayoutManager {
    val manager = LinearLayoutManager(context)
    this.layoutManager = manager

    manager.scrollToPosition(scrollState.position)

    addOnScrollListener(object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            scrollState.position = manager.findFirstVisibleItemPosition()
        }
    })

    return manager
}