package pro.horovodovodo4ka.bones

interface NavigationBone : BoneInterface {
    /**
     * Used to show new bone in hierarchy depending on bone's context. For example finger will push this bone and spine present it.
     */
    fun show(bone: Bone)

    /**
     * Used to go back in hierarchy depending on bone's context. For example finger will pop and spine will dismiss.
     */
    fun goBack(): Boolean
}
