package pro.horovodovodo4ka.bones.ui.delegates

import pro.horovodovodo4ka.bones.Phalanx
import pro.horovodovodo4ka.bones.ui.ScreenInterface
/**
 * Simple delegate that implements bone holder.
 * Semantically used for fragments representing whole screen (page).
 */
class Page<T : Phalanx> : ScreenInterface<T> {
    override lateinit var bone: T
}