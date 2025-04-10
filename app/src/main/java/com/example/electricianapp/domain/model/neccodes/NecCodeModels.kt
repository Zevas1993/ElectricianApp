package com.example.electricianapp.domain.model.neccodes

/**
 * Data class representing an NEC code article
 */
data class NecArticle(
    val id: Long = 0,
    val articleNumber: String,  // e.g., "210.8(A)(1)"
    val title: String,          // e.g., "GFCI Protection for Personnel"
    val content: String,        // The actual code text
    val summary: String,        // A simplified summary of the code
    val category: NecCategory,  // The category of the code
    val tags: List<String> = emptyList(), // Tags for searching
    val relatedArticles: List<String> = emptyList(), // Related article numbers
    val year: Int               // NEC edition year (e.g., 2020)
)

/**
 * Enum representing different categories of NEC codes
 */
enum class NecCategory {
    GENERAL,
    WIRING_AND_PROTECTION,
    WIRING_METHODS_AND_MATERIALS,
    EQUIPMENT_FOR_GENERAL_USE,
    SPECIAL_OCCUPANCIES,
    SPECIAL_EQUIPMENT,
    SPECIAL_CONDITIONS,
    COMMUNICATIONS_SYSTEMS,
    TABLES
}

/**
 * Data class representing a code update or change
 */
data class NecCodeUpdate(
    val id: Long = 0,
    val articleNumber: String,  // The article that was updated
    val previousEdition: Int,   // Previous NEC edition year
    val currentEdition: Int,    // Current NEC edition year
    val changeDescription: String, // Description of what changed
    val impactLevel: ImpactLevel // The significance of the change
)

/**
 * Enum representing the impact level of a code change
 */
enum class ImpactLevel {
    MINOR,      // Editorial or clarification
    MODERATE,   // Some practical impact
    SIGNIFICANT // Major change in requirements
}

/**
 * Data class representing a code violation check
 */
data class CodeViolationCheck(
    val articleNumber: String,
    val checkDescription: String,
    val parameters: Map<String, String>, // Parameters to check against
    val result: Boolean,                 // True if in violation
    val explanation: String              // Explanation of the violation or compliance
)

/**
 * Data class representing a bookmark or saved code article
 */
data class NecBookmark(
    val id: Long = 0,
    val articleId: Long,
    val notes: String = "",
    val dateAdded: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

/**
 * Data class representing a search query for NEC codes
 */
data class NecSearchQuery(
    val searchText: String,
    val category: NecCategory? = null,
    val year: Int? = null,
    val tags: List<String> = emptyList()
)
