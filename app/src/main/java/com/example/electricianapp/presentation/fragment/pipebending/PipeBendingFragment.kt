package com.example.electricianapp.presentation.fragment.pipebending

// Corrected and de-duplicated imports
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.electricianapp.R
import com.example.electricianapp.databinding.FragmentPipeBendingBinding
import com.example.electricianapp.presentation.viewmodel.pipebending.BendType
import com.example.electricianapp.presentation.viewmodel.pipebending.PipeBendingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class PipeBendingFragment : Fragment() {

    private var _binding: FragmentPipeBendingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PipeBendingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPipeBendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeUiState()
    }

    private fun setupListeners() {
        binding.radioGroupBendType.setOnCheckedChangeListener { _, checkedId ->
            val bendType = when (checkedId) {
                R.id.radioButtonSaddle -> BendType.SADDLE
                else -> BendType.OFFSET // Default to offset
            }
            viewModel.updateBendType(bendType)
        }

        binding.editTextOffsetAmount.addTextChangedListener {
            viewModel.updateOffsetAmount(it.toString())
        }
         binding.editTextObstacleDiameter.addTextChangedListener {
            viewModel.updateObstacleDiameter(it.toString())
        }
        binding.editTextAngle.addTextChangedListener {
            viewModel.updateAngle(it.toString())
        }
        binding.buttonCalculate.setOnClickListener { // Changed button ID
            viewModel.calculate() // Call general calculate method
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Update visibility based on bend type
                    binding.textFieldOffsetAmount.isVisible = state.bendType == BendType.OFFSET
                    binding.textFieldObstacleDiameter.isVisible = state.bendType == BendType.SADDLE
                    binding.groupOffsetResults.isVisible = state.bendType == BendType.OFFSET && state.offsetResult != null
                    binding.groupSaddleResults.isVisible = state.bendType == BendType.SADDLE && state.saddleResult != null

                    // Update input fields if they don't match state (e.g., on config change or type switch)
                    if (state.bendType == BendType.OFFSET && binding.editTextOffsetAmount.text.toString() != state.offsetAmount) {
                        binding.editTextOffsetAmount.setText(state.offsetAmount)
                    }
                    if (state.bendType == BendType.SADDLE && binding.editTextObstacleDiameter.text.toString() != state.obstacleDiameter) {
                         binding.editTextObstacleDiameter.setText(state.obstacleDiameter)
                    }
                    if (binding.editTextAngle.text.toString() != state.angle) {
                        binding.editTextAngle.setText(state.angle)
                    }

                    // Update result fields
                    val df = DecimalFormat("#.###") // Format to 3 decimal places

                    // Offset Results
                    state.offsetResult?.let { result ->
                        if (state.bendType == BendType.OFFSET) {
                            binding.textViewMultiplierValue.text = df.format(result.multiplier)
                            binding.textViewShrinkValue.text = "${df.format(result.shrink)} inches"
                            binding.textViewTravelValue.text = "${df.format(result.travel)} inches"
                            binding.textViewDistanceValue.text = "${df.format(result.distanceBetweenBends)} inches"
                        }
                    } ?: run {
                        if (state.bendType == BendType.OFFSET) clearOffsetResults()
                    }

                    // Saddle Results
                    state.saddleResult?.let { result ->
                         if (state.bendType == BendType.SADDLE) {
                            binding.textViewCenterMarkValue.text = "${df.format(result.centerMarkDistance)} inches"
                            binding.textViewSideMarkValue.text = "${df.format(result.sideMarkDistance)} inches"
                            // Optionally display saddle shrink if needed in UI
                         }
                    } ?: run {
                         if (state.bendType == BendType.SADDLE) clearSaddleResults()
                    }


                    // Show/hide error/info message
                    val message = state.errorMessage ?: state.infoMessage
                    if (message != null) {
                        binding.textViewError.text = message
                        binding.textViewError.visibility = View.VISIBLE
                        // TODO: Adjust error message position dynamically based on visible results if layout requires it.
                        // val topConstraintId = if (binding.groupOffsetResults.isVisible) R.id.textViewDistanceLabel else if (binding.groupSaddleResults.isVisible) R.id.textViewSideMarkLabel else R.id.buttonCalculate
                        // binding.textViewError.updateLayoutParams<ConstraintLayout.LayoutParams> { topToBottom = topConstraintId } // Example of dynamic constraint update
                    } else {
                        binding.textViewError.visibility = View.GONE
                    }
                }
            }
        }
    }

     private fun clearOffsetResults() {
        binding.textViewMultiplierValue.text = ""
        binding.textViewShrinkValue.text = ""
        binding.textViewTravelValue.text = ""
        binding.textViewDistanceValue.text = ""
    }

    private fun clearSaddleResults() {
        binding.textViewCenterMarkValue.text = ""
        binding.textViewSideMarkValue.text = ""
        // Clear saddle shrink if displayed
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
