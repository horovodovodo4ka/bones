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
     * ID of bone's sibling. Optional property used for binding visual parts (siblings) of bones when recreating view states of bones.
     * Example: in fragment with custom views which are bone's sibling and can be recreated, we must relink bones of those views back to their parent.
     * We can identify that views only by unique ID inside parent bone. This is really important to preserve callbacks and links to objects.
     *
     * @see [Bone.add]
     * @see [BoneSibling.link]
     */
    open val siblingId: Int = 0

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
     *
     * @see [Bone.sibling]
     */
    abstract val seed: () -> BoneSibling<out Bone>
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
     *
     * @param bone bone been added as descendant.
     * @param syncBySiblingId if *true* then tries to find descendant with the same *siblingId* and transfer seed from new bone to it instead of adding.
     *          Used with *inner class* bones which are created by their sibling. Useful for retain links, subscribers etc.
     *
     * @see [Bone.parentBone]
     */
    @CallSuper
    open fun add(bone: Bone, syncBySiblingId: Boolean = false) {
        if (syncBySiblingId) {
            descendantsStore
                .firstOrNull { it.siblingId > 0 && it.siblingId == bone.siblingId }
                ?.also {
                    it.overriddenSeed = bone.overriddenSeed ?: bone.seed
                    it.sibling = bone.sibling
                    it.syncSibling()
                    it.sibling?.onBoneChanged()
                    return
                }
        }

        bone.parentBone?.remove(bone)
        descendantsStore.add(bone)
        bone.parentBone = this
        if (!bone.ignoreAutoActivation || !isActive) bone.isActive = isActive
        bone.sibling?.onBoneChanged()
    }

    /**
     * Removes bone from descendants. Sets it's *parentBone* to null.
     * @param bone bone to be removed.
     *
     * @see [Bone.parentBone]
     */
    @CallSuper
    open fun remove(bone: Bone) {
        descendantsStore.remove(bone)
        bone.parentBone = null
    }

    /**
     * Notifies sibling that something in bone data has been changed.
     * Also see [BoneSibling.refreshUI]
     */
    fun notifyChange() {
        sibling?.onBoneChanged()
    }
}

