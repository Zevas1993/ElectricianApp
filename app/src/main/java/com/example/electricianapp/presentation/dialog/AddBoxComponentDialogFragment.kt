package com.example.electricianapp.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.electricianapp.R
import com.example.electricianapp.databinding.DialogAddBoxComponentBinding
import com.example.electricianapp.domain.model.boxfill.BoxComponent
import com.example.electricianapp.domain.model.boxfill.ComponentType

class AddBoxComponentDialogFragment(
    private val componentTypes: List<ComponentType>,
    private val wireSizes: List<String>, // e.g., ["14", "12", "10", "8", "6"]
    private val onSave: (component: BoxComponent) -> Unit,
    private val initialComponent: BoxComponent? = null
) : DialogFragment() {

    private var _binding: DialogAddBoxComponentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddBoxComponentBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupComponentTypeDropdown()
        setupWireSizeDropdown()
        setupButtons()

        initialComponent?.let {
            binding.dialogTitle.text = "Edit Component"
            binding.componentTypeAutoCompleteTextView.setText(it.type.name, false)
            binding.quantityEditText.setText(it.quantity.toString())
            if (requiresWireSize(it.type)) {
                binding.wireSizeInputLayout.isVisible = true
                binding.wireSizeAutoCompleteTextView.setText(it.wireSize ?: "", false)
            } else {
                binding.wireSizeInputLayout.isVisible = false
            }
        } ?: run {
            binding.dialogTitle.text = "Add Component"
            // Set default selection if adding new
             if (componentTypes.isNotEmpty()) {
                 binding.componentTypeAutoCompleteTextView.setText(componentTypes.first().name, false)
                 updateWireSizeVisibility(componentTypes.first())
             }
        }
    }

    private fun setupComponentTypeDropdown() {
        val typeNames = componentTypes.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, typeNames)
        (binding.componentTypeInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        // Add listener to show/hide wire size based on selection
        (binding.componentTypeInputLayout.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedType = componentTypes[position]
                updateWireSizeVisibility(selectedType)
            }
    }

    private fun setupWireSizeDropdown() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, wireSizes)
        (binding.wireSizeInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun updateWireSizeVisibility(type: ComponentType) {
        binding.wireSizeInputLayout.isVisible = requiresWireSize(type)
        if (!binding.wireSizeInputLayout.isVisible) {
            binding.wireSizeAutoCompleteTextView.setText("", false) // Clear wire size if not needed
        } else if (binding.wireSizeAutoCompleteTextView.text.isBlank() && wireSizes.isNotEmpty()) {
             // Set default wire size if becoming visible and empty
             binding.wireSizeAutoCompleteTextView.setText(wireSizes.first(), false)
        }
    }

    // Helper to determine if a component type requires a wire size
    private fun requiresWireSize(type: ComponentType): Boolean {
        return when (type) {
            ComponentType.CONDUCTOR,
            ComponentType.DEVICE, // Volume based on largest conductor connected to device
            ComponentType.CLAMP, // Volume based on largest conductor in box
            ComponentType.SUPPORT_FITTING, // Volume based on largest conductor in box
            ComponentType.EQUIPMENT_GROUNDING_CONDUCTOR -> true // Volume based on largest EGC
            // Add other types if they require size
            // else -> false // Not needed as ComponentType is exhaustive for now
        }
    }


    private fun setupButtons() {
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.saveButton.setOnClickListener {
            val typeString = binding.componentTypeAutoCompleteTextView.text.toString()
            val quantityString = binding.quantityEditText.text.toString()
            val wireSizeString = binding.wireSizeAutoCompleteTextView.text.toString()

            if (typeString.isBlank() || quantityString.isBlank()) {
                Toast.makeText(requireContext(), "Component Type and Quantity are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val componentType = componentTypes.find { it.name == typeString }
            if (componentType == null) {
                Toast.makeText(requireContext(), "Invalid Component Type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = quantityString.toIntOrNull()
            if (quantity == null || quantity <= 0) {
                binding.quantityInputLayout.error = "Quantity must be positive"
                return@setOnClickListener
            } else {
                binding.quantityInputLayout.error = null
            }

            var wireSize: String? = null
            if (requiresWireSize(componentType)) {
                if (wireSizeString.isBlank() || !wireSizes.contains(wireSizeString)) {
                     binding.wireSizeInputLayout.error = "Valid wire size required for this component"
                     return@setOnClickListener
                } else {
                     binding.wireSizeInputLayout.error = null
                     wireSize = wireSizeString
                }
            } else {
                 binding.wireSizeInputLayout.error = null // Clear error if not required
            }


            // Use wireSize!! because the requiresWireSize check ensures it's non-null here
            // Or use wireSize ?: "" if a default empty string is acceptable when not required,
            // but the domain model expects a non-null String.
            val component = BoxComponent(
                type = componentType,
                wireSize = wireSize!!,
                quantity = quantity
            )

            onSave(component)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
