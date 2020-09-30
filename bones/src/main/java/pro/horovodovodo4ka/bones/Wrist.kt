package pro.horovodovodo4ka.bones

import pro.horovodovodo4ka.bones.Wrist.TransitionType.Decrementing
import pro.horovodovodo4ka.bones.Wrist.TransitionType.Incrementing
import pro.horovodovodo4ka.bones.Wrist.TransitionType.None

/**
 * Represents navigation concept called **tabs**. It means that bone have children bones called **fingers**.
 * Bone realize mechanics to switch between that fingers. Only one finger is active at a time.
 *
 * @constructor Creates new instance with tabs passed as parameter
 */
abstract class Wrist(
    /**
     * Bones which represents tabs. Can not be modified after construction.
     */
    vararg finger: Bone
) : Bone() {

    sealed class TransitionType {
        object None : TransitionType()
        data class Incrementing(val from: Bone?, val to: Bone?) : TransitionType()
        data class Decrementing(val from: Bone?, val to: Bone?) : TransitionType()
    }

    /**
     * Used to determine which type of changes is going now on stack.
     */
    var transitionType: TransitionType = None
        private set

    /**
     * Bones which represents tabs. Can not be modified after construction.
     */
    val fingers: List<Bone> = finger.asList()

    init {
        fingers.forEach { add(it) }

        isPrimary = true
        activeBone.isPrimary = true
    }

    override fun onStateChange(state: BoneStateValue) {
        super.onStateChange(state)

        if (state is Primacy) {
            activeBone.isPrimary = isPrimary
        }
    }

    /**
     * Index of currently active tab.
     *
     * @see [Wrist.activeBone]
     */
    var activeBoneIndex: Int = 0
        set(value) {
            if (field == value) return

            val oldIndex = activeBoneIndex
            val oldBone = activeBone

            activeBone.isPrimary = false
            activeBone.isActive = false

            field = value

            activeBone.isActive = isActive
            activeBone.isPrimary = isPrimary

            transitionType = if (oldIndex > value) Decrementing(oldBone, activeBone) else Incrementing(oldBone, activeBone)
            sibling?.refreshUI()

            listeners.forEach { it.fingerSwitched(transitionType) }
            transitionType = None
        }

    /**
     * Currently active bone
     *
     * @see [Wrist.activeBoneIndex]
     */
    var activeBone: Bone
        get() = fingers[activeBoneIndex]
        set(value) {
            activeBoneIndex = fingers.indexOf(value)
        }

    /**
     * marks wrist and it's active finger as active
     *
     * @see [Bone.isActive]
     */
    override var isActive: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            syncSibling()
            activeBone.isActive = value
            descendantsStore
                .subtract(fingers)
                .filter { !it.ignoreAutoActivation || !value }
                .forEach { it.isActive = value }
        }

    // region Interactive

    /**
     * Used to notify parent bones about active tab changes.
     */
    interface Listener {
        fun fingerSwitched(transition: TransitionType)
    }

    private val listeners: List<Listener>
        get() = (parents + this).mapNotNull { it as? Listener }

    // endregion
}
