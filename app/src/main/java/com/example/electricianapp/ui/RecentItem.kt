package com.example.electricianapp.ui

import androidx.annotation.DrawableRes
import androidx.navigation.NavDirections

// Represents an item in the "Recent Activity" drawer list
data class RecentItem(
    val id: String, // Unique ID (e.g., "job_123", "calculator_conduit")
    val label: String, // Text to display (e.g., "Job: Site Survey", "Conduit Fill Calculator")
    @DrawableRes val iconResId: Int, // Icon resource ID
    val timestamp: Long, // For sorting by recency
    val navigationAction: NavDirections // Navigation action to perform on click
    // Alternatively, use action ID + Bundle if arguments are complex
    // val navigationActionId: Int,
    // val navigationArgs: Bundle? = null
)
