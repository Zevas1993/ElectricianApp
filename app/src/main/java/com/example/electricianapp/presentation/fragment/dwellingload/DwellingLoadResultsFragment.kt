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
import com.example.electricianapp.databinding.FragmentDwellingLoadResultsBinding // Uncomment ViewBinding import
import com.example.electricianapp.domain.model.dwellingload.DwellingLoadResult
import com.example.electricianapp.presentation.adapter.ApplianceResultAdapter
import com.example.electricianapp.presentation.viewmodel.dwellingload.DwellingLoadViewModel // Corrected import
import com.example.electricianapp.presentation.viewmodel.dwellingload.DwellingLoadUiState // Import UiState
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope // Import lifecycleScope
import kotlinx.coroutines.flow.collect // Import collect
import kotlinx.coroutines.launch // Import launch
import java.text.NumberFormat
import com.example.electricianapp.R // Corrected import
import android.widget.EditText // Import EditText for dialog
import androidx.appcompat.app.AlertDialog // Import AlertDialog

@AndroidEntryPoint
class DwellingLoadResultsFragment : Fragment() {

    private var _binding: FragmentDwellingLoadResultsBinding? = null // Uncomment ViewBinding
    private val binding get() = _binding!! // Uncomment ViewBinding getter

    // Use activityViewModels() if sharing ViewModel with parent fragment, or viewModels() if specific to this fragment
    // Using viewModels with requireParentFragment() to share the ViewModel instance
    private val viewModel: DwellingLoadViewModel by viewModels({requireParentFragment()}) // Or just viewModels()
    private lateinit var applianceResultAdapter: ApplianceResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDwellingLoadResultsBinding.inflate(inflater, container, false) // Inflate using ViewBinding
        return binding.root // Return the root view from binding
    }

    // Uncomment and implement onViewCreated and UI setup methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupApplianceResultsRecyclerView()
        setupButtons()
        observeViewModel()
    }

    private fun setupApplianceResultsRecyclerView() {
        applianceResultAdapter = ApplianceResultAdapter() // Assuming ApplianceResultAdapter exists
        binding.applianceResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = applianceResultAdapter
        }
    }

    private fun setupButtons() {
        binding.saveCalculationButton.setOnClickListener {
            showSaveCalculationDialog()
        }
        // Add listeners for other buttons if they exist in the layout
    }

    private fun showSaveCalculationDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "Enter calculation name"
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Save Calculation")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.saveCurrentCalculation(name)
                    showToast("Calculation saved as '$name'")
                } else {
                    showToast("Please enter a name to save the calculation.")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        // Collect the final calculation result from the shared ViewModel's StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is DwellingLoadUiState.Success -> {
                        updateUI(state.result)
                    }
                    is DwellingLoadUiState.Error -> {
                        // Handle error state if needed, e.g., show a message or navigate back
                        showToast("Error loading results: ${state.message}")
                        findNavController().popBackStack() // Go back if there's an error loading results
                    }
                    // Handle Initial or Loading states if necessary
                    is DwellingLoadUiState.Loading -> { /* Optional: Show loading indicator */ }
                    is DwellingLoadUiState.Initial -> { /* Optional: Handle initial state */ }
                }
            }
        }
    } // <-- Add missing closing brace here

    private fun updateUI(result: DwellingLoadResult) {
        val numberFormat = NumberFormat.getNumberInstance().apply {
            maximumFractionDigits = 2
        }
        val vaFormat = { value: Double -> "${numberFormat.format(value)} VA" }

        binding.generalLightingValueTextView.text = vaFormat(result.generalLightingLoad)
        binding.smallApplianceValueTextView.text = vaFormat(result.smallApplianceLoad)
        binding.laundryValueTextView.text = vaFormat(result.laundryLoad)
        binding.totalConnectedValueTextView.text = vaFormat(result.totalConnectedLoad)
        binding.totalDemandValueTextView.text = vaFormat(result.totalDemandLoad)

        // Get the original appliance list from the ViewModel (assuming it holds the input)
        val originalAppliances = viewModel.appliances.value ?: emptyList()

        // Prepare data for the RecyclerView adapter by combining results with original data
        val applianceResults = result.applianceLoads.mapNotNull { (name, demandLoad) ->
            // Find the original appliance by name to get wattage, quantity, and demand factor
            val originalAppliance = originalAppliances.find { it.name == name }
            if (originalAppliance != null) {
                ApplianceResult(
                    name = name,
                    connectedLoad = originalAppliance.wattage * originalAppliance.quantity, // Calculate connected load
                    demandFactor = originalAppliance.demandFactor, // Get original demand factor
                    demandLoad = demandLoad // Use the calculated demand load from the result map
                )
            } else {
                null // Should not happen if result map is derived from input list
            }
        }
        applianceResultAdapter.submitList(applianceResults)
    }


    private fun showToast(message: String) {
        // Check context availability before showing Toast
        context?.let {
             android.widget.Toast.makeText(it, message, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    // Uncomment onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Nullify binding to prevent memory leaks
    }

    // Data class for the appliance results RecyclerView item
    data class ApplianceResult(
        val name: String,
        val connectedLoad: Double,
        val demandFactor: Double,
        val demandLoad: Double
    )
}
