package com.example.electricianapp.domain.usecase.neccodes

import com.example.electricianapp.domain.model.neccodes.NecCodeUpdate
import com.example.electricianapp.domain.repository.neccodes.NecCodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving NEC code updates between editions.
 */
class GetNecCodeUpdatesUseCase @Inject constructor(
    private val necCodeRepository: NecCodeRepository
) {
    /**
     * Get NEC code updates between two editions.
     * @param fromYear The starting edition year
     * @param toYear The ending edition year
     * @return A flow of code updates
     */
    operator fun invoke(fromYear: Int, toYear: Int): Flow<List<NecCodeUpdate>> {
        return necCodeRepository.getCodeUpdates(fromYear, toYear)
    }
}
