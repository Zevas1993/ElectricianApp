package com.example.electricianapp.presentation.fragment.materials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.electricianapp.databinding.DialogMaterialFilterBinding
import com.example.electricianapp.domain.model.materials.MaterialCategory
import com.example.electricianapp.presentation.viewmodel.materials.MaterialListViewModel

/**
 * Dialog fragment for filtering materials by category
 */
class MaterialFilterDialogFragment : DialogFragment() {
    
    private var _binding: DialogMaterialFilterBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MaterialListViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMaterialFilterBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupCategoryOptions()
        setupButtons()
    }
    
    private fun setupCategoryOptions() {
        // Set up radio buttons for each category
        MaterialCategory.values().forEachIndexed { index, category ->
            val radioButton = binding.radioGroupCategories.getChildAt(index)
            radioButton.tag = category
            (radioButton as android.widget.RadioButton).text = category.name.replace("_", " ")
        }
    }
    
    private fun setupButtons() {
        binding.buttonApplyFilter.setOnClickListener {
            val selectedId = binding.radioGroupCategories.checkedRadioButtonId
            if (selectedId != -1) {
                val radioButton = binding.root.findViewById<android.widget.RadioButton>(selectedId)
                val category = radioButton.tag as MaterialCategory
                viewModel.loadMaterialsByCategory(category)
            }
            dismiss()
        }
        
        binding.buttonClearFilter.setOnClickListener {
            viewModel.loadMaterials()
            dismiss()
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
