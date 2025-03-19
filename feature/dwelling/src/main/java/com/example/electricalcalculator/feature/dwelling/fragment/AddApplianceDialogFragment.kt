package com.example.electricalcalculator.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.electricalcalculator.R
import com.example.electricalcalculator.domain.model.dwellingload.Appliance

class AddApplianceDialogFragment(
    private val onSave: (Appliance) -> Unit,
    private val initialAppliance: Appliance? = null
) : DialogFragment() {

    private lateinit var nameEditText: EditText
    private lateinit var wattageEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var demandFactorEditText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_appliance, null)

        // Initialize views
        nameEditText = view.findViewById(R.id.nameEditText)
        wattageEditText = view.findViewById(R.id.wattageEditText)
        quantityEditText = view.findViewById(R.id.quantityEditText)
        demandFactorEditText = view.findViewById(R.id.demandFactorEditText)

        // Set initial values if editing an existing appliance
        initialAppliance?.let {
            nameEditText.setText(it.name)
            wattageEditText.setText(it.wattage.toString())
            quantityEditText.setText(it.quantity.toString())
            demandFactorEditText.setText((it.demandFactor * 100).toString())
        }

        // Build the dialog
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (initialAppliance == null) "Add Appliance" else "Edit Appliance")
            .setView(view)
            .setPositiveButton("Save", null) // Set to null to override default behavior
            .setNegativeButton("Cancel") { _, _ -> dismiss() }

        val dialog = builder.create()
        
        // Override the positive button click to validate input before dismissing
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                if (validateInput()) {
                    val name = nameEditText.text.toString()
                    val wattage = wattageEditText.text.toString().toDouble()
                    val quantity = quantityEditText.text.toString().toIntOrNull() ?: 1
                    val demandFactor = demandFactorEditText.text.toString().toDoubleOrNull()?.div(100) ?: 1.0

                    val appliance = Appliance(
                        name = name,
                        wattage = wattage,
                        quantity = quantity,
                        demandFactor = demandFactor
                    )

                    onSave(appliance)
                    dismiss()
                }
            }
        }

        return dialog
    }

    private fun validateInput(): Boolean {
        val name = nameEditText.text.toString()
        if (name.isBlank()) {
            showError("Please enter appliance name")
            return false
        }

        val wattage = wattageEditText.text.toString().toDoubleOrNull()
        if (wattage == null || wattage <= 0) {
            showError("Please enter valid wattage")
            return false
        }

        val quantity = quantityEditText.text.toString().toIntOrNull()
        if (quantity == null || quantity <= 0) {
            showError("Please enter valid quantity")
            return false
        }

        val demandFactor = demandFactorEditText.text.toString().toDoubleOrNull()
        if (demandFactor == null || demandFactor <= 0 || demandFactor > 100) {
            showError("Please enter valid demand factor (0-100)")
            return false
        }

        return true
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
