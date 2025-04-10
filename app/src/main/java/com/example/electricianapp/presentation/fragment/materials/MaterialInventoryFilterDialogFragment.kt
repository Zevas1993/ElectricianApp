package com.example.electricianapp.presentation.fragment.materials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.electricianapp.R
import com.example.electricianapp.databinding.DialogMaterialInventoryFilterBinding
import com.example.electricianapp.presentation.viewmodel.materials.MaterialInventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Dialog fragment for filtering material inventory
 */
@AndroidEntryPoint
class MaterialInventoryFilterDialogFragment : DialogFragment() {
    
    private var _binding: DialogMaterialInventoryFilterBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MaterialInventoryViewModel by activityViewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMaterialInventoryFilterBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupListeners()
    }
    
    private fun setupListeners() {
        binding.buttonApply.setOnClickListener {
            // Apply filters
            if (binding.checkBoxLowStock.isChecked) {
                viewModel.showLowStockItems()
            } else {
                viewModel.loadInventory()
            }
            
            dismiss()
        }
        
        binding.buttonReset.setOnClickListener {
            // Reset filters
            binding.checkBoxLowStock.isChecked = false
            
            // Load all inventory
            viewModel.loadInventory()
            
            dismiss()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
