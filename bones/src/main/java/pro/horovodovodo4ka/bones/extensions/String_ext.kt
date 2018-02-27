package pro.horovodovodo4ka.bones.extensions

import java.util.UUID

/**
 * Unique id generator for bones
 */
fun String.Companion.uuid() : String = UUID.randomUUID().toString()