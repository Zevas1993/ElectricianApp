package com.example.electricianapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.electricianapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var quickAccessAdapter: HomeQuickAccessAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        quickAccessAdapter = HomeQuickAccessAdapter(findNavController())
        binding.recyclerViewQuickAccess.apply {
            // Keep the GridLayoutManager defined in XML or set it here
            // layoutManager = GridLayoutManager(context, 2) // Or get spanCount from resources
            adapter = quickAccessAdapter
        }
    }

    private fun observeViewModel() {
        // Observe Job Summary
        // Correctly observe LiveData
        viewModel.jobSummary.observe(viewLifecycleOwner) { summary ->
            binding.textViewActiveJobsCount.text = summary.activeJobs.toString()
            binding.textViewTotalJobsCount.text = summary.totalJobs.toString()
        }

        // Observe Quick Access Items
        viewModel.quickAccessItems.observe(viewLifecycleOwner) { items ->
            quickAccessAdapter.submitList(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
