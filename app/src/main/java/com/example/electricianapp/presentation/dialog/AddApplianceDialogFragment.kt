package com.example.electricianapp.presentation.dialog // Corrected package

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.electricianapp.R // Corrected import
import com.example.electricianapp.domain.model.dwellingload.Appliance // Corrected import

// TODO: Consider using ViewBinding for the dialog layout instead of findViewById
class AddApplianceDialogFragment(
    private val onSave: (Appliance) -> Unit,
    private val initialAppliance: Appliance? // Remove default null value
) : DialogFragment() {

    private lateinit var nameEditText: EditText
    private lateinit var wattageEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var demandFactorEditText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // TODO: Fix layout inflation and view finding
        // val inflater = requireActivity().layoutInflater
        // // TODO: Replace R.layout.dialog_add_appliance with the actual layout file name if different
        // val view = inflater.inflate(R.layout.dialog_add_appliance, null)

        // // Initialize views
        // // TODO: Replace R.id.* with actual IDs from your dialog_add_appliance.xml layout
        // nameEditText = view.findViewById(R.id.nameEditText)
        // wattageEditText = view.findViewById(R.id.wattageEditText)
        // quantityEditText = view.findViewById(R.id.quantityEditText)
        // demandFactorEditText = view.findViewById(R.id.demandFactorEditText)

        // // Set initial values if editing an existing appliance
        // initialAppliance?.let {
        //     nameEditText.setText(it.name)
        //     wattageEditText.setText(it.wattage.toString())
        //     quantityEditText.setText(it.quantity.toString())
        //     // Display demand factor as percentage (e.g., 0.75 becomes 75)
        //     demandFactorEditText.setText((it.demandFactor * 100).toInt().toString())
        // }

        // Build the dialog
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (initialAppliance == null) "Add Appliance" else "Edit Appliance")
            // .setView(view) // Comment out view setting
            .setMessage("Dialog UI needs layout (dialog_add_appliance.xml)") // Placeholder message
            .setPositiveButton("Save", null) // Set to null to override default behavior
            .setNegativeButton("Cancel") { _, _ -> dismiss() }

        val dialog = builder.create()

        // Override the positive button click to validate input before dismissing
        // TODO: Re-enable validation and saving when UI is fixed
        // dialog.setOnShowListener {
        //     val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        //     positiveButton.setOnClickListener {
        //         if (validateInput()) {
        //             val name = nameEditText.text.toString().trim()
        //             val wattage = wattageEditText.text.toString().toDoubleOrNull() ?: 0.0
        //             val quantity = quantityEditText.text.toString().toIntOrNull() ?: 1
        //             // Convert percentage back to decimal (e.g., 75 becomes 0.75)
        //             val demandFactor = demandFactorEditText.text.toString().toDoubleOrNull()?.div(100.0) ?: 1.0

        //             val appliance = Appliance(
        //                 name = name,
        //                 wattage = wattage,
        //                 quantity = quantity,
        //                 demandFactor = demandFactor.coerceIn(0.0, 1.0) // Ensure demand factor is between 0 and 1
        //             )

        //             onSave(appliance)
        //             dismiss()
        //         }
        //     }
        // }

        return dialog
    }

    // TODO: Re-enable validation when UI is fixed
    // private fun validateInput(): Boolean {
    //     val name = nameEditText.text.toString().trim()
    //     if (name.isBlank()) {
    //         showError("Please enter appliance name")
    //         return false
    //     }

    //     val wattage = wattageEditText.text.toString().toDoubleOrNull()
    //     if (wattage == null || wattage <= 0) {
    //         showError("Please enter valid positive wattage")
    //         return false
    //     }

    //     val quantity = quantityEditText.text.toString().toIntOrNull()
    //     if (quantity == null || quantity <= 0) {
    //         showError("Please enter valid positive quantity")
    //         return false
    //     }

    //     val demandFactorPercent = demandFactorEditText.text.toString().toDoubleOrNull()
    //     if (demandFactorPercent == null || demandFactorPercent < 0 || demandFactorPercent > 100) {
    //         showError("Please enter valid demand factor (0-100)")
    //         return false
    //     }

    //     return true
    // }

    private fun showError(message: String) {
         // Check context availability before showing Toast
         context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
         }
    }
}
