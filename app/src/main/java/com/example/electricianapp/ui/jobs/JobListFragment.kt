package com.example.electricianapp.ui.jobs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electricianapp.databinding.FragmentJobListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobListFragment : Fragment() {

    private var _binding: FragmentJobListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JobListViewModel by viewModels()
    private lateinit var jobAdapter: JobAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        jobAdapter = JobAdapter { job ->
            // Navigate to Job Detail screen when an item is clicked
            val action = JobListFragmentDirections.actionJobListFragmentToJobDetailFragment(job.id)
            findNavController().navigate(action)
        }
        binding.recyclerViewJobs.apply {
            adapter = jobAdapter
            layoutManager = LinearLayoutManager(context)
            // Add item decoration if needed (e.g., dividers)
        }
    }

    private fun observeViewModel() {
        viewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            jobAdapter.submitList(jobs)
            // Handle empty list state if needed
        }
    }

     private fun setupClickListeners() {
        binding.fabAddJob.setOnClickListener {
            // Navigate to Add/Edit Job screen for adding a new job
            // Pass the userId from the ViewModel (which got it from nav args)
            // Pass -1L for jobId to indicate adding a new job
            val action = JobListFragmentDirections.actionJobListFragmentToAddEditJobFragment(
                userId = viewModel.userId, // Get userId from ViewModel
                jobId = -1L,
                title = "Add New Job" // Provide a title for the Add screen
            )
            findNavController().navigate(action)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
