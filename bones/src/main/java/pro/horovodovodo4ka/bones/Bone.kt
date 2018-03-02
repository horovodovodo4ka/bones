package pro.horovodovodo4ka.bones

import android.support.annotation.CallSuper
import pro.horovodovodo4ka.bones.extensions.uuid
import java.lang.ref.WeakReference

interface BoneInterface {
    val sibling: BoneSibling<out Bone>?
}

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
        private val instances = ArrayList<WeakReference<Bone>>()

        private fun cleanupInstances() {
            for ((index, instance) in instances.withIndex().reversed()) {
                if (instance.get() == null) instances.removeAt(index)
            }
        }

        /**
         * Tries to fetch bone with specific id from bones repository.
         * Used to restore bones into siblings on configuration changes.
         * @return bone instance or null if not found
         */
        operator fun get(key: String): Bone? {
            cleanupInstances()
            return instances.firstOrNull { it.get()?.id == key }?.get()
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
            descendantsStore.filter { !it.ignoreAutoActivation || !value }.forEach { it.isActive = value }
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
    protected val descendantsStore = HashSet<Bone>()

    /**
     * Parent bone of self. Null if bone is not in any hierarchy.
     */
    var parentBone: Bone? = null
        private set

    /**
     * Bone's sibling. Usually is some visual part of application (activity, fragment, view, widget). Created by [Bone.seed] method when bone becomes active.
     * Tip: bone can be inner class of it's sibling (view for example) so in *seed* we can return *this* instance of view.
     */
    override var sibling: BoneSibling<out Bone>? = null

    /**
     * Lambda which creates new instance of bone's sibling.
     * Must be overridden **or** property [persistSibling] must be set to *true* and [sibling] set manually.
     *
     * @see [sibling]
     * @see [persistSibling]
     */
    open val seed: () -> BoneSibling<out Bone> = { throw NotImplementedError("Default seed do nothing: override it or set 'persistSibling = true' and set sibling manually") }
    private var overriddenSeed: (() -> BoneSibling<out Bone>)? = null

    /**
     * Stack of parent bones.
     */
    val parents: Array<Bone>
        get() {
            val ret = ArrayList<Bone>()

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
                bone.parentBone?.remove(bone)
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
     * Also see [BoneSibling.refreshUI]
     */
    fun notifyChange() {
        sibling?.onBoneChanged()
    }
}

