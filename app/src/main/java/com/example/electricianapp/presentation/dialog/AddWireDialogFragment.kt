package com.example.electricianapp.presentation.dialog // Corrected package

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.electricianapp.R // Corrected import
import com.example.electricianapp.domain.model.conduitfill.Wire // Corrected import
import com.example.electricianapp.domain.model.conduitfill.WireType // Corrected import

// TODO: Consider using ViewBinding for the dialog layout instead of findViewById
class AddWireDialogFragment(
    private val onSave: (WireType, String, Int) -> Unit,
    private val initialWire: Wire? = null
) : DialogFragment() {

    private lateinit var wireTypeSpinner: Spinner
    private lateinit var wireSizeSpinner: Spinner
    private lateinit var quantityEditText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // TODO: Fix layout inflation and view finding
        // val inflater = requireActivity().layoutInflater
        // // TODO: Replace R.layout.dialog_add_wire with the actual layout file name if different
        // val view = inflater.inflate(R.layout.dialog_add_wire, null)

        // // Initialize views
        // // TODO: Replace R.id.* with actual IDs from your dialog_add_wire.xml layout
        // wireTypeSpinner = view.findViewById(R.id.wireTypeSpinner)
        // wireSizeSpinner = view.findViewById(R.id.wireSizeSpinner)
        // quantityEditText = view.findViewById(R.id.quantityEditText)

        // Setup spinners
        setupWireTypeSpinner()
        setupWireSizeSpinner()

        // // Set initial values if editing an existing wire
        // initialWire?.let {
        //     wireTypeSpinner.setSelection(WireType.values().indexOf(it.type))

        //     // Find the position of the wire size in the spinner
        //     // TODO: Replace R.array.wire_sizes with the actual array resource name if different
        //     val wireSizes = resources.getStringArray(R.array.wire_sizes)
        //     val position = wireSizes.indexOf(it.size)
        //     if (position >= 0) {
        //         wireSizeSpinner.setSelection(position)
        //     }

        //     quantityEditText.setText(it.quantity.toString())
        // }

        // Build the dialog
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (initialWire == null) "Add Conductor" else "Edit Conductor")
            // .setView(view) // Comment out view setting
            .setMessage("Dialog UI needs layout (dialog_add_wire.xml)") // Placeholder message
            .setPositiveButton("Save", null) // Set to null to override default behavior
            .setNegativeButton("Cancel") { _, _ -> dismiss() }

        val dialog = builder.create()

        // Override the positive button click to validate input before dismissing
        // TODO: Re-enable validation and saving when UI is fixed
        // dialog.setOnShowListener {
        //     val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        //     positiveButton.setOnClickListener {
        //         if (validateInput()) {
        //             val wireType = WireType.values()[wireTypeSpinner.selectedItemPosition]
        //             val wireSize = wireSizeSpinner.selectedItem.toString()
        //             val quantity = quantityEditText.text.toString().toIntOrNull() ?: 1

        //             onSave(wireType, wireSize, quantity)
        //             dismiss()
        //         }
        //     }
        // }

        return dialog
    }

    // TODO: Fix spinner setup when UI is available
    private fun setupWireTypeSpinner() {
        // val wireTypes = WireType.values().map { it.name }
        // val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, wireTypes)
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // wireTypeSpinner.adapter = adapter
    }

    private fun setupWireSizeSpinner() {
         // TODO: Replace R.array.wire_sizes with the actual array resource name if different
         // TODO: Fix resource lookup and spinner setup
        // val wireSizes = resources.getStringArray(R.array.wire_sizes)
        // val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, wireSizes)
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // wireSizeSpinner.adapter = adapter
    }

    // TODO: Re-enable validation when UI is fixed
    // private fun validateInput(): Boolean {
    //     val quantity = quantityEditText.text.toString().toIntOrNull()
    //     if (quantity == null || quantity <= 0) {
    //         showError("Please enter valid quantity (must be > 0)")
    //         return false
    //     }
    //     // Add more validation if needed (e.g., check if spinners have valid selections)
    //     return true
    // }

    private fun showError(message: String) {
        // Check context availability before showing Toast
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}
