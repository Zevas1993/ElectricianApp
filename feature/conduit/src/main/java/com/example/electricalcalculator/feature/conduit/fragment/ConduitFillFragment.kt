package com.example.electricalcalculator.presentation.fragment.conduitfill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electricalcalculator.R
import com.example.electricalcalculator.domain.model.conduitfill.ConduitType
import com.example.electricalcalculator.domain.model.conduitfill.Wire
import com.example.electricalcalculator.domain.model.conduitfill.WireType
import com.example.electricalcalculator.presentation.adapter.WireAdapter
import com.example.electricalcalculator.presentation.dialog.AddWireDialogFragment
import com.example.electricalcalculator.presentation.viewmodel.conduitfill.ConduitFillUiState
import com.example.electricalcalculator.presentation.viewmodel.conduitfill.ConduitFillViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConduitFillFragment : Fragment(), WireAdapter.WireItemListener {

    private val viewModel: ConduitFillViewModel by viewModels()
    private lateinit var wireAdapter: WireAdapter
    
    // UI components
    private lateinit var tabLayout: TabLayout
    private lateinit var conduitTypeSpinner: Spinner
    private lateinit var conduitSizeSpinner: Spinner
    private lateinit var wiresRecyclerView: RecyclerView
    private lateinit var addWireButton: Button
    private lateinit var calculateButton: Button
    private lateinit var fillInfoTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_conduit_fill, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize UI components
        tabLayout = view.findViewById(R.id.tabLayout)
        conduitTypeSpinner = view.findViewById(R.id.conduitTypeSpinner)
        conduitSizeSpinner = view.findViewById(R.id.conduitSizeSpinner)
        wiresRecyclerView = view.findViewById(R.id.wiresRecyclerView)
        addWireButton = view.findViewById(R.id.addWireButton)
        calculateButton = view.findViewById(R.id.calculateButton)
        fillInfoTextView = view.findViewById(R.id.fillInfoTextView)
        
        setupTabLayout()
        setupConduitTypeSpinner()
        setupConduitSizeSpinner()
        setupWiresRecyclerView()
        setupButtons()
        observeViewModel()
    }
    
    private fun setupTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        // Already on Conduit Fill tab
                    }
                    1 -> {
                        // Navigate to Box Fill tab
                        findNavController().navigate(R.id.action_conduitFillFragment_to_boxFillFragment)
                    }
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    private fun setupConduitTypeSpinner() {
        val conduitTypes = ConduitType.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, conduitTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        conduitTypeSpinner.adapter = adapter
        
        conduitTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedType = ConduitType.values()[position]
                viewModel.setConduitType(selectedType)
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    
    private fun setupConduitSizeSpinner() {
        val conduitSizes = listOf("1/2", "3/4", "1", "1-1/4", "1-1/2", "2", "2-1/2", "3", "3-1/2", "4")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, conduitSizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        conduitSizeSpinner.adapter = adapter
        
        conduitSizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSize = conduitSizes[position]
                viewModel.setConduitSize(selectedSize)
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    
    private fun setupWiresRecyclerView() {
        wireAdapter = WireAdapter(this)
        wiresRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = wireAdapter
        }
    }
    
    private fun setupButtons() {
        addWireButton.setOnClickListener {
            showAddWireDialog()
        }
        
        calculateButton.setOnClickListener {
            viewModel.calculateConduitFill()
        }
    }
    
    private fun showAddWireDialog(wire: Wire? = null, position: Int = -1) {
        val dialog = AddWireDialogFragment(
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
    }
    
    private fun observeViewModel() {
        viewModel.wires.observe(viewLifecycleOwner) { wires ->
            wireAdapter.submitList(wires)
            updateFillInfo(wires)
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ConduitFillUiState.Initial -> {
                        // Initial state, no action needed
                    }
                    is ConduitFillUiState.Loading -> {
                        // Show loading state
                        calculateButton.isEnabled = false
                        calculateButton.text = "Calculating..."
                    }
                    is ConduitFillUiState.Success -> {
                        // Navigate to results screen
                        calculateButton.isEnabled = true
                        calculateButton.text = "CALCULATE"
                        findNavController().navigate(R.id.action_conduitFillFragment_to_conduitFillResultsFragment)
                    }
                    is ConduitFillUiState.Error -> {
                        // Show error message
                        calculateButton.isEnabled = true
                        calculateButton.text = "CALCULATE"
                        fillInfoTextView.text = "Error: ${state.message}"
                    }
                }
            }
        }
    }
    
    private fun updateFillInfo(wires: List<Wire>) {
        if (wires.isEmpty()) {
            fillInfoTextView.text = "Add conductors to see fill percentage"
            return
        }
        
        val totalWires = wires.sumOf { it.quantity }
        val maxFillPercentage = when {
            totalWires == 1 -> "53%"
            totalWires == 2 -> "31%"
            totalWires >= 3 -> "40%"
            else -> "40%"
        }
        
        fillInfoTextView.text = "Total conductors: $totalWires (Maximum fill: $maxFillPercentage)"
    }
    
    // WireAdapter.WireItemListener implementation
    override fun onEditWire(position: Int, wire: Wire) {
        showAddWireDialog(wire, position)
    }
    
    override fun onRemoveWire(position: Int) {
        viewModel.removeWire(position)
    }
}
