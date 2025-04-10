package com.example.electricianapp.presentation.fragment.materials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.electricianapp.databinding.FragmentAddEditMaterialBinding
import com.example.electricianapp.domain.model.materials.MaterialCategory
import com.example.electricianapp.domain.model.materials.UnitOfMeasure
import com.example.electricianapp.presentation.viewmodel.materials.AddEditMaterialViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for adding or editing a material
 */
@AndroidEntryPoint
class AddEditMaterialFragment : Fragment() {
    
    private var _binding: FragmentAddEditMaterialBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AddEditMaterialViewModel by viewModels()
    private val args: AddEditMaterialFragmentArgs by navArgs()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditMaterialBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupCategorySpinner()
        setupUnitOfMeasureSpinner()
        setupObservers()
        setupListeners()
        
        // If materialId is not null, load material for editing
        args.materialId?.let { materialId ->
            viewModel.loadMaterial(materialId)
        }
    }
    
    private fun setupCategorySpinner() {
        val categories = MaterialCategory.values().map { it.name.replace("_", " ") }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }
    
    private fun setupUnitOfMeasureSpinner() {
        val units = UnitOfMeasure.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUnitOfMeasure.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.material.observe(viewLifecycleOwner) { material ->
            material?.let {
                binding.editTextName.setText(it.name)
                binding.editTextDescription.setText(it.description)
                binding.spinnerCategory.setSelection(MaterialCategory.values().indexOf(it.category))
                binding.editTextPartNumber.setText(it.partNumber)
                binding.editTextManufacturer.setText(it.manufacturer)
                binding.spinnerUnitOfMeasure.setSelection(UnitOfMeasure.values().indexOf(it.unitOfMeasure))
                binding.editTextUnitPrice.setText(it.unitPrice.toString())
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }
        }
        
        viewModel.saveComplete.observe(viewLifecycleOwner) { complete ->
            if (complete) {
                findNavController().navigateUp()
            }
        }
    }
    
    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            saveMaterial()
        }
        
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun saveMaterial() {
        val name = binding.editTextName.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val categoryPosition = binding.spinnerCategory.selectedItemPosition
        val partNumber = binding.editTextPartNumber.text.toString().trim()
        val manufacturer = binding.editTextManufacturer.text.toString().trim()
        val unitOfMeasurePosition = binding.spinnerUnitOfMeasure.selectedItemPosition
        val unitPriceText = binding.editTextUnitPrice.text.toString().trim()
        
        if (name.isEmpty()) {
            binding.editTextName.error = "Name is required"
            return
        }
        
        val unitPrice = if (unitPriceText.isEmpty()) 0.0 else unitPriceText.toDoubleOrNull() ?: 0.0
        
        viewModel.saveMaterial(
            name = name,
            description = description,
            category = MaterialCategory.values()[categoryPosition],
            partNumber = partNumber,
            manufacturer = manufacturer,
            unitOfMeasure = UnitOfMeasure.values()[unitOfMeasurePosition],
            unitPrice = unitPrice,
            imageUrl = null
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
