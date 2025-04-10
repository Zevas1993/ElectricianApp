package com.example.electricianapp.presentation.fragment.materials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.electricianapp.R
import com.example.electricianapp.databinding.DialogMaterialInventoryAdjustmentBinding
import com.example.electricianapp.domain.model.materials.TransactionType
import com.example.electricianapp.presentation.viewmodel.materials.MaterialInventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Dialog fragment for adjusting material inventory quantities
 */
@AndroidEntryPoint
class MaterialInventoryAdjustmentDialogFragment : DialogFragment() {
    
    private var _binding: DialogMaterialInventoryAdjustmentBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MaterialInventoryViewModel by activityViewModels()
    
    private lateinit var inventoryId: String
    
    companion object {
        private const val ARG_INVENTORY_ID = "inventory_id"
        
        fun newInstance(inventoryId: String): MaterialInventoryAdjustmentDialogFragment {
            val fragment = MaterialInventoryAdjustmentDialogFragment()
            val args = Bundle()
            args.putString(ARG_INVENTORY_ID, inventoryId)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
        
        arguments?.let {
            inventoryId = it.getString(ARG_INVENTORY_ID) ?: throw IllegalArgumentException("Inventory ID is required")
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMaterialInventoryAdjustmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupTransactionTypeDropdown()
        setupObservers()
        setupListeners()
        
        // Load inventory item
        viewModel.loadInventoryItem(inventoryId)
    }
    
    private fun setupTransactionTypeDropdown() {
        val transactionTypes = arrayOf(
            "Purchase (Add)",
            "Use (Remove)",
            "Return to Inventory (Add)",
            "Return to Supplier (Remove)",
            "Adjustment"
        )
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, transactionTypes)
        binding.autoCompleteTransactionType.setAdapter(adapter)
        binding.autoCompleteTransactionType.setText(transactionTypes[0], false)
    }
    
    private fun setupObservers() {
        viewModel.selectedInventory.observe(viewLifecycleOwner) { inventory ->
            inventory?.let {
                binding.textViewMaterialName.text = it.material?.name ?: "Unknown Material"
                binding.textViewCurrentQuantity.text = "Current Quantity: ${it.quantity} ${it.material?.unitOfMeasure?.name ?: ""}"
            }
        }
        
        viewModel.transactionSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Inventory adjusted successfully", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                binding.textViewError.text = errorMessage
                binding.textViewError.visibility = View.VISIBLE
            } else {
                binding.textViewError.visibility = View.GONE
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonSave.isEnabled = !isLoading
        }
    }
    
    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            val quantityStr = binding.editTextQuantity.text.toString()
            val notes = binding.editTextNotes.text.toString()
            val transactionTypeStr = binding.autoCompleteTransactionType.text.toString()
            
            // Validate input
            if (quantityStr.isBlank()) {
                binding.textInputLayoutQuantity.error = "Quantity is required"
                return@setOnClickListener
            }
            
            val quantity = quantityStr.toDoubleOrNull()
            if (quantity == null || quantity <= 0) {
                binding.textInputLayoutQuantity.error = "Invalid quantity"
                return@setOnClickListener
            }
            
            // Clear error
            binding.textInputLayoutQuantity.error = null
            
            // Determine transaction type
            val transactionType = when (transactionTypeStr) {
                "Purchase (Add)" -> TransactionType.PURCHASE
                "Use (Remove)" -> TransactionType.USE
                "Return to Inventory (Add)" -> TransactionType.RETURN_TO_INVENTORY
                "Return to Supplier (Remove)" -> TransactionType.RETURN_TO_SUPPLIER
                "Adjustment" -> TransactionType.ADJUSTMENT
                else -> TransactionType.ADJUSTMENT
            }
            
            // Adjust inventory
            viewModel.adjustInventory(inventoryId, quantity, notes, transactionType)
        }
        
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
