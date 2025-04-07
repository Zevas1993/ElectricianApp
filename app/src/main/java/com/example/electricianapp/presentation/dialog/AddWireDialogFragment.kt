package com.example.electricianapp.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.electricianapp.R
import com.example.electricianapp.databinding.DialogAddWireBinding
import com.example.electricianapp.domain.model.conduitfill.Wire
import com.example.electricianapp.domain.model.conduitfill.WireType

class AddWireDialogFragment(
    private val wireSizes: List<String>, // Pass the list of valid sizes
    private val onSave: (wireType: WireType, wireSize: String, quantity: Int) -> Unit,
    private val initialWire: Wire? = null
) : DialogFragment() {

    private var _binding: DialogAddWireBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddWireBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent) // Optional: for rounded corners if theme supports it
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWireTypeDropdown()
        setupWireSizeDropdown()
        setupButtons()

        // Populate fields if editing
        initialWire?.let {
            binding.dialogTitle.text = "Edit Wire"
            binding.wireTypeAutoCompleteTextView.setText(it.type.name, false)
            binding.wireSizeAutoCompleteTextView.setText(it.size, false) // Use the size string directly
            binding.quantityEditText.setText(it.quantity.toString())
        } ?: run {
            binding.dialogTitle.text = "Add Wire"
        }
    }

    private fun setupWireTypeDropdown() {
        val wireTypes = WireType.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, wireTypes)
        (binding.wireTypeInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        // Set default selection if not editing
        if (initialWire == null) {
             (binding.wireTypeInputLayout.editText as? AutoCompleteTextView)?.setText(WireType.THHN.name, false)
        }
    }

     private fun setupWireSizeDropdown() {
        // Use the provided wireSizes list
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, wireSizes)
        (binding.wireSizeInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
         // Set default selection if not editing
         if (initialWire == null && wireSizes.isNotEmpty()) {
             (binding.wireSizeInputLayout.editText as? AutoCompleteTextView)?.setText(wireSizes.first(), false)
         }
    }


    private fun setupButtons() {
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.saveButton.setOnClickListener {
            val typeString = binding.wireTypeAutoCompleteTextView.text.toString()
            val sizeString = binding.wireSizeAutoCompleteTextView.text.toString()
            val quantityString = binding.quantityEditText.text.toString()

            if (typeString.isBlank() || sizeString.isBlank() || quantityString.isBlank()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = quantityString.toIntOrNull()
            if (quantity == null || quantity <= 0) {
                binding.quantityInputLayout.error = "Quantity must be a positive number"
                return@setOnClickListener
            } else {
                binding.quantityInputLayout.error = null // Clear error
            }

            // Find the corresponding enum value
            val wireType = WireType.values().find { it.name == typeString }
            if (wireType == null) {
                 Toast.makeText(requireContext(), "Invalid wire type selected", Toast.LENGTH_SHORT).show()
                 return@setOnClickListener
            }

            // Validate selected size against the provided list
            if (!wireSizes.contains(sizeString)) {
                 Toast.makeText(requireContext(), "Invalid wire size selected", Toast.LENGTH_SHORT).show()
                 return@setOnClickListener
            }


            onSave(wireType, sizeString, quantity)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
