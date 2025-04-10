package com.example.electricianapp.presentation.fragment.neccodes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.electricianapp.data.repository.neccodes.NecCodeDataProvider
import com.example.electricianapp.databinding.FragmentNecCodeLookupBinding
import com.example.electricianapp.presentation.viewmodel.neccodes.NecCodeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NecCodeLookupFragment : Fragment() {

    private var _binding: FragmentNecCodeLookupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NecCodeViewModel by viewModels()

    @Inject
    lateinit var necCodeDataProvider: NecCodeDataProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNecCodeLookupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupToolbar()
        loadSampleData()
    }

    private fun loadSampleData() {
        // Populate the database with sample data
        viewLifecycleOwner.lifecycleScope.launch {
            necCodeDataProvider.populateSampleData()
        }
    }

    private fun setupViewPager() {
        val pagerAdapter = NecCodePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // Connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Search"
                1 -> "Browse"
                2 -> "Updates"
                3 -> "Violations"
                4 -> "Bookmarks"
                else -> "Tab ${position + 1}"
            }
        }.attach()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            // Handle navigation click (e.g., go back)
            requireActivity().onBackPressed()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                com.example.electricianapp.R.id.action_search -> {
                    // Navigate to search tab
                    binding.viewPager.currentItem = 0
                    true
                }
                com.example.electricianapp.R.id.action_bookmarks -> {
                    // Navigate to bookmarks tab
                    binding.viewPager.currentItem = 4
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Pager adapter for the NEC code tabs
     */
    private inner class NecCodePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 5

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> NecCodeSearchFragment()
                1 -> NecCodeBrowseFragment()
                2 -> NecCodeUpdatesFragment()
                3 -> NecCodeViolationsFragment()
                4 -> NecCodeBookmarksFragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}
