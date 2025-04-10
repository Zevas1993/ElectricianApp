package com.example.electricianapp.data.local.dao

import androidx.room.*
import com.example.electricianapp.data.local.entity.CodeViolationCheckEntity
import com.example.electricianapp.data.local.entity.NecArticleEntity
import com.example.electricianapp.data.local.entity.NecBookmarkEntity
import com.example.electricianapp.data.local.entity.NecCodeUpdateEntity
import com.example.electricianapp.domain.model.neccodes.NecCategory
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for NEC code articles and related entities
 */
@Dao
interface NecCodeDao {

    // --- NEC Article Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: NecArticleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<NecArticleEntity>): List<Long>

    @Update
    suspend fun updateArticle(article: NecArticleEntity)

    @Query("SELECT * FROM nec_articles WHERE id = :id")
    suspend fun getArticleById(id: Long): NecArticleEntity?

    @Query("SELECT * FROM nec_articles WHERE articleNumber = :articleNumber AND year = :year")
    suspend fun getArticleByNumber(articleNumber: String, year: Int): NecArticleEntity?

    @Query("SELECT * FROM nec_articles WHERE category = :category AND year = :year ORDER BY articleNumber")
    fun getArticlesByCategory(category: NecCategory, year: Int): Flow<List<NecArticleEntity>>

    @Query("SELECT * FROM nec_articles WHERE year = :year AND (title LIKE '%' || :searchText || '%' OR content LIKE '%' || :searchText || '%' OR articleNumber LIKE '%' || :searchText || '%')")
    fun searchArticles(searchText: String, year: Int): Flow<List<NecArticleEntity>>

    @Query("SELECT * FROM nec_articles WHERE year = :year AND category = :category AND (title LIKE '%' || :searchText || '%' OR content LIKE '%' || :searchText || '%' OR articleNumber LIKE '%' || :searchText || '%')")
    fun searchArticlesByCategory(searchText: String, category: NecCategory, year: Int): Flow<List<NecArticleEntity>>

    // --- NEC Code Update Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCodeUpdate(update: NecCodeUpdateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCodeUpdates(updates: List<NecCodeUpdateEntity>): List<Long>

    @Query("SELECT * FROM nec_updates WHERE previousEdition = :fromYear AND currentEdition = :toYear ORDER BY articleNumber")
    fun getCodeUpdates(fromYear: Int, toYear: Int): Flow<List<NecCodeUpdateEntity>>

    // --- Bookmark Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: NecBookmarkEntity): Long

    @Update
    suspend fun updateBookmark(bookmark: NecBookmarkEntity)

    @Query("DELETE FROM nec_bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: Long)

    @Transaction
    @Query("""
        SELECT 
            a.*, 
            b.id AS bookmark_id, 
            b.articleId AS bookmark_articleId, 
            b.notes AS bookmark_notes, 
            b.dateAdded AS bookmark_dateAdded, 
            b.isFavorite AS bookmark_isFavorite 
        FROM 
            nec_articles a 
        INNER JOIN 
            nec_bookmarks b ON a.id = b.articleId 
        ORDER BY 
            b.dateAdded DESC
    """)
    // @RewriteQueriesToDropUnusedColumns // No longer needed with explicit columns
    fun getBookmarkedArticles(): Flow<List<BookmarkedArticle>>

    // --- Code Violation Check Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViolationCheck(check: CodeViolationCheckEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViolationChecks(checks: List<CodeViolationCheckEntity>): List<Long>

    @Query("SELECT * FROM code_violation_checks WHERE articleNumber LIKE :articlePrefix || '%'")
    suspend fun getViolationChecksByArticlePrefix(articlePrefix: String): List<CodeViolationCheckEntity>

    // --- Combined Data Class ---

    /**
     * Data class to hold the combined article and bookmark for joined queries
     */
    data class BookmarkedArticle(
        @Embedded val article: NecArticleEntity,
        @Embedded(prefix = "bookmark_") val bookmark: NecBookmarkEntity
    )

}
