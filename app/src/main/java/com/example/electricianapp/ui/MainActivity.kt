package com.example.electricianapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
// import androidx.navigation.ui.setupWithNavController // No longer used for static menu
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electricianapp.R
import com.example.electricianapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.NavDirections // Needed for RecentItem

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // Use ViewBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var recentItemsAdapter: RecentItemsAdapter // Add adapter instance

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
                R.id.conduitFillFragment, R.id.boxFillFragment, R.id.materialListFragment // Add other top-level IDs here
                // Add R.id.loginFragment here ONLY if you want the drawer on the login screen too
            ), drawerLayout
        )

        // Set up ActionBar with NavController and DrawerLayout
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up NavigationView with NavController
        // navView.setupWithNavController(navController) // Commented out: Will handle dynamically

        // Setup the RecyclerView in the drawer
        setupDrawerRecyclerView(navView)
        loadRecentItems() // Load dummy data for now
    }

    private fun setupDrawerRecyclerView(navView: NavigationView) {
        // Access the RecyclerView within the NavigationView using the binding
        // Note: We added the RecyclerView directly inside NavigationView in activity_main.xml
        // If you used app:headerLayout, you'd access it differently.
        val drawerRecyclerView = binding.recyclerViewDrawerRecents // Use the ID from activity_main.xml

        recentItemsAdapter = RecentItemsAdapter(navController) {
            // Close the drawer after item click
            drawerLayout.closeDrawers()
        }
        drawerRecyclerView.layoutManager = LinearLayoutManager(this)
        drawerRecyclerView.adapter = recentItemsAdapter
    }

    private fun loadRecentItems() {
        // TODO: Replace with actual logic to load recent items from a ViewModel/Repository
        // Create dummy NavDirections (replace with real ones using correct implementation)
        val dummyActionHome: NavDirections = object : NavDirections {
            override val actionId: Int = R.id.homeFragment // Navigate to home for demo
            override val arguments: Bundle = Bundle()
        }
         val dummyActionJobs: NavDirections = object : NavDirections {
            override val actionId: Int = R.id.jobListFragment // Navigate to jobs for demo
            override val arguments: Bundle = Bundle() // TODO: Pass userId if needed
        }
        val dummyActionCalc: NavDirections = object : NavDirections {
            override val actionId: Int = R.id.calculatorListFragment // Navigate to calc list for demo
            override val arguments: Bundle = Bundle()
        }

        val dummyItems = listOf(
            RecentItem("calc_conduit", "Conduit Fill", R.drawable.ic_launcher_foreground, System.currentTimeMillis() - 10000, dummyActionCalc),
            RecentItem("job_1", "Job: Site A", R.drawable.ic_launcher_foreground, System.currentTimeMillis() - 20000, dummyActionJobs),
            RecentItem("calc_dwelling", "Dwelling Load", R.drawable.ic_launcher_foreground, System.currentTimeMillis() - 30000, dummyActionCalc)
        )
        recentItemsAdapter.submitList(dummyItems.sortedByDescending { it.timestamp })
    }


    // Handle Up navigation with Drawer support
    override fun onSupportNavigateUp(): Boolean {
        // Use appBarConfiguration for proper Up/Drawer behavior
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
