package pro.horovodovodo4ka.bones

import pro.horovodovodo4ka.bones.Spine.TransitionType.DISMISSING
import pro.horovodovodo4ka.bones.Spine.TransitionType.NONE
import pro.horovodovodo4ka.bones.Spine.TransitionType.PRESENTING

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

    enum class TransitionType {
        NONE,
        PRESENTING,
        DISMISSING;
    }

    /**
     * Used to determine which type of changes is going now on stack.
     */
    var transitionType = NONE
        private set

    private val stack = ArrayList<Bone>(listOf(root)).also { add(root) }

    /**
     * Topmost bone of stack.
     */
    val skull: Bone
        get() = stack.last()

    val vertebrae: Array<Bone>
        get() = stack.toTypedArray()

    /**
     * Adds bone to stack. It becomes skull
     *
     * @param bone new bone
     */
    fun present(bone: Bone) {
        if (stack.contains(bone)) return

        val last = skull

        add(bone)
        stack.add(bone)
        bone.isActive = isActive

        transitionType = PRESENTING
        sibling?.refreshUI(last, skull)
        transitionType = NONE

        listeners.forEach { it.boneSwitched(last, skull, PRESENTING) }
    }

    /**
     * Removes bone and all bones above it in stack. If not specified then skull dismissed.
     *
     * @param bone bone to be removed
     */
    fun dismiss(bone: Bone? = null) {
        val target = bone ?: stack.last()
        val idx = stack.indexOf(target)
        if (idx < 1) return

        val removed = stack.subList(idx, stack.size).toTypedArray()
        val reserved = stack.subList(0, idx).toTypedArray()

        stack.clear()
        stack.addAll(reserved)
        stack.last().isActive = isActive

        transitionType = DISMISSING
        sibling?.refreshUI(target, skull)
        transitionType = NONE

        removed.forEach {
            it.isActive = false
            remove(it)
        }

        listeners.forEach { it.boneSwitched(target, skull, DISMISSING) }
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
        fun boneSwitched(from: Bone, to: Bone, type: TransitionType)
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
