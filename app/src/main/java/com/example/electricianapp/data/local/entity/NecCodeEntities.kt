package com.example.electricianapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.electricianapp.domain.model.neccodes.ImpactLevel
import com.example.electricianapp.domain.model.neccodes.NecCategory

/**
 * Room entity for storing NEC articles
 */
@Entity(
    tableName = "nec_articles",
    indices = [
        Index("articleNumber", "year", unique = true)
    ]
)
data class NecArticleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val articleNumber: String,
    val title: String,
    val content: String,
    val summary: String,
    val category: NecCategory,
    val tagsJson: String, // JSON array of tags
    val relatedArticlesJson: String, // JSON array of related article numbers
    val year: Int
)

/**
 * Room entity for storing NEC code updates
 */
@Entity(
    tableName = "nec_updates",
    indices = [
        Index("articleNumber", "previousEdition", "currentEdition", unique = true)
    ]
)
data class NecCodeUpdateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val articleNumber: String,
    val previousEdition: Int,
    val currentEdition: Int,
    val changeDescription: String,
    val impactLevel: ImpactLevel
)

/**
 * Room entity for storing bookmarks of NEC articles
 */
@Entity(
    tableName = "nec_bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = NecArticleEntity::class,
            parentColumns = ["id"],
            childColumns = ["articleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("articleId")
    ]
)
data class NecBookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val articleId: Long,
    val notes: String,
    val dateAdded: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

/**
 * Room entity for storing code violation check templates
 */
@Entity(tableName = "code_violation_checks")
data class CodeViolationCheckEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val articleNumber: String,
    val checkDescription: String,
    val parametersJson: String, // JSON object of parameter names and types
    val checkLogic: String, // Logic to evaluate for violation
    val explanationTemplate: String // Template for explanation with parameter placeholders
)
