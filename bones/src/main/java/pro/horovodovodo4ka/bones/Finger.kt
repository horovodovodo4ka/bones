package pro.horovodovodo4ka.bones

import pro.horovodovodo4ka.bones.Finger.TransitionType.None
import pro.horovodovodo4ka.bones.Finger.TransitionType.Popping
import pro.horovodovodo4ka.bones.Finger.TransitionType.Pushing
import pro.horovodovodo4ka.bones.Finger.TransitionType.Replacing

/**
 * Represent navigation concept called **stack**. It means that this bone have stack of bones topmost of which is active when this bone is active.
 * Provides method for navigating thru that stack. Allows push, pop and replace mechanics.
 *
 * @constructor Creates new instance. Can retrieve root bone as parameter.
 */
abstract class Finger(
    /**
     * If passed represents bone used as root
     */
    rootPhalanx: Bone? = null
) : Bone(), NavigationBone {

    sealed class TransitionType {
        object None : TransitionType()
        data class Pushing(val from: Bone?, val to: Bone?) : TransitionType()
        data class Popping(val from: Bone?, val to: Bone?) : TransitionType()
        data class Replacing(val from: Bone?, val to: Bone?) : TransitionType()
    }

    /**
     * Used to determine which type of changes is going now on stack.
     */
    var transitionType: TransitionType = None
        private set

    private val stack = mutableListOf<Bone>()

    init {
        rootPhalanx?.also {
            add(it)
            stack.add(it)
        }

        isPrimary = true
        fingertip?.isPrimary = true
    }

    override fun onStateChange(state: BoneStateValue) {
        super.onStateChange(state)

        if (state is Primacy) {
            fingertip?.isPrimary = isPrimary
        }
    }

    /**
     * Bones on stack. It's not the same as descendants. Any stack bone is descendant, but not any descendant is stack bone.
     */
    val phalanxes: Array<Bone>
        get() = stack.toTypedArray()

    /**
     * Topmost bone of the stack.
     */
    val fingertip: Bone?
        get() = stack.lastOrNull()

    /**
     * Root (aka first) bone in the stack.
     */
    val rootPhalanx: Bone?
        get() = stack.firstOrNull()

    /**
     * Adds new bone to the stack and makes it active (if finger is active). Old one becomes inactive.
     *
     * @param bone bone to bee added
     */
    fun push(bone: Bone) {
        if (stack.contains(bone)) return

        val last = fingertip

        last?.isPrimary = false
        last?.isActive = false

        add(bone)
        stack.add(bone)

        bone.isActive = isActive
        bone.isPrimary = isPrimary

        transitionType = Pushing(last, fingertip)
        sibling?.refreshUI()

        listeners.forEach { it.phalanxSwitched(transitionType) }
        transitionType = None
    }

    /**
     * Removes topmost (fingertip) bone from the stack. New fingertip becomes active (if finger is active).
     * If there is only one bone in stack do nothing.
     */
    fun pop() {
        if (stack.size < 2) return
        popTo(stack[stack.lastIndex - 1])
    }

    /**
     * Pops all bones in stack except root. Makes it active if finger is active.
     * If there is only one bone in stack do nothing.
     */
    fun popToRoot() {
        if (stack.size < 2) return
        popTo(stack.first())
    }

    /**
     * Find passed bone in stack, pops all bones above it in stack and makes this bone active if finger active.
     * If there is only one bone in stack do nothing.
     *
     * @param phalanx bone to find and pop to
     */
    fun popTo(phalanx: Bone) {
        if (stack.size < 2) return

        val oldBone = fingertip!!
        val idx = stack.indexOf(phalanx)

        if (idx < 0) return

        val removed = stack.subList(idx + 1, stack.size).toTypedArray()
        val reserved = stack.subList(0, idx + 1).toTypedArray()

        stack.clear()
        stack.addAll(reserved)
        stack.last().isActive = isActive
        stack.last().isPrimary = isPrimary

        transitionType = Popping(oldBone, phalanx)
        sibling?.refreshUI()

        removed.forEach {
            it.isPrimary = false
            it.isActive = false
            remove(it)
        }

        listeners.forEach { it.phalanxSwitched(transitionType) }
        transitionType = None
    }

    /**
     * Find passed bone in stack (if not specified then takes fingertip), removes it and all bones above in the stack and places new bone on it's place.
     * Do nothing if stack is empty.
     *
     * @param phalanx bone to replace, if null then fingertip will be replaced
     * @param with bone replacing old one
     *
     * @see [Finger.popTo]
     */
    fun replace(phalanx: Bone? = null, with: Bone) {
        if (stack.size < 1) return

        val oldBone = phalanx ?: fingertip!!
        val idx = stack.indexOf(oldBone)

        if (idx < 0) return

        val removed = stack.subList(idx, stack.size).toTypedArray()
        val reserved = stack.subList(0, idx).toTypedArray()

        stack.clear()
        stack.addAll(reserved)
        add(with)
        stack.add(with)
        with.isActive = isActive
        with.isPrimary = isPrimary

        transitionType = Replacing(oldBone, with)
        sibling?.refreshUI()

        removed.forEach {
            it.isPrimary = false
            it.isActive = false
            remove(it)
        }

        listeners.forEach { it.phalanxSwitched(transitionType) }
        transitionType = None
    }

    /**
     * Marks current finger and it's fingertip as active.
     *
     * @see [Bone.isActive]
     */
    override var isActive: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            syncSibling()
            fingertip?.isActive = value
            descendantsStore
                .subtract(stack)
                .filter { !it.ignoreAutoActivation || !value }
                .forEach { it.isActive = value }
        }

    // region Interactive

    /**
     * Used to notify parent bones about finger stack changes.
     */
    interface Listener {
        fun phalanxSwitched(transition: TransitionType)
    }

    private val listeners: List<Listener>
        get() = (parents + this).mapNotNull { it as? Listener }

    // endregion

    // region NavigationBone

    /**
     * Pushes new bone in stack
     *
     * @see [NavigationBone.show]
     * @see [Finger.push]
     */
    override fun show(bone: Bone) {
        push(bone)
    }

    /**
     * Tries to pop fingertip from stack.
     *
     * @return false if popping was failed
     *
     * @see [NavigationBone.goBack]
     * @see [Finger.pop]
     */
    override fun goBack(): Boolean {
        if (stack.size < 2) return false
        pop()
        return true
    }

    // endregion
}