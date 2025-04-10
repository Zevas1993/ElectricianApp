package com.example.electricianapp.presentation.fragment.materials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.electricianapp.databinding.FragmentMaterialDetailBinding
import com.example.electricianapp.presentation.viewmodel.materials.MaterialDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for displaying material details
 */
@AndroidEntryPoint
class MaterialDetailFragment : Fragment() {
    
    private var _binding: FragmentMaterialDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MaterialDetailViewModel by viewModels()
    private val args: MaterialDetailFragmentArgs by navArgs()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaterialDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Load material details
        viewModel.loadMaterial(args.materialId)
        
        setupObservers()
        setupListeners()
    }
    
    private fun setupObservers() {
        viewModel.material.observe(viewLifecycleOwner) { material ->
            material?.let {
                binding.textViewMaterialName.text = it.name
                binding.textViewMaterialDescription.text = it.description
                binding.textViewMaterialCategory.text = it.category.name.replace("_", " ")
                binding.textViewMaterialPartNumber.text = it.partNumber
                binding.textViewMaterialManufacturer.text = it.manufacturer
                binding.textViewMaterialUnitOfMeasure.text = it.unitOfMeasure.name
                binding.textViewMaterialUnitPrice.text = "$${it.unitPrice}"
                
                // Load image if available
                it.imageUrl?.let { url ->
                    // Use an image loading library like Glide or Coil
                    // For now, we'll just set a placeholder
                    // binding.imageViewMaterial.setImageResource(R.drawable.placeholder_material)
                }
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                binding.errorTextView.text = errorMessage
                binding.errorTextView.visibility = View.VISIBLE
            } else {
                binding.errorTextView.visibility = View.GONE
            }
        }
    }
    
    private fun setupListeners() {
        binding.fabEditMaterial.setOnClickListener {
            val action = MaterialDetailFragmentDirections.actionMaterialDetailFragmentToAddEditMaterialFragment(args.materialId)
            findNavController().navigate(action)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
