package com.example.aszuoye

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import com.example.aszuoye.ui.MainPagerAdapter
import com.example.aszuoye.ui.ChatFragment

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        viewPager = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        viewPager.adapter = MainPagerAdapter(this)
        val titles = listOf("消息", "联系人", "动态")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        val navViewStart: NavigationView = findViewById(R.id.navViewStart)
        val navViewEnd: NavigationView = findViewById(R.id.navViewEnd)

        navViewStart.setNavigationItemSelectedListener { item ->
            Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        navViewEnd.setNavigationItemSelectedListener { item ->
            Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            if (viewPager.currentItem == 0) {
                val fragment = supportFragmentManager.findFragmentByTag("f0")
                (fragment as? ChatFragment)?.scrollToBottom()
            } else {
                Toast.makeText(this, "FAB 示例：当前页无聊天列表", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        when (item.itemId) {
            R.id.action_more -> {
                drawerLayout.openDrawer(GravityCompat.END)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.END) -> drawerLayout.closeDrawer(GravityCompat.END)
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
            else -> super.onBackPressed()
        }
    }
}
