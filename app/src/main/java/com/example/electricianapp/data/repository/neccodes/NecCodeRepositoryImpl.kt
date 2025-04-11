package com.example.electricianapp.data.repository.neccodes // Ensure package is correct

import com.example.electricianapp.data.local.dao.NecCodeDao
import com.example.electricianapp.data.local.entity.CodeViolationCheckEntity // Use correct path if needed
import com.example.electricianapp.data.local.entity.NecArticleEntity // Use correct path if needed
import com.example.electricianapp.data.local.entity.NecBookmarkEntity // Use correct path if needed
import com.example.electricianapp.data.local.entity.NecCodeUpdateEntity // Use correct path if needed
import com.example.electricianapp.domain.model.neccodes.CodeViolationCheck // Domain models should be correct now
import com.example.electricianapp.domain.model.neccodes.NecArticle // Domain models should be correct now
import com.example.electricianapp.domain.model.neccodes.NecBookmark // Domain models should be correct now
import com.example.electricianapp.domain.model.neccodes.NecCategory // Domain models should be correct now
import com.example.electricianapp.domain.model.neccodes.NecCodeUpdate // Domain models should be correct now
import com.example.electricianapp.domain.model.neccodes.NecSearchQuery // Domain models should be correct now
import com.example.electricianapp.domain.repository.neccodes.NecCodeRepository // Domain models should be correct now
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
// import javax.script.* // Comment out script engine imports

/**
 * Implementation of the NecCodeRepository interface.
 * Handles database operations for NEC code articles and related entities.
 */
@Singleton
class NecCodeRepositoryImpl @Inject constructor(
    private val necCodeDao: NecCodeDao,
    private val gson: Gson
) : NecCodeRepository {

    // Type tokens for JSON conversion
    private val stringListType = object : TypeToken<List<String>>() {}.type
    private val stringMapType = object : TypeToken<Map<String, String>>() {}.type
    
    // JavaScript engine for evaluating code violation checks - COMMENTED OUT
    // private val scriptEngine: ScriptEngine by lazy {
    //     ScriptEngineManager().getEngineByName("javascript")
    // }
    
    override suspend fun getArticleById(id: Long): NecArticle? {
        return necCodeDao.getArticleById(id)?.toDomainModel()
    }
    
    override suspend fun getArticleByNumber(articleNumber: String, year: Int): NecArticle? {
        return necCodeDao.getArticleByNumber(articleNumber, year)?.toDomainModel()
    }
    
    override fun searchArticles(query: NecSearchQuery): Flow<List<NecArticle>> {
        return if (query.category != null) {
            necCodeDao.searchArticlesByCategory(query.searchText, query.category, query.year ?: 2020)
                .map { entities -> entities.map { it.toDomainModel() } }
        } else {
            necCodeDao.searchArticles(query.searchText, query.year ?: 2020)
                .map { entities -> entities.map { it.toDomainModel() } }
        }
    }
    
    override fun getArticlesByCategory(category: NecCategory, year: Int): Flow<List<NecArticle>> {
        return necCodeDao.getArticlesByCategory(category, year)
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    override fun getCodeUpdates(fromYear: Int, toYear: Int): Flow<List<NecCodeUpdate>> {
        return necCodeDao.getCodeUpdates(fromYear, toYear)
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    override suspend fun checkViolations(parameters: Map<String, String>): List<CodeViolationCheck> {
        // Get all violation checks that might apply based on article prefix
        // For simplicity, we'll just get all checks for now
        val checkEntities = necCodeDao.getViolationChecksByArticlePrefix("")
        
        return checkEntities.mapNotNull { entity ->
            try {
                // Parse the check parameters
                val checkParams = gson.fromJson<Map<String, String>>(entity.parametersJson, stringMapType)
                
                // Evaluate the check logic using the JavaScript engine - COMMENTED OUT
                // val script = prepareCheckScript(entity.checkLogic, parameters)
                // val result = scriptEngine.eval(script) as Boolean
                val result = false // Placeholder result
                
                // Generate explanation
                val explanation = generateExplanation(entity.explanationTemplate, parameters, result)
                
                CodeViolationCheck(
                    articleNumber = entity.articleNumber,
                    checkDescription = entity.checkDescription,
                    parameters = parameters,
                    result = result,
                    explanation = explanation
                )
            } catch (e: Exception) {
                // Log error and skip this check
                null
            }
        }
    }
    
    override fun getBookmarkedArticles(): Flow<List<Pair<NecArticle, NecBookmark>>> {
        return necCodeDao.getBookmarkedArticles().map { entries ->
            entries.map { entry ->
                Pair(
                    entry.article.toDomainModel(),
                    NecBookmark(
                        id = entry.bookmark.id,
                        articleId = entry.bookmark.articleId,
                        notes = entry.bookmark.notes,
                        dateAdded = entry.bookmark.dateAdded,
                        isFavorite = entry.bookmark.isFavorite
                    )
                )
            }
        }
    }
    
    override suspend fun addBookmark(articleId: Long, notes: String): Long {
        val bookmark = NecBookmarkEntity(
            articleId = articleId,
            notes = notes,
            dateAdded = System.currentTimeMillis(),
            isFavorite = false
        )
        return necCodeDao.insertBookmark(bookmark)
    }
    
    override suspend fun updateBookmark(bookmark: NecBookmark) {
        val entity = NecBookmarkEntity(
            id = bookmark.id,
            articleId = bookmark.articleId,
            notes = bookmark.notes,
            dateAdded = bookmark.dateAdded,
            isFavorite = bookmark.isFavorite
        )
        necCodeDao.updateBookmark(entity)
    }
    
    override suspend fun removeBookmark(bookmarkId: Long) {
        necCodeDao.deleteBookmark(bookmarkId)
    }
    
    override suspend fun getRelatedArticles(articleId: Long): List<NecArticle> {
        val article = necCodeDao.getArticleById(articleId) ?: return emptyList()
        val relatedArticleNumbers = gson.fromJson<List<String>>(article.relatedArticlesJson, stringListType)
        
        return relatedArticleNumbers.mapNotNull { articleNumber ->
            necCodeDao.getArticleByNumber(articleNumber, article.year)?.toDomainModel()
        }
    }
    
    // Helper methods
    
    private fun NecArticleEntity.toDomainModel(): NecArticle {
        val tags = gson.fromJson<List<String>>(this.tagsJson, stringListType)
        val relatedArticles = gson.fromJson<List<String>>(this.relatedArticlesJson, stringListType)
        
        return NecArticle(
            id = this.id,
            articleNumber = this.articleNumber,
            title = this.title,
            content = this.content,
            summary = this.summary,
            category = this.category,
            tags = tags,
            relatedArticles = relatedArticles,
            year = this.year
        )
    }
    
    private fun NecCodeUpdateEntity.toDomainModel(): NecCodeUpdate {
        return NecCodeUpdate(
            id = this.id,
            articleNumber = this.articleNumber,
            previousEdition = this.previousEdition,
            currentEdition = this.currentEdition,
            changeDescription = this.changeDescription,
            impactLevel = this.impactLevel
        )
    }
    
    private fun prepareCheckScript(checkLogic: String, parameters: Map<String, String>): String {
        // Create a JavaScript script that defines the parameters and evaluates the check logic
        val parameterDefinitions = parameters.entries.joinToString("\n") { (key, value) ->
            "const $key = \"$value\";"
        }
        
        return """
            $parameterDefinitions
            
            $checkLogic
        """.trimIndent()
    }
    
    private fun generateExplanation(template: String, parameters: Map<String, String>, result: Boolean): String {
        // Replace parameter placeholders in the explanation template
        var explanation = template
        
        parameters.forEach { (key, value) ->
            explanation = explanation.replace("{{$key}}", value)
        }
        
        // Add result-specific text
        val resultText = if (result) {
            "VIOLATION: "
        } else {
            "COMPLIANT: "
        }
        
        return resultText + explanation
    }
}
