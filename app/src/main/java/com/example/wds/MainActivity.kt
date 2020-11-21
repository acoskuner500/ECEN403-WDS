package com.example.wds

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wds.fragments.choose.ChooseFragment
import com.example.wds.fragments.log.LogFragment
import com.example.wds.fragments.verify.VerifyFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private val verifyFragment = VerifyFragment()
    private val logFragment = LogFragment()
    private val chooseFragment = ChooseFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize
        setCurrentFragment(verifyFragment)
        info_title.text = getString(R.string.info_verify_title)
        info_text.text = getString(R.string.info_verify)
        bottom_navigation.selectedItemId = R.id.miVerify
        toolbar.title = "Verify Deterrences"

        // Info Dialog
        infoBtn.setOnClickListener {
            infoDialog.visibility =
                if (infoDialog.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        okBtn.setOnClickListener { infoDialog.visibility = View.GONE }

        // Bottom Navigation Selector
        bottom_navigation.setOnNavigationItemSelectedListener {
            if (bottom_navigation != null) {
                infoDialog.visibility = View.GONE
                when (it.itemId) {
                    R.id.miVerify -> {
                        setCurrentFragment(verifyFragment)
                        info_title.text = getString(R.string.info_verify_title)
                        info_text.text = getString(R.string.info_verify)
                    }
                    R.id.miLog -> {
                        setCurrentFragment(logFragment)
                        info_title.text = getString(R.string.info_log_title)
                        info_text.text = getString(R.string.info_log)
                    }
                    R.id.miChoose -> {
                        setCurrentFragment(chooseFragment)
                        info_title.text = getString(R.string.info_choose_title)
                        info_text.text = getString(R.string.info_choose)
                    }
                }
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            when (fragment) {
                verifyFragment  -> toolbar.title = "Verify Deterrences"
                logFragment     -> toolbar.title = "Deterrence Log"
                chooseFragment  -> toolbar.title = "Choose Animals to Deter"
            }
            replace(R.id.flFragment, fragment)
            commit()
        }
}
