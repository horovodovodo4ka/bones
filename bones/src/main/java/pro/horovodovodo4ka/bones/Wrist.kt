package pro.horovodovodo4ka.bones

import pro.horovodovodo4ka.bones.Wrist.TransitionType.DECREMENTING
import pro.horovodovodo4ka.bones.Wrist.TransitionType.INCREMENTING
import pro.horovodovodo4ka.bones.Wrist.TransitionType.NONE

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
    vararg finger: Bone) : Bone() {

    enum class TransitionType {
        NONE,
        INCREMENTING,
        DECREMENTING;
    }

    /**
     * Used to determine which type of changes is going now on stack.
     */
    var transitionType = NONE
        private set

    /**
     * Bones which represents tabs. Can not be modified after construction.
     */
    val fingers: List<Bone> = finger.asList()

    init {
        fingers.forEach { add(it) }
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

            activeBone.isActive = false
            field = value
            activeBone.isActive = isActive

            transitionType = if (oldIndex > value) DECREMENTING else INCREMENTING
            sibling?.refreshUI(fingers[oldIndex], fingers[value])
            transitionType = NONE

            listeners.forEach { it.fingerSwitched(oldIndex, field) }
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
        fun fingerSwitched(from: Int, to: Int)
    }

    private val listeners: List<Listener>
        get() = (parents + this).mapNotNull { it as? Listener }

    // endregion
}