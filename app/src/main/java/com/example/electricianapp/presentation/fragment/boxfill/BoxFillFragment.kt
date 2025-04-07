package com.example.electricianapp.presentation.fragment.boxfill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electricianapp.R
import com.example.electricianapp.databinding.FragmentBoxFillBinding
import com.example.electricianapp.domain.model.boxfill.BoxComponent
import com.example.electricianapp.domain.model.boxfill.BoxFillResult
import com.example.electricianapp.domain.model.boxfill.BoxType
import com.example.electricianapp.presentation.adapter.BoxComponentAdapter
import com.example.electricianapp.presentation.dialog.AddBoxComponentDialogFragment
import com.example.electricianapp.presentation.viewmodel.boxfill.BoxFillUiState
import com.example.electricianapp.presentation.viewmodel.boxfill.BoxFillViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class BoxFillFragment : Fragment(), BoxComponentAdapter.BoxComponentItemListener {

    private val viewModel: BoxFillViewModel by viewModels()
    private var _binding: FragmentBoxFillBinding? = null
    private val binding get() = _binding!!
    private lateinit var componentAdapter: BoxComponentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoxFillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBoxTypeSpinner()
        setupRecyclerView()
        setupInputListeners()
        setupButtons()
        observeViewModel()
    }

    private fun setupBoxTypeSpinner() {
        val types = viewModel.availableBoxTypes.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.boxTypeSpinner.adapter = adapter

        binding.boxTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setBoxType(viewModel.availableBoxTypes[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        // Set initial selection from ViewModel
        viewModel.boxType.value?.let {
            binding.boxTypeSpinner.setSelection(it.ordinal)
        }
    }

     private fun setupInputListeners() {
         binding.boxDimensionsEditText.doOnTextChanged { text, _, _, _ ->
             viewModel.setBoxDimensionsOrVolume(text.toString())
         }
         // Set initial value from ViewModel
         binding.boxDimensionsEditText.setText(viewModel.boxDimensionsOrVolume.value)
     }


    private fun setupRecyclerView() {
        componentAdapter = BoxComponentAdapter(this)
        binding.componentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = componentAdapter
        }
    }

    private fun setupButtons() {
        binding.addComponentButton.setOnClickListener {
            showAddComponentDialog()
        }
        binding.calculateButton.setOnClickListener {
            viewModel.calculateBoxFill()
        }
    }

    private fun showAddComponentDialog(component: BoxComponent? = null, position: Int = -1) {
        val dialog = AddBoxComponentDialogFragment(
            componentTypes = viewModel.availableComponentTypes,
            wireSizes = viewModel.availableWireSizes,
            onSave = { newOrUpdatedComponent ->
                if (position >= 0) {
                    viewModel.updateComponent(position, newOrUpdatedComponent)
                } else {
                    viewModel.addComponent(newOrUpdatedComponent)
                }
            },
            initialComponent = component
        )
        dialog.show(childFragmentManager, "AddBoxComponentDialog")
    }

    private fun observeViewModel() {
        viewModel.components.observe(viewLifecycleOwner) { components ->
            componentAdapter.submitList(components)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is BoxFillUiState.Success -> {
                        updateResultView(state.result)
                        binding.resultTextView.setTextColor(resources.getColor(R.color.design_default_color_on_surface, null))
                    }
                    is BoxFillUiState.Error -> {
                        binding.resultTextView.text = "Error: ${state.message}"
                        binding.resultTextView.setTextColor(resources.getColor(R.color.design_default_color_error, null))
                    }
                    is BoxFillUiState.Loading -> {
                        binding.resultTextView.text = "Calculating..."
                        binding.resultTextView.setTextColor(resources.getColor(R.color.design_default_color_on_surface, null))
                    }
                    is BoxFillUiState.Initial -> {
                        binding.resultTextView.text = "" // Clear previous results
                    }
                }
            }
        }
    }

    private fun updateResultView(result: BoxFillResult) {
        val df = DecimalFormat("#.##")
        val requiredVol = df.format(result.totalRequiredVolumeInCubicInches)
        val remainingVol = df.format(result.remainingVolumeInCubicInches)
        val fillPercent = df.format(result.fillPercentage)
        val status = if (result.isWithinLimits) "OK" else "OVERFILLED"
        val colorRes = if (result.isWithinLimits) R.color.design_default_color_primary else R.color.design_default_color_error

        binding.resultTextView.text = "Required: ${requiredVol} cu in | Remaining: ${remainingVol} cu in | Fill: ${fillPercent}% - $status"
        binding.resultTextView.setTextColor(resources.getColor(colorRes, null))
    }

    // BoxComponentAdapter.BoxComponentItemListener implementation
    override fun onEditComponent(position: Int, component: BoxComponent) {
        showAddComponentDialog(component, position)
    }

    override fun onRemoveComponent(position: Int) {
        viewModel.removeComponent(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
