package com.example.electricianapp.domain.repository.photodoc

import android.net.Uri
import com.example.electricianapp.domain.model.photodoc.BeforeAfterPair
import com.example.electricianapp.domain.model.photodoc.PhotoAnnotation
import com.example.electricianapp.domain.model.photodoc.PhotoDocument
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for photo documentation operations
 */
interface PhotoDocRepository {
    
    /**
     * Save a new photo document
     * @param photoDocument The photo document to save
     * @return The ID of the saved photo document
     */
    suspend fun savePhotoDocument(photoDocument: PhotoDocument): Long
    
    /**
     * Update an existing photo document
     * @param photoDocument The photo document to update
     */
    suspend fun updatePhotoDocument(photoDocument: PhotoDocument)
    
    /**
     * Delete a photo document
     * @param photoId The ID of the photo document to delete
     */
    suspend fun deletePhotoDocument(photoId: Long)
    
    /**
     * Get a photo document by ID
     * @param photoId The ID of the photo document to retrieve
     * @return The photo document, or null if not found
     */
    suspend fun getPhotoDocumentById(photoId: Long): PhotoDocument?
    
    /**
     * Get all photo documents
     * @return A flow of all photo documents
     */
    fun getAllPhotoDocuments(): Flow<List<PhotoDocument>>
    
    /**
     * Get photo documents for a specific job
     * @param jobId The ID of the job
     * @return A flow of photo documents for the job
     */
    fun getPhotoDocumentsByJobId(jobId: Long): Flow<List<PhotoDocument>>
    
    /**
     * Get photo documents with specific tags
     * @param tags The tags to filter by
     * @return A flow of photo documents with the specified tags
     */
    fun getPhotoDocumentsByTags(tags: List<String>): Flow<List<PhotoDocument>>
    
    /**
     * Search photo documents by title or description
     * @param query The search query
     * @return A flow of photo documents matching the query
     */
    fun searchPhotoDocuments(query: String): Flow<List<PhotoDocument>>
    
    /**
     * Add an annotation to a photo
     * @param annotation The annotation to add
     * @return The ID of the added annotation
     */
    suspend fun addAnnotation(annotation: PhotoAnnotation): Long
    
    /**
     * Update an existing annotation
     * @param annotation The annotation to update
     */
    suspend fun updateAnnotation(annotation: PhotoAnnotation)
    
    /**
     * Delete an annotation
     * @param annotationId The ID of the annotation to delete
     */
    suspend fun deleteAnnotation(annotationId: Long)
    
    /**
     * Get all annotations for a photo
     * @param photoId The ID of the photo
     * @return A list of annotations for the photo
     */
    suspend fun getAnnotationsForPhoto(photoId: Long): List<PhotoAnnotation>
    
    /**
     * Create a before/after pair
     * @param beforeAfterPair The before/after pair to create
     * @return The ID of the created pair
     */
    suspend fun createBeforeAfterPair(beforeAfterPair: BeforeAfterPair): Long
    
    /**
     * Get all before/after pairs
     * @return A flow of all before/after pairs
     */
    fun getAllBeforeAfterPairs(): Flow<List<BeforeAfterPair>>
    
    /**
     * Get before/after pairs for a specific job
     * @param jobId The ID of the job
     * @return A flow of before/after pairs for the job
     */
    fun getBeforeAfterPairsByJobId(jobId: Long): Flow<List<BeforeAfterPair>>
    
    /**
     * Save a photo file to local storage
     * @param uri The URI of the photo to save
     * @param isTemporary Whether the photo is temporary or permanent
     * @return The URI of the saved photo
     */
    suspend fun savePhotoFile(uri: Uri, isTemporary: Boolean = false): Uri
    
    /**
     * Generate a thumbnail for a photo
     * @param photoUri The URI of the photo
     * @return The URI of the generated thumbnail
     */
    suspend fun generateThumbnail(photoUri: Uri): Uri
    
    /**
     * Delete a photo file from storage
     * @param uri The URI of the photo to delete
     * @return True if the deletion was successful, false otherwise
     */
    suspend fun deletePhotoFile(uri: Uri): Boolean
}
