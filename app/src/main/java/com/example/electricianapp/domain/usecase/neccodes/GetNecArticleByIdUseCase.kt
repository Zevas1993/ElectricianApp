package com.example.electricianapp.domain.usecase.neccodes

import com.example.electricianapp.domain.model.neccodes.NecArticle
import com.example.electricianapp.domain.repository.neccodes.NecCodeRepository
import javax.inject.Inject

/**
 * Use case for retrieving an NEC article by its ID.
 */
class GetNecArticleByIdUseCase @Inject constructor(
    private val necCodeRepository: NecCodeRepository
) {
    /**
     * Get an NEC article by its ID.
     * @param id The ID of the article to retrieve
     * @return The NEC article, or null if not found
     */
    suspend operator fun invoke(id: Long): NecArticle? {
        return necCodeRepository.getArticleById(id)
    }
}
