package com.example.electricalcalculator.presentation.fragment.dwellingload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electricalcalculator.R
import com.example.electricalcalculator.databinding.FragmentDwellingLoadBinding
import com.example.electricalcalculator.domain.model.dwellingload.Appliance
import com.example.electricalcalculator.domain.model.dwellingload.DwellingType
import com.example.electricalcalculator.presentation.adapter.ApplianceAdapter
import com.example.electricalcalculator.presentation.dialog.AddApplianceDialogFragment
import com.example.electricalcalculator.presentation.viewmodel.dwellingload.DwellingLoadUiState
import com.example.electricalcalculator.presentation.viewmodel.dwellingload.DwellingLoadViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DwellingLoadFragment : Fragment(), ApplianceAdapter.ApplianceItemListener {

    private var _binding: FragmentDwellingLoadBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DwellingLoadViewModel by viewModels()
    private lateinit var applianceAdapter: ApplianceAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDwellingLoadBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupDwellingTypeSpinner()
        setupApplianceRecyclerView()
        setupCounterButtons()
        setupCalculateButton()
        observeViewModel()
    }
    
    private fun setupDwellingTypeSpinner() {
        val dwellingTypes = DwellingType.values().map { it.name.capitalize() }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dwellingTypes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.dwellingTypeSpinner.adapter = adapter
        
        binding.dwellingTypeSpinner.setOnItemSelectedListener { _, _, position, _ ->
            viewModel.setDwellingType(DwellingType.values()[position])
        }
    }
    
    private fun setupApplianceRecyclerView() {
        applianceAdapter = ApplianceAdapter(this)
        binding.appliancesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = applianceAdapter
        }
        
        viewModel.appliances.observe(viewLifecycleOwner) { appliances ->
            applianceAdapter.submitList(appliances)
        }
        
        binding.addApplianceButton.setOnClickListener {
            showAddApplianceDialog()
        }
    }
    
    private fun setupCounterButtons() {
        // Small appliance circuits
        binding.decreaseSmallApplianceButton.setOnClickListener {
            val currentCount = binding.smallApplianceCountTextView.text.toString().toInt()
            if (currentCount > 1) {
                val newCount = currentCount - 1
                binding.smallApplianceCountTextView.text = newCount.toString()
                viewModel.setSmallApplianceCircuits(newCount)
            }
        }
        
        binding.increaseSmallApplianceButton.setOnClickListener {
            val currentCount = binding.smallApplianceCountTextView.text.toString().toInt()
            val newCount = currentCount + 1
            binding.smallApplianceCountTextView.text = newCount.toString()
            viewModel.setSmallApplianceCircuits(newCount)
        }
        
        // Laundry circuits
        binding.decreaseLaundryButton.setOnClickListener {
            val currentCount = binding.laundryCountTextView.text.toString().toInt()
            if (currentCount > 0) {
                val newCount = currentCount - 1
                binding.laundryCountTextView.text = newCount.toString()
                viewModel.setLaundryCircuits(newCount)
            }
        }
        
        binding.increaseLaundryButton.setOnClickListener {
            val currentCount = binding.laundryCountTextView.text.toString().toInt()
            val newCount = currentCount + 1
            binding.laundryCountTextView.text = newCount.toString()
            viewModel.setLaundryCircuits(newCount)
        }
    }
    
    private fun setupCalculateButton() {
        binding.calculateButton.setOnClickListener {
            val squareFootage = binding.squareFootageEditText.text.toString().toDoubleOrNull()
            if (squareFootage == null || squareFootage <= 0) {
                Toast.makeText(requireContext(), "Please enter valid square footage", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            viewModel.setSquareFootage(squareFootage)
            viewModel.calculateDwellingLoad()
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is DwellingLoadUiState.Initial -> {
                        // Initial state, nothing to do
                    }
                    is DwellingLoadUiState.Loading -> {
                        // Show loading indicator
                        binding.calculateButton.isEnabled = false
                        binding.calculateButton.text = "Calculating..."
                    }
                    is DwellingLoadUiState.Success -> {
                        // Navigate to results screen
                        binding.calculateButton.isEnabled = true
                        binding.calculateButton.text = "CALCULATE"
                        findNavController().navigate(
                            R.id.action_dwellingLoadFragment_to_dwellingLoadResultsFragment
                        )
                    }
                    is DwellingLoadUiState.Error -> {
                        // Show error message
                        binding.calculateButton.isEnabled = true
                        binding.calculateButton.text = "CALCULATE"
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    
    private fun showAddApplianceDialog() {
        val dialog = AddApplianceDialogFragment { appliance ->
            viewModel.addAppliance(appliance)
        }
        dialog.show(parentFragmentManager, "AddApplianceDialog")
    }
    
    override fun onEditAppliance(position: Int, appliance: Appliance) {
        val dialog = AddApplianceDialogFragment(
            onSave = { updatedAppliance ->
                viewModel.updateAppliance(position, updatedAppliance)
            },
            initialAppliance = appliance
        )
        dialog.show(parentFragmentManager, "EditApplianceDialog")
    }
    
    override fun onRemoveAppliance(position: Int) {
        viewModel.removeAppliance(position)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Extension function to simplify spinner item selection
private fun android.widget.Spinner.setOnItemSelectedListener(
    onItemSelected: (parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) -> Unit
) {
    this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
            onItemSelected(parent, view, position, id)
        }
        
        override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
            // Do nothing
        }
    }
}

// Extension function to capitalize first letter of string
private fun String.capitalize(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}
