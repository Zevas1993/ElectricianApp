package com.example.electricianapp.presentation.fragment.calculators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.electricianapp.databinding.FragmentLuminaireCalculatorBinding
import com.example.electricianapp.presentation.viewmodel.calculators.LuminaireCalculatorViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import kotlin.math.ceil

@AndroidEntryPoint
class LuminaireCalculatorFragment : Fragment() {

    private var _binding: FragmentLuminaireCalculatorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LuminaireCalculatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLuminaireCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInputListeners()
        setupCalculateButton()
        observeViewModel()
    }

    private fun setupInputListeners() {
        binding.roomLengthEditText.doOnTextChanged { text, _, _, _ -> viewModel.roomLength.value = text.toString() }
        binding.roomWidthEditText.doOnTextChanged { text, _, _, _ -> viewModel.roomWidth.value = text.toString() }
        binding.footcandlesEditText.doOnTextChanged { text, _, _, _ -> viewModel.desiredFootcandles.value = text.toString() }
        binding.lumensEditText.doOnTextChanged { text, _, _, _ -> viewModel.lumensPerLuminaire.value = text.toString() }
        binding.cuEditText.doOnTextChanged { text, _, _, _ -> viewModel.coefficientOfUtilization.value = text.toString() }
        binding.llfEditText.doOnTextChanged { text, _, _, _ -> viewModel.lightLossFactor.value = text.toString() }
    }

    private fun setupCalculateButton() {
        binding.calculateButton.setOnClickListener {
            viewModel.calculate()
        }
    }

    private fun observeViewModel() {
        viewModel.numberOfLuminaires.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                binding.resultTextView.text = "Result: $result Luminaires"
            } else {
                // Clear result if calculation failed or inputs are invalid
                 if (viewModel.errorMessage.value == null) { // Avoid clearing if there's a specific error message
                     binding.resultTextView.text = ""
                 }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.resultTextView.text = "" // Clear calculation result on error
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                viewModel.clearError() // Clear error after showing it
            }
        }

        // Restore input values from ViewModel (e.g., after rotation)
        binding.roomLengthEditText.setText(viewModel.roomLength.value)
        binding.roomWidthEditText.setText(viewModel.roomWidth.value)
        binding.footcandlesEditText.setText(viewModel.desiredFootcandles.value)
        binding.lumensEditText.setText(viewModel.lumensPerLuminaire.value)
        binding.cuEditText.setText(viewModel.coefficientOfUtilization.value)
        binding.llfEditText.setText(viewModel.lightLossFactor.value)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
