package com.example.electricianapp.domain.usecase.neccodes

import com.example.electricianapp.domain.model.neccodes.CodeViolationCheck
import com.example.electricianapp.domain.repository.neccodes.NecCodeRepository
import javax.inject.Inject

/**
 * Use case for checking code violations based on input parameters.
 */
class CheckCodeViolationsUseCase @Inject constructor(
    private val necCodeRepository: NecCodeRepository
) {
    /**
     * Check for code violations based on input parameters.
     * @param parameters The parameters to check against code requirements
     * @return A list of violation check results
     */
    suspend operator fun invoke(parameters: Map<String, String>): List<CodeViolationCheck> {
        return necCodeRepository.checkViolations(parameters)
    }
}
