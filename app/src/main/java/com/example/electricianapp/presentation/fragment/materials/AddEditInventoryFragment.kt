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
import com.example.electricianapp.databinding.FragmentAddEditInventoryBinding
import com.example.electricianapp.domain.model.materials.Material
import com.example.electricianapp.domain.model.materials.MaterialInventory
import com.example.electricianapp.presentation.viewmodel.materials.AddEditInventoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import java.util.UUID

/**
 * Fragment for adding or editing a material inventory item
 */
@AndroidEntryPoint
class AddEditInventoryFragment : Fragment() {
    
    private var _binding: FragmentAddEditInventoryBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AddEditInventoryViewModel by viewModels()
    private val args: AddEditInventoryFragmentArgs by navArgs()
    
    private var isEditMode = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        isEditMode = args.inventoryId != null
        
        setupMaterialDropdown()
        setupObservers()
        setupListeners()
        
        // Load data if in edit mode
        if (isEditMode) {
            viewModel.loadInventoryItem(args.inventoryId!!)
        } else {
            // In add mode, just load materials for dropdown
            viewModel.loadMaterials()
        }
    }
    
    private fun setupMaterialDropdown() {
        viewModel.materials.observe(viewLifecycleOwner) { materials ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                materials.map { it.name }
            )
            binding.autoCompleteMaterial.setAdapter(adapter)
            
            // Store the materials for later use
            binding.autoCompleteMaterial.setOnItemClickListener { _, _, position, _ ->
                viewModel.selectMaterial(materials[position])
            }
        }
    }
    
    private fun setupObservers() {
        viewModel.selectedInventory.observe(viewLifecycleOwner) { inventory ->
            inventory?.let {
                // Fill form with inventory data
                binding.autoCompleteMaterial.setText(it.material?.name ?: "", false)
                binding.editTextQuantity.setText(it.quantity.toString())
                binding.editTextMinimumQuantity.setText(it.minimumQuantity.toString())
                binding.editTextLocation.setText(it.location)
                binding.editTextNotes.setText(it.notes)
                
                // Disable material selection in edit mode
                binding.autoCompleteMaterial.isEnabled = false
            }
        }
        
        viewModel.selectedMaterial.observe(viewLifecycleOwner) { material ->
            // Update unit of measure display
            binding.textViewUnitOfMeasure.text = material?.unitOfMeasure?.name ?: ""
            binding.textViewMinimumUnitOfMeasure.text = material?.unitOfMeasure?.name ?: ""
        }
        
        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(
                    requireContext(),
                    if (isEditMode) "Inventory updated successfully" else "Inventory added successfully",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonSave.isEnabled = !isLoading
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                binding.textViewError.text = errorMessage
                binding.textViewError.visibility = View.VISIBLE
            } else {
                binding.textViewError.visibility = View.GONE
            }
        }
    }
    
    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            saveInventory()
        }
        
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun saveInventory() {
        // Validate input
        val materialName = binding.autoCompleteMaterial.text.toString()
        val quantityStr = binding.editTextQuantity.text.toString()
        val minimumQuantityStr = binding.editTextMinimumQuantity.text.toString()
        val location = binding.editTextLocation.text.toString()
        val notes = binding.editTextNotes.text.toString()
        
        // Validate material
        if (materialName.isBlank() || viewModel.selectedMaterial.value == null) {
            binding.textInputLayoutMaterial.error = "Please select a material"
            return
        }
        
        // Validate quantity
        if (quantityStr.isBlank()) {
            binding.textInputLayoutQuantity.error = "Quantity is required"
            return
        }
        
        val quantity = quantityStr.toDoubleOrNull()
        if (quantity == null || quantity < 0) {
            binding.textInputLayoutQuantity.error = "Invalid quantity"
            return
        }
        
        // Validate minimum quantity
        if (minimumQuantityStr.isBlank()) {
            binding.textInputLayoutMinimumQuantity.error = "Minimum quantity is required"
            return
        }
        
        val minimumQuantity = minimumQuantityStr.toDoubleOrNull()
        if (minimumQuantity == null || minimumQuantity < 0) {
            binding.textInputLayoutMinimumQuantity.error = "Invalid minimum quantity"
            return
        }
        
        // Clear errors
        binding.textInputLayoutMaterial.error = null
        binding.textInputLayoutQuantity.error = null
        binding.textInputLayoutMinimumQuantity.error = null
        
        // Get selected material
        val material = viewModel.selectedMaterial.value ?: return
        
        if (isEditMode) {
            // Update existing inventory
            val existingInventory = viewModel.selectedInventory.value ?: return
            val updatedInventory = existingInventory.copy(
                quantity = quantity,
                minimumQuantity = minimumQuantity,
                location = location,
                notes = notes,
                lastUpdated = Date()
            )
            
            viewModel.updateInventory(updatedInventory)
        } else {
            // Create new inventory
            val newInventory = MaterialInventory(
                id = UUID.randomUUID().toString(),
                materialId = material.id,
                material = material,
                quantity = quantity,
                minimumQuantity = minimumQuantity,
                location = location,
                notes = notes,
                lastUpdated = Date()
            )
            
            viewModel.saveInventory(newInventory)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
