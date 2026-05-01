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
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.example.aszuoye.ui.ChatFragment
import com.example.aszuoye.ui.NewsListFragment
import com.example.aszuoye.ui.PlaceholderFragment

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    private val tagChat = "nav_chat"
    private val tagContacts = "nav_contacts"
    private val tagDynamic = "nav_dynamic"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        bottomNav = findViewById(R.id.bottomNav)

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

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            val current = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            (current as? ChatFragment)?.scrollToBottom()
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chat -> switchTo(tagChat) { ChatFragment() }
                R.id.nav_contacts -> switchTo(tagContacts) { PlaceholderFragment.newInstance("联系人页（占位）") }
                R.id.nav_dynamic -> switchTo(tagDynamic) { NewsListFragment() }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_chat
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

    private fun switchTo(tag: String, factory: () -> Fragment): Boolean {
        val fm = supportFragmentManager
        val tx = fm.beginTransaction()

        val current = fm.findFragmentById(R.id.fragmentContainer)
        if (current != null) {
            tx.hide(current)
        }

        val target = fm.findFragmentByTag(tag) ?: factory().also {
            tx.add(R.id.fragmentContainer, it, tag)
        }

        tx.show(target)
        tx.commit()

        updateFab(tag)
        return true
    }

    private fun updateFab(tag: String) {
        fab.isEnabled = tag == tagChat
        fab.alpha = if (tag == tagChat) 1f else 0.4f
    }
}
