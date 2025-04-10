package com.example.electricianapp.domain.usecase.neccodes

import com.example.electricianapp.domain.model.neccodes.NecArticle
import com.example.electricianapp.domain.model.neccodes.NecSearchQuery
import com.example.electricianapp.domain.repository.neccodes.NecCodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching NEC code articles.
 */
class NecCodeSearchUseCase @Inject constructor(
    private val necCodeRepository: NecCodeRepository
) {
    /**
     * Search for NEC articles based on the provided query.
     * @param query The search query parameters
     * @return A flow of matching NEC articles
     */
    operator fun invoke(query: NecSearchQuery): Flow<List<NecArticle>> {
        return necCodeRepository.searchArticles(query)
    }
}
