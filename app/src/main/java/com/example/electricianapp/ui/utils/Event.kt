package com.example.electricianapp.ui.utils

/**
 * Used as a wrapper for data exposed via LiveData that represents a single event
 * (like navigation, showing a Snackbar, or displaying a Toast).
 *
 * Problem Solved: LiveData holds the last value. On configuration changes (like screen rotation),
 * the Activity/Fragment might re-observe the LiveData and trigger the event again undesirably.
 * This class ensures the event's content is consumed only once.
 *
 * Usage:
 * ViewModel:
 *   private val _navigateToDetail = MutableLiveData<Event<Long>>()
 *   val navigateToDetail: LiveData<Event<Long>> = _navigateToDetail
 *
 *   fun onUserClicked(userId: Long) {
 *       _navigateToDetail.value = Event(userId) // Trigger event
 *   }
 *
 * Fragment:
 *   viewModel.navigateToDetail.observe(viewLifecycleOwner) { event ->
 *       event.getContentIfNotHandled()?.let { userId ->
 *           // Only triggers if the event hasn't been handled yet
 *           navigateToDetailScreen(userId)
 *       }
 *   }
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but only internal write (within this class)

    /**
     * Returns the content only if it hasn't been handled yet.
     * Marks the event as handled after returning the content.
     * @return The content of type T, or null if it has already been handled.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, regardless of whether it has been handled.
     * Useful for inspecting the event's value without consuming it.
     * @return The content of type T.
     */
    fun peekContent(): T = content
}
