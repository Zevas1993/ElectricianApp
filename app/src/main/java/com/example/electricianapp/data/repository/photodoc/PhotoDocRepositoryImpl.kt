package com.example.electricianapp.data.repository.photodoc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.electricianapp.data.local.dao.PhotoDocDao
import com.example.electricianapp.data.local.entity.BeforeAfterPairEntity
import com.example.electricianapp.data.local.entity.PhotoAnnotationEntity
import com.example.electricianapp.data.local.entity.PhotoDocumentEntity
import com.example.electricianapp.domain.model.photodoc.AnnotationType
import com.example.electricianapp.domain.model.photodoc.BeforeAfterPair
import com.example.electricianapp.domain.model.photodoc.GeoLocation
import com.example.electricianapp.domain.model.photodoc.PhotoAnnotation
import com.example.electricianapp.domain.model.photodoc.PhotoDocument
import com.example.electricianapp.domain.repository.photodoc.PhotoDocRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext // Import qualifier

@Singleton
class PhotoDocRepositoryImpl @Inject constructor(
    private val photoDocDao: PhotoDocDao,
    @ApplicationContext private val context: Context, // Add qualifier
    private val gson: Gson
) : PhotoDocRepository {

    private val photoDirectory = File(context.filesDir, "photos")
    private val thumbnailDirectory = File(context.filesDir, "thumbnails")
    private val tempDirectory = File(context.cacheDir, "temp_photos")
    
    init {
        // Ensure directories exist
        photoDirectory.mkdirs()
        thumbnailDirectory.mkdirs()
        tempDirectory.mkdirs()
    }
    
    override suspend fun savePhotoDocument(photoDocument: PhotoDocument): Long {
        // Save the photo file if it's not already in our app's directory
        val savedPhotoUri = if (!isAppStorageUri(photoDocument.photoUri)) {
            savePhotoFile(photoDocument.photoUri)
        } else {
            photoDocument.photoUri
        }
        
        // Generate thumbnail if not provided
        val thumbnailUri = photoDocument.thumbnailUri ?: generateThumbnail(savedPhotoUri)
        
        // Convert domain model to entity
        val entity = PhotoDocumentEntity(
            id = photoDocument.id,
            jobId = photoDocument.jobId,
            title = photoDocument.title,
            description = photoDocument.description,
            photoUri = savedPhotoUri.toString(),
            thumbnailUri = thumbnailUri.toString(),
            dateCreated = photoDocument.dateCreated.time,
            latitude = photoDocument.location?.latitude,
            longitude = photoDocument.location?.longitude,
            address = photoDocument.location?.address,
            tagsJson = gson.toJson(photoDocument.tags),
            beforeAfterPairId = photoDocument.beforeAfterPairId
        )
        
        // Insert into database
        val photoId = photoDocDao.insertPhotoDocument(entity)
        
        // Save annotations if any
        photoDocument.annotations.forEach { annotation ->
            addAnnotation(annotation.copy(photoId = photoId))
        }
        
        return photoId
    }
    
    override suspend fun updatePhotoDocument(photoDocument: PhotoDocument) {
        val entity = PhotoDocumentEntity(
            id = photoDocument.id,
            jobId = photoDocument.jobId,
            title = photoDocument.title,
            description = photoDocument.description,
            photoUri = photoDocument.photoUri.toString(),
            thumbnailUri = photoDocument.thumbnailUri?.toString(),
            dateCreated = photoDocument.dateCreated.time,
            latitude = photoDocument.location?.latitude,
            longitude = photoDocument.location?.longitude,
            address = photoDocument.location?.address,
            tagsJson = gson.toJson(photoDocument.tags),
            beforeAfterPairId = photoDocument.beforeAfterPairId
        )
        
        photoDocDao.updatePhotoDocument(entity)
    }
    
    override suspend fun deletePhotoDocument(photoId: Long) {
        // Get the photo document to delete its files
        val photoDocument = getPhotoDocumentById(photoId)
        
        // Delete the photo and thumbnail files
        photoDocument?.let {
            deletePhotoFile(it.photoUri)
            it.thumbnailUri?.let { thumbnailUri ->
                deletePhotoFile(thumbnailUri)
            }
        }
        
        // Delete from database (will cascade to annotations)
        photoDocDao.deletePhotoDocument(photoId)
    }
    
    override suspend fun getPhotoDocumentById(photoId: Long): PhotoDocument? {
        val entity = photoDocDao.getPhotoDocumentById(photoId) ?: return null
        val annotations = getAnnotationsForPhoto(photoId)
        return entity.toDomainModel(annotations)
    }
    
    override fun getAllPhotoDocuments(): Flow<List<PhotoDocument>> {
        return photoDocDao.getAllPhotoDocuments().map { entities ->
            entities.map { entity ->
                val annotations = getAnnotationsForPhoto(entity.id)
                entity.toDomainModel(annotations)
            }
        }
    }
    
    override fun getPhotoDocumentsByJobId(jobId: Long): Flow<List<PhotoDocument>> {
        return photoDocDao.getPhotoDocumentsByJobId(jobId).map { entities ->
            entities.map { entity ->
                val annotations = getAnnotationsForPhoto(entity.id)
                entity.toDomainModel(annotations)
            }
        }
    }
    
    override fun getPhotoDocumentsByTags(tags: List<String>): Flow<List<PhotoDocument>> {
        // Since we need to check multiple tags, we'll get photos for each tag
        // and then filter in memory
        return if (tags.isEmpty()) {
            getAllPhotoDocuments()
        } else {
            photoDocDao.getPhotoDocumentsByTag(tags.first()).map { entities ->
                entities.filter { entity ->
                    val photoTags = gson.fromJson<List<String>>(
                        entity.tagsJson,
                        object : TypeToken<List<String>>() {}.type
                    )
                    tags.all { tag -> photoTags.contains(tag) }
                }.map { entity ->
                    val annotations = getAnnotationsForPhoto(entity.id)
                    entity.toDomainModel(annotations)
                }
            }
        }
    }
    
    override fun searchPhotoDocuments(query: String): Flow<List<PhotoDocument>> {
        return photoDocDao.searchPhotoDocuments(query).map { entities ->
            entities.map { entity ->
                val annotations = getAnnotationsForPhoto(entity.id)
                entity.toDomainModel(annotations)
            }
        }
    }
    
    override suspend fun addAnnotation(annotation: PhotoAnnotation): Long {
        val entity = PhotoAnnotationEntity(
            id = annotation.id,
            photoId = annotation.photoId,
            type = annotation.type.name,
            x = annotation.x,
            y = annotation.y,
            width = annotation.width,
            height = annotation.height,
            text = annotation.text,
            color = annotation.color
        )
        
        return photoDocDao.insertAnnotation(entity)
    }
    
    override suspend fun updateAnnotation(annotation: PhotoAnnotation) {
        val entity = PhotoAnnotationEntity(
            id = annotation.id,
            photoId = annotation.photoId,
            type = annotation.type.name,
            x = annotation.x,
            y = annotation.y,
            width = annotation.width,
            height = annotation.height,
            text = annotation.text,
            color = annotation.color
        )
        
        photoDocDao.updateAnnotation(entity)
    }
    
    override suspend fun deleteAnnotation(annotationId: Long) {
        photoDocDao.deleteAnnotationById(annotationId)
    }
    
    override suspend fun getAnnotationsForPhoto(photoId: Long): List<PhotoAnnotation> {
        return photoDocDao.getAnnotationsForPhoto(photoId).map { entity ->
            PhotoAnnotation(
                id = entity.id,
                photoId = entity.photoId,
                type = AnnotationType.valueOf(entity.type),
                x = entity.x,
                y = entity.y,
                width = entity.width,
                height = entity.height,
                text = entity.text,
                color = entity.color
            )
        }
    }
    
    override suspend fun createBeforeAfterPair(beforeAfterPair: BeforeAfterPair): Long {
        val entity = BeforeAfterPairEntity(
            id = beforeAfterPair.id,
            beforePhotoId = beforeAfterPair.beforePhotoId,
            afterPhotoId = beforeAfterPair.afterPhotoId,
            title = beforeAfterPair.title,
            description = beforeAfterPair.description
        )
        
        val pairId = photoDocDao.insertBeforeAfterPair(entity)
        
        // Update the photos to reference this pair
        val beforePhoto = getPhotoDocumentById(beforeAfterPair.beforePhotoId)
        val afterPhoto = getPhotoDocumentById(beforeAfterPair.afterPhotoId)
        
        beforePhoto?.let {
            updatePhotoDocument(it.copy(beforeAfterPairId = pairId))
        }
        
        afterPhoto?.let {
            updatePhotoDocument(it.copy(beforeAfterPairId = pairId))
        }
        
        return pairId
    }
    
    override fun getAllBeforeAfterPairs(): Flow<List<BeforeAfterPair>> {
        return photoDocDao.getAllBeforeAfterPairs().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getBeforeAfterPairsByJobId(jobId: Long): Flow<List<BeforeAfterPair>> {
        return photoDocDao.getBeforeAfterPairsByJobId(jobId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun savePhotoFile(uri: Uri, isTemporary: Boolean): Uri = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            
            // Create a unique filename
            val filename = "${UUID.randomUUID()}.jpg"
            val directory = if (isTemporary) tempDirectory else photoDirectory
            val outputFile = File(directory, filename)
            
            // Copy the file
            FileOutputStream(outputFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            
            // Close the input stream
            inputStream?.close()
            
            // Return the URI for the saved file
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                outputFile
            )
        } catch (e: Exception) {
            Log.e("PhotoDocRepository", "Error saving photo file", e)
            uri // Return the original URI if there was an error
        }
    }
    
    override suspend fun generateThumbnail(photoUri: Uri): Uri = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(photoUri)
            
            // Create a unique filename for the thumbnail
            val filename = "${UUID.randomUUID()}_thumb.jpg"
            val outputFile = File(thumbnailDirectory, filename)
            
            // Decode the image to get its dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()
            
            // Calculate the sample size to reduce the image
            val maxSize = 300 // Max width or height for thumbnail
            val sampleSize = calculateSampleSize(options.outWidth, options.outHeight, maxSize)
            
            // Decode the image again with the sample size
            val decodingOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            val newInputStream = context.contentResolver.openInputStream(photoUri)
            val bitmap = BitmapFactory.decodeStream(newInputStream, null, decodingOptions)
            newInputStream?.close()
            
            // Save the thumbnail
            FileOutputStream(outputFile).use { outputStream ->
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            }
            
            // Clean up
            bitmap?.recycle()
            
            // Return the URI for the thumbnail
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                outputFile
            )
        } catch (e: Exception) {
            Log.e("PhotoDocRepository", "Error generating thumbnail", e)
            photoUri // Return the original URI if there was an error
        }
    }
    
    override suspend fun deletePhotoFile(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = getFileFromUri(uri)
            file?.delete() ?: false
        } catch (e: Exception) {
            Log.e("PhotoDocRepository", "Error deleting photo file", e)
            false
        }
    }
    
    // Helper methods
    
    private fun calculateSampleSize(width: Int, height: Int, maxSize: Int): Int {
        var sampleSize = 1
        
        if (width > maxSize || height > maxSize) {
            val halfWidth = width / 2
            val halfHeight = height / 2
            
            while ((halfWidth / sampleSize) >= maxSize && (halfHeight / sampleSize) >= maxSize) {
                sampleSize *= 2
            }
        }
        
        return sampleSize
    }
    
    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val path = uri.path ?: return null
            File(path)
        } catch (e: Exception) {
            Log.e("PhotoDocRepository", "Error getting file from URI", e)
            null
        }
    }
    
    private fun isAppStorageUri(uri: Uri): Boolean {
        val path = uri.path ?: return false
        return path.contains(context.filesDir.path) || path.contains(context.cacheDir.path)
    }
    
    // Extension functions to convert between domain models and entities
    
    private fun PhotoDocumentEntity.toDomainModel(annotations: List<PhotoAnnotation>): PhotoDocument {
        val tags = gson.fromJson<List<String>>(
            this.tagsJson,
            object : TypeToken<List<String>>() {}.type
        )
        
        val location = if (this.latitude != null && this.longitude != null) {
            GeoLocation(
                latitude = this.latitude,
                longitude = this.longitude,
                address = this.address
            )
        } else {
            null
        }
        
        return PhotoDocument(
            id = this.id,
            jobId = this.jobId,
            title = this.title,
            description = this.description,
            photoUri = Uri.parse(this.photoUri),
            thumbnailUri = this.thumbnailUri?.let { Uri.parse(it) },
            dateCreated = Date(this.dateCreated),
            location = location,
            tags = tags,
            annotations = annotations,
            beforeAfterPairId = this.beforeAfterPairId
        )
    }
    
    private fun BeforeAfterPairEntity.toDomainModel(): BeforeAfterPair {
        return BeforeAfterPair(
            id = this.id,
            beforePhotoId = this.beforePhotoId,
            afterPhotoId = this.afterPhotoId,
            title = this.title,
            description = this.description
        )
    }
}
