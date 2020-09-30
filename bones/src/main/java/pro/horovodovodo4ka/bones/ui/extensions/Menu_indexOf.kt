package pro.horovodovodo4ka.bones.ui.extensions

import android.view.Menu
import android.view.MenuItem

fun Menu.indexOf(item: MenuItem): Int = 0.until(size()).firstOrNull { getItem(it).itemId == item.itemId } ?: -1
