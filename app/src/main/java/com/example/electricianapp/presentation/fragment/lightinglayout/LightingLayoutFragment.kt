package com.example.electricianapp.presentation.fragment.lightinglayout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.electricianapp.databinding.FragmentLightingLayoutBinding
import com.example.electricianapp.presentation.viewmodel.lightinglayout.LightingLayoutViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class LightingLayoutFragment : Fragment() {

    private var _binding: FragmentLightingLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LightingLayoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLightingLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeUiState()
    }

    private fun setupListeners() {
        binding.editTextRoomLength.addTextChangedListener {
            viewModel.updateRoomLength(it.toString())
        }
        binding.editTextRoomWidth.addTextChangedListener {
            viewModel.updateRoomWidth(it.toString())
        }
        binding.buttonCalculateLayout.setOnClickListener {
            viewModel.calculateLayout()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Update input fields if they don't match state
                    if (binding.editTextRoomLength.text.toString() != state.roomLength) {
                        binding.editTextRoomLength.setText(state.roomLength)
                    }
                    if (binding.editTextRoomWidth.text.toString() != state.roomWidth) {
                        binding.editTextRoomWidth.setText(state.roomWidth)
                    }

                    // Update result fields
                    val df = DecimalFormat("#.##") // Format to 2 decimal places
                    state.spacingResult?.let { result ->
                        binding.textViewNumRowsValue.text = result.numRows.toString()
                        binding.textViewNumColsValue.text = result.numCols.toString()
                        binding.textViewTotalLuminairesValue.text = result.totalLuminaires.toString()
                        binding.textViewSpacingLengthValue.text = "${df.format(result.spacingLength)} ft"
                        binding.textViewSpacingWidthValue.text = "${df.format(result.spacingWidth)} ft"
                        binding.textViewBorderLengthValue.text = "${df.format(result.borderSpacingLength)} ft"
                        binding.textViewBorderWidthValue.text = "${df.format(result.borderSpacingWidth)} ft"
                        binding.textViewError.visibility = View.GONE
                        // Update visualization view
                        binding.lightingLayoutVisualization.updateLayout(
                            result,
                            state.roomLength.toDoubleOrNull(),
                            state.roomWidth.toDoubleOrNull()
                        )
                    } ?: run {
                        // Clear results if null
                        binding.textViewNumRowsValue.text = ""
                        binding.textViewNumColsValue.text = ""
                        binding.textViewTotalLuminairesValue.text = ""
                        binding.textViewSpacingLengthValue.text = ""
                        binding.textViewSpacingWidthValue.text = ""
                        binding.textViewBorderLengthValue.text = ""
                        binding.textViewBorderWidthValue.text = ""
                        // Clear visualization view
                        binding.lightingLayoutVisualization.updateLayout(null, null, null)
                    }

                    // Show/hide error message
                    if (state.errorMessage != null) {
                        binding.textViewError.text = state.errorMessage
                        binding.textViewError.visibility = View.VISIBLE
                    } else {
                        binding.textViewError.visibility = View.GONE
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
