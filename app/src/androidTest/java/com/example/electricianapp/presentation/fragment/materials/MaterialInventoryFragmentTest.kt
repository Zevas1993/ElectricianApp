package com.example.electricianapp.presentation.fragment.materials

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.electricianapp.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for the MaterialInventoryFragment
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MaterialInventoryFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testInventoryListDisplayed() {
        // Launch the fragment
        launchFragmentInContainer<MaterialInventoryFragment>(themeResId = R.style.Theme_ElectricianApp)

        // Verify the title is displayed
        onView(withId(R.id.textViewTitle)).check(matches(withText("Material Inventory")))
        
        // Verify the search view is displayed
        onView(withId(R.id.searchView)).check(matches(isDisplayed()))
        
        // Verify the filter button is displayed
        onView(withId(R.id.filterButton)).check(matches(isDisplayed()))
        
        // Verify the FAB is displayed
        onView(withId(R.id.fabAddInventory)).check(matches(isDisplayed()))
    }

    @Test
    fun testSearchFunctionality() {
        // Launch the fragment
        launchFragmentInContainer<MaterialInventoryFragment>(themeResId = R.style.Theme_ElectricianApp)

        // Perform search
        onView(withId(R.id.searchView)).perform(click())
        onView(isAssignableFrom(androidx.appcompat.widget.SearchView.SearchAutoComplete::class.java))
            .perform(typeText("Wire"), pressImeActionButton())

        // Verify search is performed (we can't verify results without mock data)
        // But we can verify the search view is still displayed
        onView(withId(R.id.searchView)).check(matches(isDisplayed()))
    }

    @Test
    fun testLowStockBanner() {
        // Launch the fragment
        launchFragmentInContainer<MaterialInventoryFragment>(themeResId = R.style.Theme_ElectricianApp)

        // Verify the low stock banner exists
        onView(withId(R.id.lowStockBanner)).check(matches(isDisplayed()))
        
        // Click on the low stock banner
        onView(withId(R.id.lowStockBanner)).perform(click())
        
        // Verify the banner is still displayed after click
        onView(withId(R.id.lowStockBanner)).check(matches(isDisplayed()))
    }

    @Test
    fun testAddInventoryButton() {
        // Launch the fragment
        launchFragmentInContainer<MaterialInventoryFragment>(themeResId = R.style.Theme_ElectricianApp)

        // Verify the FAB is displayed
        onView(withId(R.id.fabAddInventory)).check(matches(isDisplayed()))
        
        // Click on the FAB
        // Note: This would normally navigate to another fragment, which we can't test here
        // without additional setup. But we can verify the button is clickable.
        onView(withId(R.id.fabAddInventory)).perform(click())
    }

    @Test
    fun testFilterButton() {
        // Launch the fragment
        launchFragmentInContainer<MaterialInventoryFragment>(themeResId = R.style.Theme_ElectricianApp)

        // Verify the filter button is displayed
        onView(withId(R.id.filterButton)).check(matches(isDisplayed()))
        
        // Click on the filter button
        // This would normally show a dialog, which we can't easily test here
        // without additional setup. But we can verify the button is clickable.
        onView(withId(R.id.filterButton)).perform(click())
    }
}
