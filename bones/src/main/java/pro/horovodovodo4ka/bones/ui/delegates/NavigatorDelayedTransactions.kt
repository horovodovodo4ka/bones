package pro.horovodovodo4ka.bones.ui.delegates

import android.util.Log

private val handlers = mutableListOf<() -> Unit>()

interface NavigatorDelayedTransactions {
    companion object {
        fun executePendingTransactions() {
            handlers.forEach { it() }
            handlers.clear()
        }
    }
}

fun NavigatorDelayedTransactions.post(block: () -> Unit) {
    Log.d("Bones", "Delayed transaction detected. Be careful using this feature! Multiple transactions can cause glitches and fluent bugs.")
    handlers.add(block)
}
