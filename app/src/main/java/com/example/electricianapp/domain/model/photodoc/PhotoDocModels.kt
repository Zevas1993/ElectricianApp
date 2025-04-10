package com.example.electricianapp.domain.model.photodoc

import android.net.Uri
import java.util.Date

/**
 * Data class representing a photo document
 */
data class PhotoDocument(
    val id: Long = 0,
    val jobId: Long? = null,          // Optional association with a job
    val title: String,                // Title/name of the photo
    val description: String = "",     // Description of what the photo shows
    val photoUri: Uri,                // URI to the photo file
    val thumbnailUri: Uri? = null,    // URI to a thumbnail version (optional)
    val dateCreated: Date = Date(),   // Date the photo was taken
    val location: GeoLocation? = null, // Location where the photo was taken
    val tags: List<String> = emptyList(), // Tags for categorization and searching
    val annotations: List<PhotoAnnotation> = emptyList(), // Annotations on the photo
    val beforeAfterPairId: Long? = null // ID linking before/after photos
)

/**
 * Data class representing a geographical location
 */
data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)

/**
 * Data class representing an annotation on a photo
 */
data class PhotoAnnotation(
    val id: Long = 0,
    val photoId: Long,           // ID of the photo this annotation belongs to
    val type: AnnotationType,    // Type of annotation
    val x: Float,                // X coordinate (0-1 relative to image width)
    val y: Float,                // Y coordinate (0-1 relative to image height)
    val width: Float? = null,    // Width (for box annotations, 0-1 relative to image width)
    val height: Float? = null,   // Height (for box annotations, 0-1 relative to image height)
    val text: String = "",       // Text content of the annotation
    val color: Int               // Color of the annotation
)

/**
 * Enum representing different types of annotations
 */
enum class AnnotationType {
    POINT,      // A single point marker
    TEXT,       // Text annotation
    ARROW,      // An arrow pointing to something
    RECTANGLE,  // A rectangle highlighting an area
    CIRCLE,     // A circle highlighting an area
    FREEFORM    // A freeform drawing
}

/**
 * Data class representing a before/after photo pair
 */
data class BeforeAfterPair(
    val id: Long = 0,
    val beforePhotoId: Long,
    val afterPhotoId: Long,
    val title: String,
    val description: String = ""
)
