package com.example.electricianapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.electricianapp.R
import com.example.electricianapp.databinding.ActivityMainBinding // Import ViewBinding
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // Use ViewBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Inflate using ViewBinding
        setContentView(binding.root)

        // Set up the Toolbar
        setSupportActionBar(binding.toolbar) // Use binding to access toolbar

        // Find NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Find DrawerLayout and NavigationView
        drawerLayout = binding.drawerLayout // Use binding
        val navView: NavigationView = binding.navView // Use binding

        // Define top-level destinations for AppBarConfiguration
        // These IDs should match the IDs in your drawer_menu.xml and nav_graph.xml
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.jobListFragment, R.id.dwellingLoadFragment,
                R.id.conduitFillFragment, R.id.boxFillFragment // Add other top-level IDs here
                // Add R.id.loginFragment here ONLY if you want the drawer on the login screen too
            ), drawerLayout
        )

        // Set up ActionBar with NavController and DrawerLayout
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up NavigationView with NavController
        navView.setupWithNavController(navController)
    }

    // Handle Up navigation with Drawer support
    override fun onSupportNavigateUp(): Boolean {
        // Use appBarConfiguration for proper Up/Drawer behavior
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
