package com.example.electricianapp.presentation.fragment.conduitfill // Corrected package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView // Keep one
import android.widget.ArrayAdapter // Keep one
import android.widget.Button
// Remove duplicate AdapterView
// Remove duplicate ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast // Import Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.R // Corrected
import com.example.electricianapp.domain.model.conduitfill.ConduitType // Corrected
import com.example.electricianapp.domain.model.conduitfill.Wire // Corrected
import com.example.electricianapp.domain.model.conduitfill.WireType // Corrected
import com.example.electricianapp.presentation.adapter.WireAdapter // Corrected
import com.example.electricianapp.presentation.dialog.AddWireDialogFragment // Corrected
import com.example.electricianapp.presentation.viewmodel.conduitfill.ConduitFillUiState // Add missing import
import com.example.electricianapp.presentation.viewmodel.conduitfill.ConduitFillViewModel
import com.example.electricianapp.databinding.FragmentConduitFillBinding // Import ViewBinding
import com.example.electricianapp.domain.model.conduitfill.ConduitFillResult // Add missing import
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest // Use collectLatest for UI state
import kotlinx.coroutines.launch
import java.text.NumberFormat // Import NumberFormat

@AndroidEntryPoint
class ConduitFillFragment : Fragment(), WireAdapter.WireItemListener {

    private val viewModel: ConduitFillViewModel by viewModels()
    private lateinit var wireAdapter: WireAdapter
    private var _binding: FragmentConduitFillBinding? = null
    private val binding get() = _binding!!

    // Define standard conduit sizes (adjust as needed)
    private val conduitSizes = listOf("1/2", "3/4", "1", "1-1/4", "1-1/2", "2", "2-1/2", "3", "3-1/2", "4")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConduitFillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupConduitTypeSpinner()
        setupConduitSizeSpinner()
        setupWiresRecyclerView()
        setupButtons()
        observeViewModel()
    }

    private fun setupConduitTypeSpinner() {
        val types = ConduitType.values().map { it.name } // Get names from enum
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.conduitTypeSpinner.adapter = adapter

        binding.conduitTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setConduitType(ConduitType.values()[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        // Set initial selection based on ViewModel
        viewModel.conduitType.value?.let {
            binding.conduitTypeSpinner.setSelection(it.ordinal)
        }
    }

    private fun setupConduitSizeSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, conduitSizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.conduitSizeSpinner.adapter = adapter

        binding.conduitSizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setConduitSize(conduitSizes[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        // Set initial selection based on ViewModel
        viewModel.conduitSize.value?.let { size ->
            val index = conduitSizes.indexOf(size)
            if (index >= 0) {
                binding.conduitSizeSpinner.setSelection(index)
            }
        }
    }

    private fun setupWiresRecyclerView() {
        wireAdapter = WireAdapter(this) // Pass listener
        binding.wiresRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = wireAdapter
        }
    }

    private fun setupButtons() {
        binding.addWireButton.setOnClickListener {
            showAddWireDialog()
        }
        binding.calculateButton.setOnClickListener {
            viewModel.calculateConduitFill()
        }
    }

    private fun showAddWireDialog(wire: Wire? = null, position: Int = -1) {
        // Get the list of wire sizes from the ViewModel
        val availableSizes = viewModel.availableWireSizes
        if (availableSizes.isEmpty()) {
            Toast.makeText(requireContext(), "Wire size data not available", Toast.LENGTH_SHORT).show()
            return // Don't show dialog if sizes aren't ready
        }

        val dialog = AddWireDialogFragment(
            wireSizes = availableSizes, // Pass the list
            onSave = { wireType, wireSize, quantity ->
                if (position >= 0) {
                    viewModel.updateWire(position, wireType, wireSize, quantity)
                } else {
                    viewModel.addWire(wireType, wireSize, quantity)
                }
            },
            initialWire = wire
        )
        dialog.show(childFragmentManager, "AddWireDialog")
        // Toast.makeText(requireContext(), "Add/Edit Wire Dialog not implemented yet", Toast.LENGTH_SHORT).show() // Placeholder removed
    }

    private fun observeViewModel() {
        // Observe wire list
        viewModel.wires.observe(viewLifecycleOwner) { wires ->
            wireAdapter.submitList(wires)
        }

        // Observe UI state (calculation results or errors)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                // Make when exhaustive
                when (state) {
                    is ConduitFillUiState.Success -> {
                        updateFillInfo(state.result)
                        binding.fillInfoTextView.setTextColor(resources.getColor(R.color.design_default_color_on_surface, null)) // Reset color
                    }
                    is ConduitFillUiState.Error -> {
                        binding.fillInfoTextView.text = "Error: ${state.message}"
                        binding.fillInfoTextView.setTextColor(resources.getColor(R.color.design_default_color_error, null))
                    }
                    is ConduitFillUiState.Loading -> {
                        binding.fillInfoTextView.text = "Calculating..."
                        binding.fillInfoTextView.setTextColor(resources.getColor(R.color.design_default_color_on_surface, null))
                    }
                    is ConduitFillUiState.Initial -> {
                        binding.fillInfoTextView.text = "" // Clear previous results/errors
                    }
                    // Add else or handle all subtypes if sealed class changes
                    // else -> { /* Optional: Handle any other state if necessary */ }
                }
            }
        }
    }

    private fun updateFillInfo(result: ConduitFillResult) {
        val nf = NumberFormat.getPercentInstance().apply { maximumFractionDigits = 1 }
        val fillPercent = result.fillPercentage / 100.0
        val maxFillPercent = result.maximumAllowedFillPercentage / 100.0

        val status = if (result.isWithinLimits) "OK" else "EXCEEDED"
        val colorRes = if (result.isWithinLimits) R.color.design_default_color_primary else R.color.design_default_color_error

        // Explicitly format Doubles
        binding.fillInfoTextView.text = "Fill: ${nf.format(fillPercent)} (Max: ${nf.format(maxFillPercent)}) - $status"
        binding.fillInfoTextView.setTextColor(resources.getColor(colorRes, null))
    }

    // WireAdapter.WireItemListener implementation
    override fun onEditWire(position: Int, wire: Wire) {
        showAddWireDialog(wire, position) // Re-enable when dialog is ready
        // Toast.makeText(requireContext(), "Edit Wire not implemented yet", Toast.LENGTH_SHORT).show() // Placeholder removed
    }

    override fun onRemoveWire(position: Int) {
        viewModel.removeWire(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding
    }
}
