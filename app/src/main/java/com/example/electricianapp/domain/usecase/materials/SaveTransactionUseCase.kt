package com.example.electricianapp.domain.usecase.materials

import com.example.electricianapp.domain.model.materials.MaterialTransaction
import com.example.electricianapp.domain.repository.materials.MaterialRepository
import javax.inject.Inject

/**
 * Use case for saving a material transaction
 */
interface SaveTransactionUseCase {
    /**
     * Save a material transaction
     * @param transaction The transaction to save
     */
    suspend operator fun invoke(transaction: MaterialTransaction)
}

/**
 * Implementation of [SaveTransactionUseCase]
 */
class SaveTransactionUseCaseImpl @Inject constructor(
    private val materialRepository: MaterialRepository
) : SaveTransactionUseCase {
    override suspend fun invoke(transaction: MaterialTransaction) {
        materialRepository.saveTransaction(transaction)
    }
}
