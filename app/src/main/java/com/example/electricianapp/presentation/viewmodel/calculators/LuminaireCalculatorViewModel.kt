package com.example.electricianapp.presentation.viewmodel.calculators

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class LuminaireCalculatorViewModel @Inject constructor() : ViewModel() {

    // Input LiveData
    val roomLength = MutableLiveData("")
    val roomWidth = MutableLiveData("")
    val desiredFootcandles = MutableLiveData("")
    val lumensPerLuminaire = MutableLiveData("")
    val coefficientOfUtilization = MutableLiveData("")
    val lightLossFactor = MutableLiveData("")

    // Result LiveData
    private val _numberOfLuminaires = MutableLiveData<Int?>(null)
    val numberOfLuminaires: LiveData<Int?> = _numberOfLuminaires

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    fun calculate() {
        val length = roomLength.value?.toDoubleOrNull()
        val width = roomWidth.value?.toDoubleOrNull()
        val fc = desiredFootcandles.value?.toDoubleOrNull()
        val lumens = lumensPerLuminaire.value?.toDoubleOrNull()
        val cu = coefficientOfUtilization.value?.toDoubleOrNull()
        val llf = lightLossFactor.value?.toDoubleOrNull()

        if (length == null || length <= 0 ||
            width == null || width <= 0 ||
            fc == null || fc <= 0 ||
            lumens == null || lumens <= 0 ||
            cu == null || cu <= 0 || cu > 1.0 || // CU is typically between 0 and 1
            llf == null || llf <= 0 || llf > 1.0) { // LLF is typically between 0 and 1
            _errorMessage.value = "Please enter valid positive inputs. CU and LLF must be between 0 and 1."
            _numberOfLuminaires.value = null
            return
        }

        val roomArea = length * width
        val denominator = lumens * cu * llf

        if (denominator == 0.0) {
            _errorMessage.value = "Lumens, CU, and LLF must be greater than zero."
            _numberOfLuminaires.value = null
            return
        }

        val calculatedValue = (fc * roomArea) / denominator
        _numberOfLuminaires.value = ceil(calculatedValue).toInt() // Round up to nearest whole luminaire
        _errorMessage.value = null // Clear previous errors
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
