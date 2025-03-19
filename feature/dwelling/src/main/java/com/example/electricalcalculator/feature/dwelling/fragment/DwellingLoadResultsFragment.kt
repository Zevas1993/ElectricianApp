package com.example.electricalcalculator.presentation.fragment.dwellingload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electricalcalculator.databinding.FragmentDwellingLoadResultsBinding
import com.example.electricalcalculator.domain.model.dwellingload.DwellingLoadResult
import com.example.electricalcalculator.presentation.adapter.ApplianceResultAdapter
import com.example.electricalcalculator.presentation.viewmodel.dwellingload.DwellingLoadViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat

@AndroidEntryPoint
class DwellingLoadResultsFragment : Fragment() {

    private var _binding: FragmentDwellingLoadResultsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DwellingLoadViewModel by viewModels()
    private lateinit var applianceResultAdapter: ApplianceResultAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDwellingLoadResultsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupApplianceResultsRecyclerView()
        setupButtons()
        observeViewModel()
    }
    
    private fun setupApplianceResultsRecyclerView() {
        applianceResultAdapter = ApplianceResultAdapter()
        binding.applianceResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = applianceResultAdapter
        }
    }
    
    private fun setupButtons() {
        binding.saveCalculationButton.setOnClickListener {
            // Already saved in the ViewModel when calculation was performed
            // Just show confirmation
            showToast("Calculation saved")
        }
        
        binding.newCalculationButton.setOnClickListener {
            findNavController().popBackStack()
        }
        
        binding.viewMoreDetailsButton.setOnClickListener {
            // Toggle visibility of detailed sections
            // In a real implementation, this might navigate to a more detailed view
            showToast("Showing additional details")
        }
    }
    
    private fun observeViewModel() {
        viewModel.calculationResult.observe(viewLifecycleOwner) { result ->
            result?.let { updateUI(it) }
        }
    }
    
    private fun updateUI(result: DwellingLoadResult) {
        val numberFormat = NumberFormat.getNumberInstance().apply {
            maximumFractionDigits = 1
        }
        
        // Update summary section
        binding.totalConnectedLoadTextView.text = "${numberFormat.format(result.totalConnectedLoad)} VA"
        binding.totalDemandLoadTextView.text = "${numberFormat.format(result.totalDemandLoad)} VA"
        binding.serviceSizeTextView.text = "${result.serviceSize}A"
        
        // Update detailed breakdown
        val squareFootage = viewModel.squareFootage.value ?: 0.0
        val vaPerSquareFoot = when (viewModel.dwellingType.value) {
            com.example.electricalcalculator.domain.model.dwellingload.DwellingType.RESIDENTIAL -> 3.0
            com.example.electricalcalculator.domain.model.dwellingload.DwellingType.COMMERCIAL -> 3.5
            com.example.electricalcalculator.domain.model.dwellingload.DwellingType.INDUSTRIAL -> 2.5
            else -> 3.0
        }
        
        binding.generalLightingDetailTextView.text = 
            "${numberFormat.format(result.generalLightingLoad)} VA " +
            "(${numberFormat.format(squareFootage)} sq ft × ${vaPerSquareFoot} VA)"
        
        val smallApplianceCircuits = viewModel.smallApplianceCircuits.value ?: 2
        binding.smallApplianceDetailTextView.text = 
            "${numberFormat.format(result.smallApplianceLoad)} VA " +
            "(${smallApplianceCircuits} circuits × 1,500 VA)"
        
        val laundryCircuits = viewModel.laundryCircuits.value ?: 1
        binding.laundryDetailTextView.text = 
            "${numberFormat.format(result.laundryLoad)} VA " +
            "(${laundryCircuits} circuit${if (laundryCircuits > 1) "s" else ""} × 1,500 VA)"
        
        // Lighting demand factors
        val totalLightingLoad = result.generalLightingLoad + result.smallApplianceLoad + result.laundryLoad
        val firstPortion = minOf(totalLightingLoad, 3000.0)
        val remainingPortion = maxOf(0.0, totalLightingLoad - 3000.0)
        val remainingWithDemand = remainingPortion * 0.35
        val totalWithDemand = firstPortion + remainingWithDemand
        
        binding.lightingDemandFactorDetailTextView.text = 
            "First 3,000 VA at 100%: ${numberFormat.format(firstPortion)} VA\n" +
            "Remaining ${numberFormat.format(remainingPortion)} VA at 35%: ${numberFormat.format(remainingWithDemand)} VA\n" +
            "Total: ${numberFormat.format(totalWithDemand)} VA"
        
        // Update appliance results
        val applianceResults = result.applianceLoads.map { (name, load) ->
            val demandFactor = result.demandFactors[name] ?: 1.0
            ApplianceResult(name, load, demandFactor, load * demandFactor)
        }
        applianceResultAdapter.submitList(applianceResults)
    }
    
    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    data class ApplianceResult(
        val name: String,
        val connectedLoad: Double,
        val demandFactor: Double,
        val demandLoad: Double
    )
}
