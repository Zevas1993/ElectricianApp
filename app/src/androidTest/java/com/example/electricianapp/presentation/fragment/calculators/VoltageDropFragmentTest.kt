package com.example.electricianapp.presentation.fragment.calculators

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.electricianapp.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for the VoltageDropFragment
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class VoltageDropFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testVoltageDropCalculation() {
        // Launch the fragment
        launchFragmentInContainer<VoltageDropFragment>(themeResId = R.style.Theme_ElectricianApp)

        // Input test values
        onView(withId(R.id.editTextVoltage)).perform(clearText(), typeText("120"), closeSoftKeyboard())
        onView(withId(R.id.spinnerPhase)).perform(click())
        onView(withText("Single Phase")).perform(click())
        onView(withId(R.id.spinnerMaterial)).perform(click())
        onView(withText("Copper")).perform(click())
        onView(withId(R.id.spinnerWireSize)).perform(click())
        onView(withText("12 AWG")).perform(click())
        onView(withId(R.id.editTextLoadCurrent)).perform(clearText(), typeText("20"), closeSoftKeyboard())
        onView(withId(R.id.editTextDistance)).perform(clearText(), typeText("100"), closeSoftKeyboard())

        // Perform calculation
        onView(withId(R.id.buttonCalculate)).perform(click())

        // Verify results are displayed
        onView(withId(R.id.textViewVoltageDropValue)).check(matches(not(withText(""))))
        onView(withId(R.id.textViewVoltageDropPercentValue)).check(matches(not(withText(""))))
        onView(withId(R.id.textViewEndVoltageValue)).check(matches(not(withText(""))))
        
        // Verify recommendation is displayed
        onView(withId(R.id.textViewRecommendationValue)).check(matches(isDisplayed()))
    }

    @Test
    fun testInvalidInputShowsError() {
        // Launch the fragment
        launchFragmentInContainer<VoltageDropFragment>(themeResId = R.style.Theme_ElectricianApp)

        // Input invalid values (missing required fields)
        onView(withId(R.id.editTextVoltage)).perform(clearText(), typeText("120"), closeSoftKeyboard())
        // Don't fill in other fields

        // Perform calculation
        onView(withId(R.id.buttonCalculate)).perform(click())

        // Verify error message is displayed
        onView(withId(R.id.textViewErrorMessage)).check(matches(isDisplayed()))
    }

    @Test
    fun testInputFieldsUpdateUiState() {
        // Launch the fragment
        launchFragmentInContainer<VoltageDropFragment>(themeResId = R.style.Theme_ElectricianApp)

        // Input test values
        onView(withId(R.id.editTextVoltage)).perform(clearText(), typeText("240"), closeSoftKeyboard())
        onView(withId(R.id.spinnerPhase)).perform(click())
        onView(withText("Three Phase")).perform(click())
        onView(withId(R.id.spinnerMaterial)).perform(click())
        onView(withText("Aluminum")).perform(click())
        onView(withId(R.id.spinnerWireSize)).perform(click())
        onView(withText("2 AWG")).perform(click())
        onView(withId(R.id.editTextLoadCurrent)).perform(clearText(), typeText("30"), closeSoftKeyboard())
        onView(withId(R.id.editTextDistance)).perform(clearText(), typeText("150"), closeSoftKeyboard())

        // Perform calculation
        onView(withId(R.id.buttonCalculate)).perform(click())

        // Verify results are displayed
        onView(withId(R.id.textViewVoltageDropValue)).check(matches(not(withText(""))))
        onView(withId(R.id.textViewVoltageDropPercentValue)).check(matches(not(withText(""))))
        onView(withId(R.id.textViewEndVoltageValue)).check(matches(not(withText(""))))
    }
}
