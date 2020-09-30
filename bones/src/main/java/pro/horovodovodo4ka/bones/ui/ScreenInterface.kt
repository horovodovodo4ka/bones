package pro.horovodovodo4ka.bones.ui

import pro.horovodovodo4ka.bones.Phalanx

/**
 * Semantic alias for fragments. Describes fragments which represents view that takes whole page/screen.
 */
interface ScreenInterface<T : Phalanx> : FragmentSibling<T>
