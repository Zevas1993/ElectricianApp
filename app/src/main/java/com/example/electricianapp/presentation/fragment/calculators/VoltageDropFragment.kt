package com.example.electricianapp.presentation.fragment.calculators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.electricianapp.R
import com.example.electricianapp.databinding.FragmentVoltageDropBinding
import com.example.electricianapp.presentation.viewmodel.calculators.VoltageDropViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class VoltageDropFragment : Fragment() {

    private var _binding: FragmentVoltageDropBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VoltageDropViewModel by viewModels()

    // Define options for dropdowns
    private val voltageOptions = arrayOf("120", "208", "240", "277", "480") // Common voltages
    private val phaseOptions = arrayOf("Single Phase", "Three Phase")
    private val materialOptions = arrayOf("Copper", "Aluminum")
    // Common AWG sizes - align with ViewModel's map keys
    private val wireSizeOptions = arrayOf(
        "14", "12", "10", "8", "6", "4", "3", "2", "1",
        "1/0", "2/0", "3/0", "4/0", "250", "300", "350", "400", "500"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoltageDropBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropdowns()
        setupListeners()
        observeUiState()
    }

    private fun setupDropdowns() {
        setupArrayAdapter(binding.autoCompleteVoltage, voltageOptions)
        setupArrayAdapter(binding.autoCompletePhase, phaseOptions)
        setupArrayAdapter(binding.autoCompleteMaterial, materialOptions)
        setupArrayAdapter(binding.autoCompleteWireSize, wireSizeOptions)
    }

    private fun setupArrayAdapter(autoCompleteTextView: AutoCompleteTextView, options: Array<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options)
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun setupListeners() {
        binding.autoCompleteVoltage.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateVoltage(voltageOptions[position])
        }
        binding.autoCompletePhase.setOnItemClickListener { _, _, position, _ ->
            viewModel.updatePhase(phaseOptions[position])
        }
        binding.autoCompleteMaterial.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateMaterial(materialOptions[position])
        }
        binding.autoCompleteWireSize.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateWireSize(wireSizeOptions[position])
        }

        // Use doAfterTextChanged for TextInputEditText to avoid infinite loops if setting text programmatically
        binding.editTextLoadCurrent.addTextChangedListener {
            viewModel.updateLoadCurrent(it.toString())
        }
        binding.editTextDistance.addTextChangedListener {
            viewModel.updateDistance(it.toString())
        }

        binding.buttonCalculate.setOnClickListener {
            viewModel.calculateVoltageDrop()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Update dropdowns only if the text doesn't match the state
                    // This prevents resetting the dropdown when the user selects an item
                    if (binding.autoCompleteVoltage.text.toString() != state.voltage) {
                         binding.autoCompleteVoltage.setText(state.voltage, false)
                    }
                     if (binding.autoCompletePhase.text.toString() != state.phase) {
                         binding.autoCompletePhase.setText(state.phase, false)
                    }
                     if (binding.autoCompleteMaterial.text.toString() != state.material) {
                         binding.autoCompleteMaterial.setText(state.material, false)
                    }
                     if (binding.autoCompleteWireSize.text.toString() != state.wireSize) {
                         binding.autoCompleteWireSize.setText(state.wireSize, false)
                    }

                    // Update text inputs
                     if (binding.editTextLoadCurrent.text.toString() != state.loadCurrent) {
                        binding.editTextLoadCurrent.setText(state.loadCurrent)
                    }
                     if (binding.editTextDistance.text.toString() != state.distance) {
                        binding.editTextDistance.setText(state.distance)
                    }


                    // Update results
                    val df = DecimalFormat("#,##0.00") // Format for results
                    binding.textViewVoltageDropValue.text = state.voltageDrop?.let { "${df.format(it)} V" } ?: ""
                    binding.textViewVoltageDropPercentValue.text = state.voltageDropPercent?.let { "${df.format(it)} %" } ?: ""
                    binding.textViewEndVoltageValue.text = state.endVoltage?.let { "${df.format(it)} V" } ?: ""

                    // Update recommendation status
                    state.isWithinRecommendedLimits?.let { isWithinLimits ->
                        binding.textViewRecommendationValue.text = if (isWithinLimits) {
                            "Within NEC recommended limits"
                        } else {
                            "Exceeds NEC recommended limits"
                        }
                        binding.textViewRecommendationValue.setTextColor(
                            resources.getColor(
                                if (isWithinLimits) android.R.color.holo_green_dark else android.R.color.holo_red_dark,
                                null
                            )
                        )
                        binding.textViewRecommendationValue.visibility = View.VISIBLE
                    } ?: run {
                        binding.textViewRecommendationValue.visibility = View.GONE
                    }

                    // Show/hide error message
                    if (state.errorMessage != null) {
                        binding.textViewErrorMessage.text = state.errorMessage
                        binding.textViewErrorMessage.visibility = View.VISIBLE
                    } else {
                        binding.textViewErrorMessage.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
