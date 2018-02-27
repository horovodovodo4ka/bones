package pro.horovodovodo4ka.bones.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import pro.horovodovodo4ka.bones.Bone
import pro.horovodovodo4ka.bones.Finger
import pro.horovodovodo4ka.bones.Spine
import pro.horovodovodo4ka.bones.Wrist
import pro.horovodovodo4ka.bones.sample.MainActivity.Root
import pro.horovodovodo4ka.bones.sample.navigation.NavigationStack
import pro.horovodovodo4ka.bones.sample.navigation.TabBar
import pro.horovodovodo4ka.bones.sample.presentation.TestForm
import pro.horovodovodo4ka.bones.sample.presentation.TestScreen
import pro.horovodovodo4ka.bones.ui.SpineNavigatorInterface
import pro.horovodovodo4ka.bones.ui.delegates.SpineNavigator

class MainActivity : AppCompatActivity(),
    SpineNavigatorInterface<Root> by SpineNavigator() {

    init {
        managerProvider = ::getSupportFragmentManager
    }

    inner class Root(root: Bone) :
        Spine(root),
        Wrist.Listener,
        Finger.Listener {

        init {
            persistSibling = true
        }

        override val seed = { this@MainActivity }

        private var canExit = false

        override fun fingerSwitched(from: Int, to: Int) {
            canExit = false
        }

        override fun phalanxSwitched(
            from: Bone,
            to: Bone?,
            type: pro.horovodovodo4ka.bones.Finger.TransitionType
        ) {
            canExit = false
        }

        fun processBack(): Boolean {
            if (processBackPress()) {
                if (!canExit) {
                    Toast.makeText(this@MainActivity, """Press "back" button again to exit.""", Toast.LENGTH_LONG).show()
                    canExit = true
                    return false
                }
            }
            return canExit
        }
    }

    override fun onBackPressed() {
        if (bone.processBack()) super.onBackPressed()
    }

    companion object {
        private var root: Root? = null
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        bone = root ?: Root(
            TabBar(
                NavigationStack(TestScreen()),
                TestForm(),
                TestScreen()
            )
        ).also {
            root = it

        }
        bone.sibling = this
        bone.isActive = true

        refreshUI()


//            bone.attachTo(supportFragmentManager)

//        bone.sibling = this
//        bone.isActive = true
//        bone.attachTo(supportFragmentManager)
    }

    override fun onStart() {
        super.onStart()

//        refreshUI()
    }

    override fun onDestroy() {
        super.onDestroy()
//        bone.isActive = false
    }

    override fun onResume() {
        super.onResume()
//        refreshUI()
//        bone.isActive = true
//        bone.attachTo(supportFragmentManager)
    }

    override fun onPause() {
        super.onPause()
//        bone.isActive = false
//
//        // "clear" screen for not saving any fragment states to bundle when onSaveInstanceState called
//        with(supportFragmentManager) {
//            beginTransaction().replace(android.R.id.content, SnapshotFragment.snapshotFrom(findViewById(android.R.id.content))).commitNow()
//        }
    }

}

