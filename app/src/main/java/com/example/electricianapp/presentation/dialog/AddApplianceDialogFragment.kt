package com.example.electricianapp.presentation.dialog // Corrected package

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.electricianapp.R // Corrected import
import com.example.electricianapp.databinding.DialogAddApplianceBinding // Import ViewBinding
import com.example.electricianapp.domain.model.dwellingload.Appliance // Corrected import

class AddApplianceDialogFragment(
    private val onSave: (Appliance) -> Unit,
    private val initialAppliance: Appliance? // Remove default null value
) : DialogFragment() {

    // Use ViewBinding
    private var _binding: DialogAddApplianceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddApplianceBinding.inflate(LayoutInflater.from(context))

        // Set initial values if editing an existing appliance
        initialAppliance?.let {
            binding.nameEditText.setText(it.name)
            binding.wattageEditText.setText(it.wattage.toString())
            binding.quantityEditText.setText(it.quantity.toString())
            // Display demand factor as percentage (e.g., 0.75 becomes 75)
            binding.demandFactorEditText.setText((it.demandFactor * 100).toInt().toString())
        }

        // Build the dialog
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (initialAppliance == null) "Add Appliance" else "Edit Appliance")
            .setView(binding.root) // Set the view using ViewBinding
            .setPositiveButton("Save", null) // Set to null to override default behavior
            .setNegativeButton("Cancel") { _, _ -> dismiss() }

        val dialog = builder.create()

        // Override the positive button click to validate input before dismissing
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                if (validateInput()) {
                    val name = binding.nameEditText.text.toString().trim()
                    val wattage = binding.wattageEditText.text.toString().toDoubleOrNull() ?: 0.0
                    val quantity = binding.quantityEditText.text.toString().toIntOrNull() ?: 1
                    // Convert percentage back to decimal (e.g., 75 becomes 0.75)
                    val demandFactor = binding.demandFactorEditText.text.toString().toDoubleOrNull()?.div(100.0) ?: 1.0

                    val appliance = Appliance(
                        name = name,
                        wattage = wattage,
                        quantity = quantity,
                        demandFactor = demandFactor.coerceIn(0.0, 1.0) // Ensure demand factor is between 0 and 1
                    )

                    onSave(appliance)
                    dismiss()
                }
            }
        }

        return dialog
    }

    // Re-enable validation
    private fun validateInput(): Boolean {
        val name = binding.nameEditText.text.toString().trim()
        if (name.isBlank()) {
            showError("Please enter appliance name")
            return false
        }

        val wattage = binding.wattageEditText.text.toString().toDoubleOrNull()
        if (wattage == null || wattage <= 0) {
            showError("Please enter valid positive wattage")
            return false
        }

        val quantity = binding.quantityEditText.text.toString().toIntOrNull()
        if (quantity == null || quantity <= 0) {
            showError("Please enter valid positive quantity")
            return false
        }

        val demandFactorPercent = binding.demandFactorEditText.text.toString().toDoubleOrNull()
        if (demandFactorPercent == null || demandFactorPercent < 0 || demandFactorPercent > 100) {
            showError("Please enter valid demand factor (0-100)")
            return false
        }

        return true
    }

    private fun showError(message: String) {
        // Check context availability before showing Toast
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Clean up binding in onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
