package com.example.electricianapp.domain.usecase.materials

import com.example.electricianapp.domain.model.materials.MaterialTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Use case for getting transaction history for a material
 */
interface GetTransactionHistoryUseCase {
    /**
     * Get transaction history for a material
     * @param materialId The ID of the material
     * @return Flow of transaction history
     */
    operator fun invoke(materialId: String): Flow<List<MaterialTransaction>>
}

/**
 * Implementation of [GetTransactionHistoryUseCase]
 */
class GetTransactionHistoryUseCaseImpl @Inject constructor() : GetTransactionHistoryUseCase {
    override fun invoke(materialId: String): Flow<List<MaterialTransaction>> {
        // This is a stub implementation that returns an empty list
        // In a real implementation, this would fetch data from a repository
        return flowOf(emptyList())
    }
}
