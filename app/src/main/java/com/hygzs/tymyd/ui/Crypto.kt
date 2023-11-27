package com.hygzs.tymyd.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.KeyboardUtils
import com.google.android.material.tabs.TabLayout
import com.hygzs.tymyd.BaseActivity
import com.hygzs.tymyd.R
import com.hygzs.tymyd.crypto.CryptoLingShiFragment
import com.hygzs.tymyd.crypto.CryptoPropFragment
import com.hygzs.tymyd.crypto.CryptoVoiceFragment

class Crypto : BaseActivity() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        window.run {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            enterTransition = Explode() //进入动画
            exitTransition = Explode() //退出动画
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)
        init()
    }

    private fun init() {
        findViewById<ImageView>(R.id.imageView2).setOnClickListener {
            finishAfterTransition()
        }
        findViewById<RelativeLayout>(R.id.toolbar).setOnClickListener {
            //键盘存在就隐藏
            if (KeyboardUtils.isSoftInputVisible(this)) {
                KeyboardUtils.hideSoftInput(this)
            }
        }
        viewPager2 = findViewById(R.id.viewPager2)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager2.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager2.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }


    //懒得写文件了直接这里处理吧
    class ViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> CryptoLingShiFragment()
                1 -> CryptoPropFragment()
                2 -> CryptoVoiceFragment()
                else -> CryptoPropFragment()
            }
        }
    }
}