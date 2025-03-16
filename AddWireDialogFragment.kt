package com.example.electricalcalculator.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.electricalcalculator.R
import com.example.electricalcalculator.domain.model.conduitfill.Wire
import com.example.electricalcalculator.domain.model.conduitfill.WireType

class AddWireDialogFragment(
    private val onSave: (WireType, String, Int) -> Unit,
    private val initialWire: Wire? = null
) : DialogFragment() {

    private lateinit var wireTypeSpinner: Spinner
    private lateinit var wireSizeSpinner: Spinner
    private lateinit var quantityEditText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_wire, null)

        // Initialize views
        wireTypeSpinner = view.findViewById(R.id.wireTypeSpinner)
        wireSizeSpinner = view.findViewById(R.id.wireSizeSpinner)
        quantityEditText = view.findViewById(R.id.quantityEditText)

        // Setup spinners
        setupWireTypeSpinner()
        setupWireSizeSpinner()

        // Set initial values if editing an existing wire
        initialWire?.let {
            wireTypeSpinner.setSelection(WireType.values().indexOf(it.type))
            
            // Find the position of the wire size in the spinner
            val wireSizes = resources.getStringArray(R.array.wire_sizes)
            val position = wireSizes.indexOf(it.size)
            if (position >= 0) {
                wireSizeSpinner.setSelection(position)
            }
            
            quantityEditText.setText(it.quantity.toString())
        }

        // Build the dialog
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (initialWire == null) "Add Conductor" else "Edit Conductor")
            .setView(view)
            .setPositiveButton("Save", null) // Set to null to override default behavior
            .setNegativeButton("Cancel") { _, _ -> dismiss() }

        val dialog = builder.create()
        
        // Override the positive button click to validate input before dismissing
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                if (validateInput()) {
                    val wireType = WireType.values()[wireTypeSpinner.selectedItemPosition]
                    val wireSize = wireSizeSpinner.selectedItem.toString()
                    val quantity = quantityEditText.text.toString().toIntOrNull() ?: 1

                    onSave(wireType, wireSize, quantity)
                    dismiss()
                }
            }
        }

        return dialog
    }

    private fun setupWireTypeSpinner() {
        val wireTypes = WireType.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, wireTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        wireTypeSpinner.adapter = adapter
    }

    private fun setupWireSizeSpinner() {
        val wireSizes = resources.getStringArray(R.array.wire_sizes)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, wireSizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        wireSizeSpinner.adapter = adapter
    }

    private fun validateInput(): Boolean {
        val quantity = quantityEditText.text.toString().toIntOrNull()
        if (quantity == null || quantity <= 0) {
            showError("Please enter valid quantity")
            return false
        }

        return true
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
