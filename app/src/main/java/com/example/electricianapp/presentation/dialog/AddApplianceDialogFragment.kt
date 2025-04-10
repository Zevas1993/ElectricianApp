package com.example.electricianapp.presentation.dialog // Corrected package

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.electricianapp.R
import com.example.electricianapp.databinding.DialogAddApplianceBinding
import com.example.electricianapp.domain.model.dwellingload.Appliance

class AddApplianceDialogFragment(
    private val onSave: (Appliance) -> Unit,
    private val initialAppliance: Appliance?
) : DialogFragment() {

    private var _binding: DialogAddApplianceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddApplianceBinding.inflate(inflater, container, false)
        // Optional: Set background for rounded corners if theme supports it
        // dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set title (optional, could be done via arguments or removed if layout has title)
        dialog?.setTitle(if (initialAppliance == null) "Add Appliance" else "Edit Appliance")

        // Set initial values if editing
        initialAppliance?.let {
            binding.nameEditText.setText(it.name)
            binding.wattageEditText.setText(it.wattage.toString())
            binding.quantityEditText.setText(it.quantity.toString())
            binding.demandFactorEditText.setText((it.demandFactor * 100).toInt().toString())
        }

        setupButtons()
    }

    private fun setupButtons() {
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.saveButton.setOnClickListener {
            if (validateInput()) {
                val name = binding.nameEditText.text.toString().trim()
                val wattage = binding.wattageEditText.text.toString().toDoubleOrNull() ?: 0.0
                val quantity = binding.quantityEditText.text.toString().toIntOrNull() ?: 1
                val demandFactor = binding.demandFactorEditText.text.toString().toDoubleOrNull()?.div(100.0) ?: 1.0

                val appliance = Appliance(
                    name = name,
                    wattage = wattage,
                    quantity = quantity,
                    demandFactor = demandFactor.coerceIn(0.0, 1.0)
                )
                onSave(appliance)
                dismiss()
            }
        }
    }

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
