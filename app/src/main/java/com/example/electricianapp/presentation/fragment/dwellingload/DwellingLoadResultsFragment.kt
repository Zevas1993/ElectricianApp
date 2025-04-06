package com.example.electricianapp.presentation.fragment.dwellingload // Corrected package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView // Add missing import
import androidx.fragment.app.Fragment // Keep only one import
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
// import com.example.electricianapp.databinding.FragmentDwellingLoadResultsBinding // Comment out ViewBinding
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadResult
import com.example.electricianapp.presentation.adapter.ApplianceResultAdapter
import com.example.electricianapp.presentation.viewmodel.dwellingload.DwellingLoadViewModel // Corrected import
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import com.example.electricianapp.R // Corrected import

@AndroidEntryPoint
class DwellingLoadResultsFragment : Fragment() {

    // private var _binding: FragmentDwellingLoadResultsBinding? = null // Comment out ViewBinding
    // private val binding get() = _binding!!

    // Use activityViewModels() if sharing ViewModel with parent fragment, or viewModels() if specific to this fragment
    // Assuming it might need data from the previous fragment's ViewModel instance
    private val viewModel: DwellingLoadViewModel by viewModels({requireParentFragment()}) // Or just viewModels()
    private lateinit var applianceResultAdapter: ApplianceResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // _binding = FragmentDwellingLoadResultsBinding.inflate(inflater, container, false) // Comment out ViewBinding
        // return binding.root // Comment out ViewBinding
        return TextView(requireContext()).apply { text = "Dwelling Load Results UI Placeholder" } // Return placeholder
    }

    // Comment out onViewCreated and UI setup methods
    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setupApplianceResultsRecyclerView()
        // setupButtons()
        // observeViewModel()
    }

    private fun setupApplianceResultsRecyclerView() {
        // ... (implementation depends on binding.applianceResultsRecyclerView)
    }

    private fun setupButtons() {
        // ... (implementation depends on binding.saveCalculationButton, etc.)
    }

    private fun observeViewModel() {
        // ... (implementation depends on updateUI)
    }

    private fun updateUI(result: DwellingLoadResult) {
        // ... (implementation depends on binding.* and R.string.*)
    }
    */

    private fun showToast(message: String) {
        // Check context availability before showing Toast
        context?.let {
             android.widget.Toast.makeText(it, message, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    // Comment out onDestroyView
    /*
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    */

    // Data class for the appliance results RecyclerView item
    data class ApplianceResult(
        val name: String,
        val connectedLoad: Double,
        val demandFactor: Double,
        val demandLoad: Double
    )
}
