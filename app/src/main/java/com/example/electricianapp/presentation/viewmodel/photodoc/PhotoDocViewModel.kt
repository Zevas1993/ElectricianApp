package com.example.electricianapp.presentation.viewmodel.photodoc

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricianapp.domain.model.photodoc.AnnotationType
import com.example.electricianapp.domain.model.photodoc.BeforeAfterPair
import com.example.electricianapp.domain.model.photodoc.GeoLocation
import com.example.electricianapp.domain.model.photodoc.PhotoAnnotation
import com.example.electricianapp.domain.model.photodoc.PhotoDocument
import com.example.electricianapp.domain.usecase.photodoc.AddAnnotationUseCase
import com.example.electricianapp.domain.usecase.photodoc.CreateBeforeAfterPairUseCase
import com.example.electricianapp.domain.usecase.photodoc.DeleteAnnotationUseCase
import com.example.electricianapp.domain.usecase.photodoc.DeletePhotoDocumentUseCase
import com.example.electricianapp.domain.usecase.photodoc.DeletePhotoFileUseCase
import com.example.electricianapp.domain.usecase.photodoc.GenerateThumbnailUseCase
import com.example.electricianapp.domain.usecase.photodoc.GetAllBeforeAfterPairsUseCase
import com.example.electricianapp.domain.usecase.photodoc.GetAllPhotoDocumentsUseCase
import com.example.electricianapp.domain.usecase.photodoc.GetAnnotationsForPhotoUseCase
import com.example.electricianapp.domain.usecase.photodoc.GetBeforeAfterPairsByJobIdUseCase
import com.example.electricianapp.domain.usecase.photodoc.GetPhotoDocumentByIdUseCase
import com.example.electricianapp.domain.usecase.photodoc.GetPhotoDocumentsByJobIdUseCase
import com.example.electricianapp.domain.usecase.photodoc.GetPhotoDocumentsByTagsUseCase
import com.example.electricianapp.domain.usecase.photodoc.SavePhotoDocumentUseCase
import com.example.electricianapp.domain.usecase.photodoc.SavePhotoFileUseCase
import com.example.electricianapp.domain.usecase.photodoc.SearchPhotoDocumentsUseCase
import com.example.electricianapp.domain.usecase.photodoc.UpdateAnnotationUseCase
import com.example.electricianapp.domain.usecase.photodoc.UpdatePhotoDocumentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * UI state for photo documentation
 */
sealed class PhotoDocUiState {
    object Loading : PhotoDocUiState()
    data class Success(val photos: List<PhotoDocument>) : PhotoDocUiState()
    data class Error(val message: String) : PhotoDocUiState()
}

/**
 * UI state for before/after pairs
 */
sealed class BeforeAfterPairUiState {
    object Loading : BeforeAfterPairUiState()
    data class Success(val pairs: List<BeforeAfterPair>) : BeforeAfterPairUiState()
    data class Error(val message: String) : BeforeAfterPairUiState()
}

/**
 * ViewModel for photo documentation
 */
@HiltViewModel
class PhotoDocViewModel @Inject constructor(
    private val savePhotoDocumentUseCase: SavePhotoDocumentUseCase,
    private val updatePhotoDocumentUseCase: UpdatePhotoDocumentUseCase,
    private val deletePhotoDocumentUseCase: DeletePhotoDocumentUseCase,
    private val getPhotoDocumentByIdUseCase: GetPhotoDocumentByIdUseCase,
    private val getAllPhotoDocumentsUseCase: GetAllPhotoDocumentsUseCase,
    private val getPhotoDocumentsByJobIdUseCase: GetPhotoDocumentsByJobIdUseCase,
    private val getPhotoDocumentsByTagsUseCase: GetPhotoDocumentsByTagsUseCase,
    private val searchPhotoDocumentsUseCase: SearchPhotoDocumentsUseCase,
    private val addAnnotationUseCase: AddAnnotationUseCase,
    private val updateAnnotationUseCase: UpdateAnnotationUseCase,
    private val deleteAnnotationUseCase: DeleteAnnotationUseCase,
    private val getAnnotationsForPhotoUseCase: GetAnnotationsForPhotoUseCase,
    private val createBeforeAfterPairUseCase: CreateBeforeAfterPairUseCase,
    private val getAllBeforeAfterPairsUseCase: GetAllBeforeAfterPairsUseCase,
    private val getBeforeAfterPairsByJobIdUseCase: GetBeforeAfterPairsByJobIdUseCase,
    private val savePhotoFileUseCase: SavePhotoFileUseCase,
    private val generateThumbnailUseCase: GenerateThumbnailUseCase,
    private val deletePhotoFileUseCase: DeletePhotoFileUseCase
) : ViewModel() {

    // UI state for photo documents
    private val _photoDocUiState = MutableStateFlow<PhotoDocUiState>(PhotoDocUiState.Loading)
    val photoDocUiState: StateFlow<PhotoDocUiState> = _photoDocUiState

    // UI state for before/after pairs
    private val _beforeAfterPairUiState = MutableStateFlow<BeforeAfterPairUiState>(BeforeAfterPairUiState.Loading)
    val beforeAfterPairUiState: StateFlow<BeforeAfterPairUiState> = _beforeAfterPairUiState

    // Selected photo document
    private val _selectedPhoto = MutableLiveData<PhotoDocument?>()
    val selectedPhoto: LiveData<PhotoDocument?> = _selectedPhoto

    // Current job ID
    private val _currentJobId = MutableLiveData<Long?>()
    val currentJobId: LiveData<Long?> = _currentJobId

    // Current tags filter
    private val _currentTags = MutableLiveData<List<String>>(emptyList())
    val currentTags: LiveData<List<String>> = _currentTags

    // Current search query
    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    // Annotation mode
    private val _annotationMode = MutableLiveData<AnnotationType?>(null)
    val annotationMode: LiveData<AnnotationType?> = _annotationMode

    // Current annotation color
    private val _annotationColor = MutableLiveData(0xFF0000FF.toInt()) // Default blue
    val annotationColor: LiveData<Int> = _annotationColor

    // Temporary photo URI for camera capture
    private val _tempPhotoUri = MutableLiveData<Uri?>()
    val tempPhotoUri: LiveData<Uri?> = _tempPhotoUri

    // Before/after pair mode
    private val _beforeAfterMode = MutableLiveData(false)
    val beforeAfterMode: LiveData<Boolean> = _beforeAfterMode

    // Selected before photo
    private val _selectedBeforePhoto = MutableLiveData<PhotoDocument?>()
    val selectedBeforePhoto: LiveData<PhotoDocument?> = _selectedBeforePhoto

    /**
     * Load all photo documents
     */
    fun loadAllPhotoDocuments() {
        viewModelScope.launch {
            _photoDocUiState.value = PhotoDocUiState.Loading
            getAllPhotoDocumentsUseCase()
                .catch { e ->
                    _photoDocUiState.value = PhotoDocUiState.Error(e.message ?: "Unknown error")
                }
                .collectLatest { photos ->
                    _photoDocUiState.value = PhotoDocUiState.Success(photos)
                }
        }
    }

    /**
     * Load photo documents for a specific job
     * @param jobId The ID of the job
     */
    fun loadPhotoDocumentsByJobId(jobId: Long) {
        viewModelScope.launch {
            _currentJobId.value = jobId
            _photoDocUiState.value = PhotoDocUiState.Loading
            getPhotoDocumentsByJobIdUseCase(jobId)
                .catch { e ->
                    _photoDocUiState.value = PhotoDocUiState.Error(e.message ?: "Unknown error")
                }
                .collectLatest { photos ->
                    _photoDocUiState.value = PhotoDocUiState.Success(photos)
                }
        }
    }

    /**
     * Load photo documents with specific tags
     * @param tags The tags to filter by
     */
    fun loadPhotoDocumentsByTags(tags: List<String>) {
        viewModelScope.launch {
            _currentTags.value = tags
            _photoDocUiState.value = PhotoDocUiState.Loading
            getPhotoDocumentsByTagsUseCase(tags)
                .catch { e ->
                    _photoDocUiState.value = PhotoDocUiState.Error(e.message ?: "Unknown error")
                }
                .collectLatest { photos ->
                    _photoDocUiState.value = PhotoDocUiState.Success(photos)
                }
        }
    }

    /**
     * Search photo documents
     * @param query The search query
     */
    fun searchPhotoDocuments(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
            _photoDocUiState.value = PhotoDocUiState.Loading
            searchPhotoDocumentsUseCase(query)
                .catch { e ->
                    _photoDocUiState.value = PhotoDocUiState.Error(e.message ?: "Unknown error")
                }
                .collectLatest { photos ->
                    _photoDocUiState.value = PhotoDocUiState.Success(photos)
                }
        }
    }

    /**
     * Save a photo document
     * @param photoUri The URI of the photo
     * @param title The title of the photo
     * @param description The description of the photo
     * @param tags The tags for the photo
     * @param location The location where the photo was taken
     * @return The ID of the saved photo document
     */
    suspend fun savePhotoDocument(
        photoUri: Uri,
        title: String,
        description: String = "",
        tags: List<String> = emptyList(),
        location: GeoLocation? = null
    ): Long {
        // Save the photo file
        val savedPhotoUri = savePhotoFileUseCase(photoUri)
        
        // Generate a thumbnail
        val thumbnailUri = generateThumbnailUseCase(savedPhotoUri)
        
        // Create the photo document
        val photoDocument = PhotoDocument(
            jobId = _currentJobId.value,
            title = title,
            description = description,
            photoUri = savedPhotoUri,
            thumbnailUri = thumbnailUri,
            dateCreated = Date(),
            location = location,
            tags = tags
        )
        
        // Save the photo document
        return savePhotoDocumentUseCase(photoDocument)
    }

    /**
     * Update a photo document
     * @param photoId The ID of the photo document to update
     * @param title The new title
     * @param description The new description
     * @param tags The new tags
     */
    fun updatePhotoDocument(
        photoId: Long,
        title: String,
        description: String,
        tags: List<String>
    ) {
        viewModelScope.launch {
            val photoDocument = getPhotoDocumentByIdUseCase(photoId)
            photoDocument?.let {
                val updatedPhoto = it.copy(
                    title = title,
                    description = description,
                    tags = tags
                )
                updatePhotoDocumentUseCase(updatedPhoto)
                
                // Refresh the selected photo if it's the one being updated
                if (_selectedPhoto.value?.id == photoId) {
                    _selectedPhoto.value = updatedPhoto
                }
                
                // Refresh the photo list
                refreshPhotoList()
            }
        }
    }

    /**
     * Delete a photo document
     * @param photoId The ID of the photo document to delete
     */
    fun deletePhotoDocument(photoId: Long) {
        viewModelScope.launch {
            deletePhotoDocumentUseCase(photoId)
            
            // Clear the selected photo if it's the one being deleted
            if (_selectedPhoto.value?.id == photoId) {
                _selectedPhoto.value = null
            }
            
            // Refresh the photo list
            refreshPhotoList()
        }
    }

    /**
     * Select a photo document
     * @param photoId The ID of the photo document to select
     */
    fun selectPhotoDocument(photoId: Long) {
        viewModelScope.launch {
            val photoDocument = getPhotoDocumentByIdUseCase(photoId)
            _selectedPhoto.value = photoDocument
        }
    }

    /**
     * Clear the selected photo document
     */
    fun clearSelectedPhoto() {
        _selectedPhoto.value = null
    }

    /**
     * Add an annotation to a photo
     * @param photoId The ID of the photo to annotate
     * @param type The type of annotation
     * @param x The x coordinate (0-1 relative to image width)
     * @param y The y coordinate (0-1 relative to image height)
     * @param width The width (for box annotations, 0-1 relative to image width)
     * @param height The height (for box annotations, 0-1 relative to image height)
     * @param text The text content of the annotation
     * @param color The color of the annotation
     * @return The ID of the added annotation
     */
    suspend fun addAnnotation(
        photoId: Long,
        type: AnnotationType,
        x: Float,
        y: Float,
        width: Float? = null,
        height: Float? = null,
        text: String = "",
        color: Int = _annotationColor.value ?: 0xFF0000FF.toInt()
    ): Long {
        val annotation = PhotoAnnotation(
            photoId = photoId,
            type = type,
            x = x,
            y = y,
            width = width,
            height = height,
            text = text,
            color = color
        )
        
        val annotationId = addAnnotationUseCase(annotation)
        
        // Refresh the selected photo to include the new annotation
        if (_selectedPhoto.value?.id == photoId) {
            selectPhotoDocument(photoId)
        }
        
        return annotationId
    }

    /**
     * Update an annotation
     * @param annotationId The ID of the annotation to update
     * @param x The new x coordinate
     * @param y The new y coordinate
     * @param width The new width
     * @param height The new height
     * @param text The new text content
     * @param color The new color
     */
    fun updateAnnotation(
        annotationId: Long,
        x: Float? = null,
        y: Float? = null,
        width: Float? = null,
        height: Float? = null,
        text: String? = null,
        color: Int? = null
    ) {
        viewModelScope.launch {
            val photoId = _selectedPhoto.value?.id ?: return@launch
            val annotations = getAnnotationsForPhotoUseCase(photoId)
            val annotation = annotations.find { it.id == annotationId } ?: return@launch
            
            val updatedAnnotation = annotation.copy(
                x = x ?: annotation.x,
                y = y ?: annotation.y,
                width = width ?: annotation.width,
                height = height ?: annotation.height,
                text = text ?: annotation.text,
                color = color ?: annotation.color
            )
            
            updateAnnotationUseCase(updatedAnnotation)
            
            // Refresh the selected photo to include the updated annotation
            selectPhotoDocument(photoId)
        }
    }

    /**
     * Delete an annotation
     * @param annotationId The ID of the annotation to delete
     */
    fun deleteAnnotation(annotationId: Long) {
        viewModelScope.launch {
            val photoId = _selectedPhoto.value?.id ?: return@launch
            deleteAnnotationUseCase(annotationId)
            
            // Refresh the selected photo to remove the deleted annotation
            selectPhotoDocument(photoId)
        }
    }

    /**
     * Set the annotation mode
     * @param type The type of annotation to create, or null to exit annotation mode
     */
    fun setAnnotationMode(type: AnnotationType?) {
        _annotationMode.value = type
    }

    /**
     * Set the annotation color
     * @param color The color to use for annotations
     */
    fun setAnnotationColor(color: Int) {
        _annotationColor.value = color
    }

    /**
     * Create a before/after pair
     * @param beforePhotoId The ID of the "before" photo
     * @param afterPhotoId The ID of the "after" photo
     * @param title The title of the pair
     * @param description The description of the pair
     * @return The ID of the created pair
     */
    suspend fun createBeforeAfterPair(
        beforePhotoId: Long,
        afterPhotoId: Long,
        title: String,
        description: String = ""
    ): Long {
        val pair = BeforeAfterPair(
            beforePhotoId = beforePhotoId,
            afterPhotoId = afterPhotoId,
            title = title,
            description = description
        )
        
        val pairId = createBeforeAfterPairUseCase(pair)
        
        // Refresh the before/after pairs
        loadBeforeAfterPairs()
        
        return pairId
    }

    /**
     * Load all before/after pairs
     */
    fun loadBeforeAfterPairs() {
        viewModelScope.launch {
            _beforeAfterPairUiState.value = BeforeAfterPairUiState.Loading
            getAllBeforeAfterPairsUseCase()
                .catch { e ->
                    _beforeAfterPairUiState.value = BeforeAfterPairUiState.Error(e.message ?: "Unknown error")
                }
                .collectLatest { pairs ->
                    _beforeAfterPairUiState.value = BeforeAfterPairUiState.Success(pairs)
                }
        }
    }

    /**
     * Load before/after pairs for a specific job
     * @param jobId The ID of the job
     */
    fun loadBeforeAfterPairsByJobId(jobId: Long) {
        viewModelScope.launch {
            _beforeAfterPairUiState.value = BeforeAfterPairUiState.Loading
            getBeforeAfterPairsByJobIdUseCase(jobId)
                .catch { e ->
                    _beforeAfterPairUiState.value = BeforeAfterPairUiState.Error(e.message ?: "Unknown error")
                }
                .collectLatest { pairs ->
                    _beforeAfterPairUiState.value = BeforeAfterPairUiState.Success(pairs)
                }
        }
    }

    /**
     * Set before/after mode
     * @param enabled Whether before/after mode is enabled
     */
    fun setBeforeAfterMode(enabled: Boolean) {
        _beforeAfterMode.value = enabled
        if (!enabled) {
            _selectedBeforePhoto.value = null
        }
    }

    /**
     * Select a photo as the "before" photo
     * @param photo The photo to select as the "before" photo
     */
    fun selectBeforePhoto(photo: PhotoDocument) {
        _selectedBeforePhoto.value = photo
    }

    /**
     * Set a temporary photo URI for camera capture
     * @param uri The URI of the temporary photo
     */
    fun setTempPhotoUri(uri: Uri?) {
        _tempPhotoUri.value = uri
    }

    /**
     * Refresh the current photo list based on current filters
     */
    private fun refreshPhotoList() {
        when {
            _currentJobId.value != null -> {
                loadPhotoDocumentsByJobId(_currentJobId.value!!)
            }
            _currentTags.value?.isNotEmpty() == true -> {
                loadPhotoDocumentsByTags(_currentTags.value!!)
            }
            _searchQuery.value?.isNotEmpty() == true -> {
                searchPhotoDocuments(_searchQuery.value!!)
            }
            else -> {
                loadAllPhotoDocuments()
            }
        }
    }
}
