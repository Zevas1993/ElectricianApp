package com.example.electricianapp.ui.jobs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.electricianapp.databinding.FragmentAddEditJobBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditJobFragment : Fragment() {

    private var _binding: FragmentAddEditJobBinding? = null
    private val binding get() = _binding!!

    // TODO: Inject ViewModel
    // private val viewModel: AddEditJobViewModel by viewModels()

    private val args: AddEditJobFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditJobBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = args.userId
        val jobId = args.jobId // Will be -1L if adding a new job
        val title = args.title // Title passed from navigation

        // Set the title (e.g., in the ActionBar)
        // (requireActivity() as AppCompatActivity).supportActionBar?.title = title

        // TODO: Populate fields if editing (jobId != -1L)
        // TODO: Set up save button click listener
        // binding.saveJobButton.setOnClickListener { saveJob() }
    }

    // TODO: Implement saveJob() function

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
