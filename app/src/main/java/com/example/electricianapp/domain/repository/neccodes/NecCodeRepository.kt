package com.example.electricianapp.domain.repository.neccodes

import com.example.electricianapp.domain.model.neccodes.CodeViolationCheck
import com.example.electricianapp.domain.model.neccodes.NecArticle
import com.example.electricianapp.domain.model.neccodes.NecBookmark
import com.example.electricianapp.domain.model.neccodes.NecCategory
import com.example.electricianapp.domain.model.neccodes.NecCodeUpdate
import com.example.electricianapp.domain.model.neccodes.NecSearchQuery
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for NEC code lookup and management
 */
interface NecCodeRepository {
    
    /**
     * Get an NEC article by its ID
     * @param id The ID of the article
     * @return The NEC article, or null if not found
     */
    suspend fun getArticleById(id: Long): NecArticle?
    
    /**
     * Get an NEC article by its article number
     * @param articleNumber The article number (e.g., "210.8(A)(1)")
     * @param year The NEC edition year (e.g., 2020)
     * @return The NEC article, or null if not found
     */
    suspend fun getArticleByNumber(articleNumber: String, year: Int): NecArticle?
    
    /**
     * Search for NEC articles based on a query
     * @param query The search query
     * @return A flow of matching NEC articles
     */
    fun searchArticles(query: NecSearchQuery): Flow<List<NecArticle>>
    
    /**
     * Get all NEC articles in a specific category
     * @param category The category to filter by
     * @param year The NEC edition year
     * @return A flow of NEC articles in the specified category
     */
    fun getArticlesByCategory(category: NecCategory, year: Int): Flow<List<NecArticle>>
    
    /**
     * Get all NEC code updates between two editions
     * @param fromYear The starting edition year
     * @param toYear The ending edition year
     * @return A flow of code updates
     */
    fun getCodeUpdates(fromYear: Int, toYear: Int): Flow<List<NecCodeUpdate>>
    
    /**
     * Check for code violations based on input parameters
     * @param parameters The parameters to check against code requirements
     * @return A list of violation check results
     */
    suspend fun checkViolations(parameters: Map<String, String>): List<CodeViolationCheck>
    
    /**
     * Get all bookmarked NEC articles
     * @return A flow of bookmarked articles
     */
    fun getBookmarkedArticles(): Flow<List<Pair<NecArticle, NecBookmark>>>
    
    /**
     * Add a bookmark for an NEC article
     * @param articleId The ID of the article to bookmark
     * @param notes Optional notes for the bookmark
     * @return The ID of the created bookmark
     */
    suspend fun addBookmark(articleId: Long, notes: String = ""): Long
    
    /**
     * Update a bookmark
     * @param bookmark The bookmark to update
     */
    suspend fun updateBookmark(bookmark: NecBookmark)
    
    /**
     * Remove a bookmark
     * @param bookmarkId The ID of the bookmark to remove
     */
    suspend fun removeBookmark(bookmarkId: Long)
    
    /**
     * Get related articles for a specific article
     * @param articleId The ID of the article to find related articles for
     * @return A list of related NEC articles
     */
    suspend fun getRelatedArticles(articleId: Long): List<NecArticle>
}
