package com.example.electricianapp.domain.usecase.neccodes

import com.example.electricianapp.domain.model.neccodes.NecArticle
import com.example.electricianapp.domain.model.neccodes.NecCategory
import com.example.electricianapp.domain.model.neccodes.NecSearchQuery
import com.example.electricianapp.domain.repository.neccodes.NecCodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class NecCodeSearchUseCaseTest {

    private lateinit var repository: NecCodeRepository
    private lateinit var searchUseCase: NecCodeSearchUseCase

    @Before
    fun setUp() {
        repository = mock(NecCodeRepository::class.java)
        searchUseCase = NecCodeSearchUseCase(repository)
    }

    @Test
    fun `search articles returns expected results`() = runBlocking {
        // Given
        val query = NecSearchQuery(
            searchText = "GFCI",
            category = NecCategory.WIRING_AND_PROTECTION,
            year = 2020
        )
        
        val expectedArticles = listOf(
            NecArticle(
                id = 1,
                articleNumber = "210.8(A)(1)",
                title = "GFCI Protection for Personnel - Dwelling Units - Bathrooms",
                content = "All 125-volt, single-phase, 15- and 20-ampere receptacles installed in bathrooms shall have ground-fault circuit-interrupter protection for personnel.",
                summary = "GFCI protection required for all 125-volt, single-phase, 15- and 20-ampere receptacles in dwelling unit bathrooms.",
                category = NecCategory.WIRING_AND_PROTECTION,
                tags = listOf("GFCI", "Bathroom", "Dwelling Unit", "Receptacle"),
                relatedArticles = listOf("210.8(A)(2)", "210.8(A)(3)", "210.8(B)"),
                year = 2020
            )
        )
        
        // Mock repository response
        `when`(repository.searchArticles(query)).thenReturn(flowOf(expectedArticles))
        
        // When
        val result = searchUseCase(query)
        
        // Then
        val resultList = (result as Flow<List<NecArticle>>).collect { articles ->
            assertEquals(1, articles.size)
            assertEquals("210.8(A)(1)", articles[0].articleNumber)
            assertEquals("GFCI Protection for Personnel - Dwelling Units - Bathrooms", articles[0].title)
        }
    }
}
