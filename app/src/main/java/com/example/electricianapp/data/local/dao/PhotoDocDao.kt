package com.example.electricianapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.electricianapp.data.local.entity.BeforeAfterPairEntity
import com.example.electricianapp.data.local.entity.PhotoAnnotationEntity
import com.example.electricianapp.data.local.entity.PhotoDocumentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for photo documentation entities
 */
@Dao
interface PhotoDocDao {
    
    // Photo Document operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoDocument(photoDocument: PhotoDocumentEntity): Long
    
    @Update
    suspend fun updatePhotoDocument(photoDocument: PhotoDocumentEntity)
    
    @Query("DELETE FROM photo_documents WHERE id = :photoId")
    suspend fun deletePhotoDocument(photoId: Long)
    
    @Query("SELECT * FROM photo_documents WHERE id = :photoId")
    suspend fun getPhotoDocumentById(photoId: Long): PhotoDocumentEntity?
    
    @Query("SELECT * FROM photo_documents ORDER BY dateCreated DESC")
    fun getAllPhotoDocuments(): Flow<List<PhotoDocumentEntity>>
    
    @Query("SELECT * FROM photo_documents WHERE jobId = :jobId ORDER BY dateCreated DESC")
    fun getPhotoDocumentsByJobId(jobId: Long): Flow<List<PhotoDocumentEntity>>
    
    @Query("SELECT * FROM photo_documents WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY dateCreated DESC")
    fun searchPhotoDocuments(query: String): Flow<List<PhotoDocumentEntity>>
    
    // Photo Annotation operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnotation(annotation: PhotoAnnotationEntity): Long
    
    @Update
    suspend fun updateAnnotation(annotation: PhotoAnnotationEntity)
    
    @Delete
    suspend fun deleteAnnotation(annotation: PhotoAnnotationEntity)
    
    @Query("DELETE FROM photo_annotations WHERE id = :annotationId")
    suspend fun deleteAnnotationById(annotationId: Long)
    
    @Query("SELECT * FROM photo_annotations WHERE photoId = :photoId")
    suspend fun getAnnotationsForPhoto(photoId: Long): List<PhotoAnnotationEntity>
    
    // Before/After Pair operations
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeforeAfterPair(beforeAfterPair: BeforeAfterPairEntity): Long
    
    @Update
    suspend fun updateBeforeAfterPair(beforeAfterPair: BeforeAfterPairEntity)
    
    @Delete
    suspend fun deleteBeforeAfterPair(beforeAfterPair: BeforeAfterPairEntity)
    
    @Query("SELECT * FROM before_after_pairs")
    fun getAllBeforeAfterPairs(): Flow<List<BeforeAfterPairEntity>>
    
    @Query("""
        SELECT bap.* FROM before_after_pairs bap
        JOIN photo_documents pd ON bap.beforePhotoId = pd.id OR bap.afterPhotoId = pd.id
        WHERE pd.jobId = :jobId
        GROUP BY bap.id
    """)
    fun getBeforeAfterPairsByJobId(jobId: Long): Flow<List<BeforeAfterPairEntity>>
    
    @Transaction
    @Query("""
        SELECT * FROM photo_documents 
        WHERE tagsJson LIKE '%' || :tag || '%'
        ORDER BY dateCreated DESC
    """)
    fun getPhotoDocumentsByTag(tag: String): Flow<List<PhotoDocumentEntity>>
}
