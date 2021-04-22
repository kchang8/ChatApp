package com.bignerdranch.android.chatapp2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bignerdranch.android.chatapp2.fragments.ChatFragment
import com.bignerdranch.android.chatapp2.fragments.SearchFragment
import com.bignerdranch.android.chatapp2.fragments.SettingsFragment
import com.bignerdranch.android.chatapp2.fragments.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setUpTabs()
    }

    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ChatFragment(), "Chats")
        adapter.addFragment(SearchFragment(), "Search")
        adapter.addFragment(SettingsFragment(), "Settings")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_chat_24)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_search_24)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_settings_24)
    }
}