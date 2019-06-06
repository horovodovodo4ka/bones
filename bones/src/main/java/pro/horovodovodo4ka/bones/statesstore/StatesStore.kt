package pro.horovodovodo4ka.bones.statesstore

import android.os.Bundle

const val STATE_STORABLE_ID_KEY = "STATE_STORABLE_ID_KEY"

@Suppress("UNCHECKED_CAST")
object StatesStore {

    private val holder = HashMap<String, Any?>()

    private operator fun set(key: String, value: Any?) {
        holder[key] = value
    }

    private operator fun <T> get(key: String): T? = holder[key].also { del(key) } as? T

    private fun del(key: String) {
        holder.remove(key)
    }

    fun pinState(bundle: Bundle?, key: String) {
        if (bundle == null) return
        bundle.putString(STATE_STORABLE_ID_KEY, key)
        set(key, null)
    }

    fun <T> saveIfPinned(key: String, what: (T) -> Unit) {
        if (!holder.containsKey(key)) return
        set(key, what)
    }

    fun <T> restorePinnedState(bundle: Bundle?, context: T): String? {
        if (bundle == null) return null
        val key = bundle.getString(STATE_STORABLE_ID_KEY) ?: return null
        val state: (T) -> Unit = get(key) ?: return null
        state(context)
        return key
    }

    fun clearPin(key: String) = del(key)

    fun clear() = holder.clear()
}
