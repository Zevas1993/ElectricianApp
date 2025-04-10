package com.example.electricianapp.domain.usecase.photodoc

import android.net.Uri
import com.example.electricianapp.domain.model.photodoc.BeforeAfterPair
import com.example.electricianapp.domain.model.photodoc.PhotoAnnotation
import com.example.electricianapp.domain.model.photodoc.PhotoDocument
import com.example.electricianapp.domain.repository.photodoc.PhotoDocRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for saving a photo document
 */
class SavePhotoDocumentUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    suspend operator fun invoke(photoDocument: PhotoDocument): Long {
        return photoDocRepository.savePhotoDocument(photoDocument)
    }
}

/**
 * Use case for updating a photo document
 */
class UpdatePhotoDocumentUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    suspend operator fun invoke(photoDocument: PhotoDocument) {
        photoDocRepository.updatePhotoDocument(photoDocument)
    }
}

/**
 * Use case for deleting a photo document
 */
class DeletePhotoDocumentUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    suspend operator fun invoke(photoId: Long) {
        photoDocRepository.deletePhotoDocument(photoId)
    }
}

/**
 * Use case for getting a photo document by ID
 */
class GetPhotoDocumentByIdUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    suspend operator fun invoke(photoId: Long): PhotoDocument? {
        return photoDocRepository.getPhotoDocumentById(photoId)
    }
}

/**
 * Use case for getting all photo documents
 */
class GetAllPhotoDocumentsUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    operator fun invoke(): Flow<List<PhotoDocument>> {
        return photoDocRepository.getAllPhotoDocuments()
    }
}

/**
 * Use case for getting photo documents by job ID
 */
class GetPhotoDocumentsByJobIdUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    operator fun invoke(jobId: Long): Flow<List<PhotoDocument>> {
        return photoDocRepository.getPhotoDocumentsByJobId(jobId)
    }
}

/**
 * Use case for getting photo documents by tags
 */
class GetPhotoDocumentsByTagsUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    operator fun invoke(tags: List<String>): Flow<List<PhotoDocument>> {
        return photoDocRepository.getPhotoDocumentsByTags(tags)
    }
}

/**
 * Use case for searching photo documents
 */
class SearchPhotoDocumentsUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    operator fun invoke(query: String): Flow<List<PhotoDocument>> {
        return photoDocRepository.searchPhotoDocuments(query)
    }
}

/**
 * Use case for adding an annotation to a photo
 */
class AddAnnotationUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    suspend operator fun invoke(annotation: PhotoAnnotation): Long {
        return photoDocRepository.addAnnotation(annotation)
    }
}

/**
 * Use case for updating an annotation
 */
class UpdateAnnotationUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    suspend operator fun invoke(annotation: PhotoAnnotation) {
        photoDocRepository.updateAnnotation(annotation)
    }
}

/**
 * Use case for deleting an annotation
 */
class DeleteAnnotationUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    suspend operator fun invoke(annotationId: Long) {
        photoDocRepository.deleteAnnotation(annotationId)
    }
}

/**
 * Use case for getting annotations for a photo
 */
class GetAnnotationsForPhotoUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    suspend operator fun invoke(photoId: Long): List<PhotoAnnotation> {
        return photoDocRepository.getAnnotationsForPhoto(photoId)
    }
}

/**
 * Use case for creating a before/after pair
 */
class CreateBeforeAfterPairUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    suspend operator fun invoke(beforeAfterPair: BeforeAfterPair): Long {
        return photoDocRepository.createBeforeAfterPair(beforeAfterPair)
    }
}

/**
 * Use case for getting all before/after pairs
 */
class GetAllBeforeAfterPairsUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    operator fun invoke(): Flow<List<BeforeAfterPair>> {
        return photoDocRepository.getAllBeforeAfterPairs()
    }
}

/**
 * Use case for getting before/after pairs by job ID
 */
class GetBeforeAfterPairsByJobIdUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    operator fun invoke(jobId: Long): Flow<List<BeforeAfterPair>> {
        return photoDocRepository.getBeforeAfterPairsByJobId(jobId)
    }
}

/**
 * Use case for saving a photo file
 */
class SavePhotoFileUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    suspend operator fun invoke(uri: Uri, isTemporary: Boolean = false): Uri {
        return photoDocRepository.savePhotoFile(uri, isTemporary)
    }
}

/**
 * Use case for generating a thumbnail
 */
class GenerateThumbnailUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    suspend operator fun invoke(photoUri: Uri): Uri {
        return photoDocRepository.generateThumbnail(photoUri)
    }
}

/**
 * Use case for deleting a photo file
 */
class DeletePhotoFileUseCase @Inject constructor(
    private val photoDocRepository: PhotoDocRepository
) {
    suspend operator fun invoke(uri: Uri): Boolean {
        return photoDocRepository.deletePhotoFile(uri)
    }
}
