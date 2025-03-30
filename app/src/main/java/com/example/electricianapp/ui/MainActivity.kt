package com.example.electricianapp.ui
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp // Extension function for NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.electricianapp.R // Import your project's R class
import com.example.electricianapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main and typically only Activity in a single-activity architecture.
 * It sets up the navigation host and configures the ActionBar (if present)
 * to work with the Navigation Component.
 *
 * @AndroidEntryPoint enables Hilt dependency injection in this Activity.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find the NavHostFragment within the layout. This fragment hosts all other fragments.
        // It's crucial that the ID 'nav_host_fragment_container' matches the ID in activity_main.xml.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Define which destinations are considered "top-level". The Up button (<-)
        // will not be shown in the ActionBar for these destinations.
        // In this app, after login, the JobListFragment is the main screen.
        // Login itself is the start, but we pop it off the back stack.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.jobListFragment) // Add other top-level destination IDs here if needed
        )

        // Connect the NavController to the ActionBar provided by the Activity's theme.
        // This automatically updates the ActionBar title based on the fragment's label
        // and handles the Up button clicks based on the appBarConfiguration.
        // Ensure your theme includes an ActionBar (e.g., Theme.Material3.DayNight.DarkActionBar).
        // If using NoActionBar theme, you'd set up a Toolbar manually here instead.
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * Handles the ActionBar's Up button click.
     * Delegates the navigation action to the NavController, respecting the AppBarConfiguration.
     * If the NavController can't navigate up (e.g., at a top-level destination),
     * it falls back to the default Activity behavior (which might finish the activity).
     *
     * @return True if navigation was handled by the NavController, false otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        // Use the navigateUp extension function with the appBarConfiguration
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
