package com.example.aszuoye

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.example.aszuoye.broadcast.AppBroadcasts
import com.example.aszuoye.network.NetworkConnectivityHelper
import com.example.aszuoye.ui.ChatFragment
import com.example.aszuoye.ui.NewsListFragment
import com.example.aszuoye.ui.PlaceholderFragment
import com.example.aszuoye.ui.UserListActivity
import com.example.aszuoye.ui.ContactsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    /** 动态注册：默认网络回调 */
    private val networkHelper by lazy {
        NetworkConnectivityHelper(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    /** 动态注册：模拟 QQ 服务端下发的强制下线广播 */
    private val forceLogoutReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != AppBroadcasts.ACTION_FORCE_LOGOUT) return
            val message = intent.getStringExtra(AppBroadcasts.EXTRA_MESSAGE)
                ?: getString(R.string.force_logout_default)
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    private val tagChat = "nav_chat"
    private val tagContacts = "nav_contacts"
    private val tagDynamic = "nav_dynamic"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // API 35+ 常强制边到边；用 insets 顶开 AppBar，避免导航键/菜单与状态栏抢触控
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)

        val appBarLayout = findViewById<AppBarLayout>(R.id.appBarLayout)
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { v, insets ->
            val top = insets.getInsets(
                WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout()
            )
            v.setPadding(top.left, top.top, top.right, 0)
            insets
        }

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(this, android.R.drawable.ic_menu_sort_by_size)
        )
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        bottomNav = findViewById(R.id.bottomNav)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { v, insets ->
            val nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, nav.bottom)
            insets
        }

        ViewCompat.requestApplyInsets(drawerLayout)

        val navViewStart: NavigationView = findViewById(R.id.navViewStart)
        val navViewEnd: NavigationView = findViewById(R.id.navViewEnd)

        val navBarPadding = OnApplyWindowInsetsListener { v, insets ->
            val top = insets.getInsets(
                WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout()
            ).top
            v.setPadding(0, top, 0, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(navViewStart, navBarPadding)
        ViewCompat.setOnApplyWindowInsetsListener(navViewEnd, navBarPadding)

        navViewStart.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_login_users -> startActivity(Intent(this, UserListActivity::class.java))
                R.id.nav_contacts -> startActivity(Intent(this, ContactsActivity::class.java))
                else -> Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
            }
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

    override fun onStart() {
        super.onStart()
        networkHelper.register()
        val filter = IntentFilter(AppBroadcasts.ACTION_FORCE_LOGOUT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(forceLogoutReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(forceLogoutReceiver, filter)
        }
    }

    override fun onStop() {
        networkHelper.unregister()
        unregisterReceiver(forceLogoutReceiver)
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_simulate_force_logout -> {
                sendBroadcast(
                    Intent(AppBroadcasts.ACTION_FORCE_LOGOUT).apply {
                        setPackage(packageName)
                        putExtra(
                            AppBroadcasts.EXTRA_MESSAGE,
                            "您的账号已在其他设备登录，被迫下线（广播模拟）"
                        )
                    }
                )
                return true
            }
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
