package pro.horovodovodo4ka.bones

import pro.horovodovodo4ka.bones.extensions.uuid
import java.lang.ref.WeakReference

interface BoneInterface {
    val sibling: BoneSibling<out Bone>?
}

interface BoneStateValue

data class Activeness(val value: Boolean) : BoneStateValue

data class Primacy(val value: Boolean) : BoneStateValue

data class Adoption(val isAdopted: Boolean) : BoneStateValue

/**
 * Bone class. Used for organize hierarchy and navigation inside that hierarchy.
 *
 * @constructor Creates new instance
 */
abstract class Bone(
    /**
     * Used for disabling activating this on when parent bone is activated. Useful when deferred activation is required. Example: ViewPager
     */
    var ignoreAutoActivation: Boolean = false,
    /**
     * Persists sibling linked to bone on bone deactivation. Often goes with *inner class* bones which captures it's sibling instance in [Bone.seed] property.
     */
    var persistSibling: Boolean = false
) : BoneInterface {

    //region internal API

    init {
        cleanupInstances()

        @Suppress("LeakingThis")
        instances.add(WeakReference(this))
    }

    companion object {
        private val instances = mutableSetOf<WeakReference<Bone>>()

        private fun cleanupInstances() {
            instances.retainAll { it.get() != null }
        }

        /**
         * Tries to fetch bone with specific id from bones repository.
         * Used to restore bones into siblings on configuration changes.
         * @return bone instance or null if not found
         */
        operator fun get(key: String): Bone? {
            cleanupInstances()
            return instances.find { it.get()?.id == key }?.get()
        }
    }

    //endregion

    /**
     * Unique ID used for identifying bones. Used for persisting on configuration changes etc
     */
    val id = String.uuid()

    /**
     * Marks bone as active or inactive. On activation bone creates it's sibling. On deactivation unlink it from self.
     * That means that after deactivation siblings still able work with bone but bone can't.
     * Activeness status also transferred to bone's descendants (behavior can be overridden by subclasses).
     */
    open var isActive: Boolean = false
        set(value) {
            if (field == value) return
            field = value

            syncSibling()

            onStateChange(Activeness(value))
            sibling?.onBoneStateChange(Activeness(value))

            descendantsStore.filter { !it.ignoreAutoActivation || !value }.forEach { it.isActive = value }
        }

    /**
     * Marks bone as primary inside parent's descendants
     */
    var isPrimary: Boolean = false
        set(value) {
            field = value

            onStateChange(Primacy(value))
            sibling?.onBoneStateChange(Primacy(value))
        }

    /**
     * Technical function realize functionality of creation siblings and detaching them from bone. **Must** be called on subclasses which overrides *isActive* property.
     */
    protected fun syncSibling() {
        when (isActive) {
            true -> {
                sibling = sibling ?: (overriddenSeed ?: seed)()
                sibling?.link(this)
            }
            false -> {
                if (!persistSibling) sibling = null
            }
        }
    }

    /**
     * Internal storage of descendants. Not private to allow subclasses to override behavior of transferring bone's activeness.
     */
    protected val descendantsStore = mutableSetOf<Bone>()

    /**
     * Parent bone of self. Null if bone is not in any hierarchy.
     */
    var parentBone: Bone? = null
        private set(value) {
            val oldValue = field

            if (oldValue != null && value == null) {
                onOrphaned()
                onStateChange(Adoption(false))
                sibling?.onBoneStateChange(Adoption(false))
            }

            field = value

            if (oldValue == null && value != null) {
                onAdopted()
                onStateChange(Adoption(true))
                sibling?.onBoneStateChange(Adoption(true))
            }
        }

    /**
     * Bone's sibling. Usually is some visual part of application (activity, fragment, view, widget). Created by [Bone.seed] method when bone becomes active.
     */
    override var sibling: BoneSibling<out Bone>? = null

    /**
     * Lambda which creates new instance of bone's sibling.
     * Must be overridden **or** property [persistSibling] must be set to *true* and [sibling] set manually.
     */
    open val seed: () -> BoneSibling<out Bone> = { throw NotImplementedError("Default seed do nothing: override it or set 'persistSibling = true' and set sibling manually") }
    private var overriddenSeed: (() -> BoneSibling<out Bone>)? = null

    /**
     * Stack of parent bones.
     */
    val parents: Array<Bone>
        get() {
            val ret = mutableListOf<Bone>()

            var bone = parentBone
            while (bone != null) {
                ret.add(bone)
                bone = bone.parentBone
            }

            return ret.toTypedArray()
        }

    /**
     * Adds descendant to this bone hierarchy. Also removes it from previous hierarchy.
     * Sets new bone's *parentBone* to self.
     * Also checks if bone already exist (using [equals] function). If so then transfers sibling and seed to old bone and replaces bone in sibling to old.
     *
     * @param bone bone been added as descendant.
     *
     * @see [parentBone]
     */
    fun add(bone: Bone) {
        val oldBone = descendantsStore.find { it == bone }
        when (oldBone) {
            null -> {
                bone.parentBone?.descendantsStore?.remove(bone)
                descendantsStore.add(bone)
                bone.parentBone = this
                if (!bone.ignoreAutoActivation || !isActive) bone.isActive = isActive
                bone.notifyChange()
            }
            else -> {
                with(oldBone) {
                    overriddenSeed = bone.overriddenSeed ?: bone.seed
                    sibling = bone.sibling
                    syncSibling()
                    notifyChange()
                }
            }
        }
    }

    /**
     * Removes bone from descendants. Sets it's *parentBone* to null.
     *
     * @param bone bone to be removed.
     *
     * @see [parentBone]
     */
    fun remove(bone: Bone) {
        descendantsStore.remove(bone)
        bone.parentBone = null
        bone.notifyChange()
    }

    /**
     * Notifies sibling that something in bone data has been changed.
     * Also see [BoneSibling.refreshUI].
     */
    fun notifyChange() {
        sibling?.onBoneChanged()
        notifySubscribers()
    }

    /**
     * Same as [onBoneChanged] but for current bone and specific states, can be custom
     */
    protected open fun onStateChange(state: BoneStateValue) {}

    /**
     * Called just before when bone is removed from hierarchy - it become 'orphaned' will no have parent after this moment
     *
     * Also see [parentBone]
     */
    protected open fun onOrphaned() {}

    /**
     * Called just after when bone is added to hierarchy - it become 'adopted' and now has parent
     *
     * Also see [parentBone]
     */
    protected open fun onAdopted() {}

    // Callback-less linking

    private val subscribers = mutableSetOf<String>()

    private fun notifySubscribers() {
        with(subscribers) {
            retainAll { Bone[it] != null }
            forEach { Bone[it]?.onBoneChanged(this@Bone) }
        }
    }

    /**
     * Subscribes self to changes of other bone. When target bone is changed it calls [notifyChange] method. This causes call [onBoneChanged] on subscribers.
     *
     * @param source target bone
     */
    protected fun subscribe(source: Bone) {
        source.subscribers.add(id)
    }

    /**
     * Removes subscription to target bone changes.
     *
     * @see [subscribe]
     */
    protected fun unsubscribe(from: Bone) {
        from.subscribers.remove(id)
    }

    /**
     * Called when any of bones on which current bone is subscribed calls [notifyChange].
     *
     * @param bone target bone which state has been changed
     */
    protected open fun onBoneChanged(bone: Bone) {}

    // region Helpers and shorthands

    /**
     * Add multiple bones to self
     */
    protected fun add(vararg bones: Bone) {
        bones.forEach { add(it) }
    }

    /**
     * Subscribe on multiple bones.
     */
    protected fun subscribe(vararg bones: Bone) {
        bones.forEach { subscribe(it) }
    }

    /**
     * Add multiple bones to self and subscribe on them.
     */
    protected fun addAndSubscribe(vararg bones: Bone) {
        add(*bones)
        subscribe(*bones)
    }
    // endregion
}
