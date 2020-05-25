package pro.horovodovodo4ka.bones

import pro.horovodovodo4ka.bones.Spine.TransitionType.Dismissing
import pro.horovodovodo4ka.bones.Spine.TransitionType.None
import pro.horovodovodo4ka.bones.Spine.TransitionType.Presenting

/**
 * Represents mechanic of modal screens. Provides methods for presenting and dismissing.
 * As [Finger] contains stack of presented bones, where only one topmost bone called skull is active when Spine is active.
 */
abstract class Spine(
    /**
     * Represents root bone of spine stack. Required.
     */
    root: Bone
) : Bone(), NavigationBone {

    sealed class TransitionType {
        object None : TransitionType()
        data class Presenting(val from: Bone?, val to: Bone?) : TransitionType()
        data class Dismissing(val from: Bone?, val to: Bone?) : TransitionType()
    }

    /**
     * Used to determine which type of changes is going now on stack.
     */
    var transitionType: TransitionType = None
        private set

    private val stack = mutableListOf(root).also { add(root) }

    /**
     * Topmost bone of stack.
     */
    val skull: Bone
        get() = stack.last()

    val vertebrae: Array<Bone>
        get() = stack.toTypedArray()

    init {
        isPrimary = true
        skull.isPrimary = true
    }

    override fun onStateChange(state: BoneStateValue) {
        super.onStateChange(state)

        if (state is Primacy) {
            skull.isPrimary = isPrimary
        }
    }

    /**
     * Adds bone to stack. It becomes skull
     *
     * @param bone new bone
     */
    fun present(bone: Bone) {
        if (stack.contains(bone)) return

        val last = skull

        last.isPrimary = false

        add(bone)
        stack.add(bone)

        bone.isActive = isActive
        bone.isPrimary = isPrimary

        transitionType = Presenting(last, skull)
        sibling?.refreshUI()
        transitionType = None

        listeners.forEach { it.boneSwitched(Presenting(last, skull)) }
    }

    /**
     * Removes bone and all bones above it in stack. If not specified then skull dismissed.
     *
     * @param bone bone to be removed
     * @param dismissOverlapping if true then dismiss also bones over current. Default is **false**
     */
    fun dismiss(bone: Bone? = null, dismissOverlapping: Boolean = false) {
        val target = bone ?: stack.last()
        val idx = stack.indexOf(target)
        if (idx < 1) return

        val removed: List<Bone>
        if (dismissOverlapping) {
            removed = stack.subList(idx, stack.size)
            val reserved = stack.subList(0, idx)

            stack.clear()
            stack.addAll(reserved)
        } else {
            removed = listOf(target)
            stack.remove(target)
        }

        skull.isActive = isActive
        skull.isPrimary = isPrimary

        transitionType = Dismissing(target, skull)
        sibling?.refreshUI()
        transitionType = None

        removed.forEach {
            it.isPrimary = false
            it.isActive = false
            remove(it)
        }

        listeners.forEach { it.boneSwitched(Dismissing(target, skull)) }
    }

    /**
     * Activates wrist and it's skull
     *
     * @see [Bone.isActive]
     */
    override var isActive: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            syncSibling()
            stack.forEach { it.isActive = value }
            descendantsStore
                .subtract(stack)
                .filter { !it.ignoreAutoActivation || !value }
                .forEach { it.isActive = value }
        }

    // region Interactive

    /**
     * Used to notify parent bones about spine navigation actions.
     */
    interface Listener {
        fun boneSwitched(transition: TransitionType)
    }

    private val listeners: List<Listener>
        get() = (parents + this).mapNotNull { it as? Listener }

    // endregion

    // region NavigationBone

    /**
     * Presents new bone
     *
     * @see [NavigationBone.show]
     * @see [Spine.present]
     */
    override fun show(bone: Bone) {
        present(bone)
    }

    /**
     * Tries to dismiss skull
     *
     * @return false if dismissing was failed
     *
     * @see [NavigationBone.goBack]
     * @see [Spine.dismiss]
     */
    override fun goBack(): Boolean {
        if (stack.size < 2) return false
        dismiss()
        return true
    }

    // endregion
}
