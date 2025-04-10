package com.example.electricianapp.presentation.viewmodel.neccodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electricianapp.domain.model.neccodes.CodeViolationCheck
import com.example.electricianapp.domain.model.neccodes.NecArticle
import com.example.electricianapp.domain.model.neccodes.NecBookmark
import com.example.electricianapp.domain.model.neccodes.NecCategory
import com.example.electricianapp.domain.model.neccodes.NecCodeUpdate
import com.example.electricianapp.domain.model.neccodes.NecSearchQuery
import com.example.electricianapp.domain.usecase.neccodes.CheckCodeViolationsUseCase
import com.example.electricianapp.domain.usecase.neccodes.GetNecArticleByIdUseCase
import com.example.electricianapp.domain.usecase.neccodes.GetNecCodeUpdatesUseCase
import com.example.electricianapp.domain.usecase.neccodes.NecCodeSearchUseCase
import com.example.electricianapp.domain.repository.neccodes.NecCodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed class representing the UI state for NEC code lookup
 */
sealed class NecCodeUiState {
    object Initial : NecCodeUiState()
    object Loading : NecCodeUiState()
    data class Success(val articles: List<NecArticle>) : NecCodeUiState()
    data class Error(val message: String) : NecCodeUiState()
}

/**
 * Sealed class representing the UI state for NEC code updates
 */
sealed class NecCodeUpdateUiState {
    object Initial : NecCodeUpdateUiState()
    object Loading : NecCodeUpdateUiState()
    data class Success(val updates: List<NecCodeUpdate>) : NecCodeUpdateUiState()
    data class Error(val message: String) : NecCodeUpdateUiState()
}

/**
 * Sealed class representing the UI state for NEC code violation checks
 */
sealed class CodeViolationUiState {
    object Initial : CodeViolationUiState()
    object Loading : CodeViolationUiState()
    data class Success(val violations: List<CodeViolationCheck>) : CodeViolationUiState()
    data class Error(val message: String) : CodeViolationUiState()
}

/**
 * Sealed class representing the UI state for NEC bookmarks
 */
sealed class NecBookmarkUiState {
    object Initial : NecBookmarkUiState()
    object Loading : NecBookmarkUiState()
    data class Success(val bookmarks: List<Pair<NecArticle, NecBookmark>>) : NecBookmarkUiState()
    data class Error(val message: String) : NecBookmarkUiState()
}

/**
 * ViewModel for the NEC code lookup screen
 */
@HiltViewModel
class NecCodeViewModel @Inject constructor(
    private val necCodeRepository: NecCodeRepository,
    private val necCodeSearchUseCase: NecCodeSearchUseCase,
    private val getNecArticleByIdUseCase: GetNecArticleByIdUseCase,
    private val getNecCodeUpdatesUseCase: GetNecCodeUpdatesUseCase,
    private val checkCodeViolationsUseCase: CheckCodeViolationsUseCase
) : ViewModel() {

    // UI states
    private val _codeUiState = MutableStateFlow<NecCodeUiState>(NecCodeUiState.Initial)
    val codeUiState: StateFlow<NecCodeUiState> = _codeUiState

    private val _updateUiState = MutableStateFlow<NecCodeUpdateUiState>(NecCodeUpdateUiState.Initial)
    val updateUiState: StateFlow<NecCodeUpdateUiState> = _updateUiState

    private val _violationUiState = MutableStateFlow<CodeViolationUiState>(CodeViolationUiState.Initial)
    val violationUiState: StateFlow<CodeViolationUiState> = _violationUiState

    private val _bookmarkUiState = MutableStateFlow<NecBookmarkUiState>(NecBookmarkUiState.Initial)
    val bookmarkUiState: StateFlow<NecBookmarkUiState> = _bookmarkUiState

    // Selected article
    private val _selectedArticle = MutableStateFlow<NecArticle?>(null)
    val selectedArticle: StateFlow<NecArticle?> = _selectedArticle

    // Related articles
    private val _relatedArticles = MutableStateFlow<List<NecArticle>>(emptyList())
    val relatedArticles: StateFlow<List<NecArticle>> = _relatedArticles

    // Search parameters
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _selectedCategory = MutableStateFlow<NecCategory?>(null)
    val selectedCategory: StateFlow<NecCategory?> = _selectedCategory

    private val _selectedYear = MutableStateFlow(2020) // Default to 2020 NEC
    val selectedYear: StateFlow<Int> = _selectedYear

    // Update parameters
    private val _fromYear = MutableStateFlow(2017)
    val fromYear: StateFlow<Int> = _fromYear

    private val _toYear = MutableStateFlow(2020)
    val toYear: StateFlow<Int> = _toYear

    // Violation check parameters
    private val _violationParameters = MutableStateFlow<Map<String, String>>(emptyMap())
    val violationParameters: StateFlow<Map<String, String>> = _violationParameters

    // Initialize
    init {
        loadBookmarks()
    }

    // Search functions
    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    fun updateSelectedCategory(category: NecCategory?) {
        _selectedCategory.value = category
    }

    fun updateSelectedYear(year: Int) {
        _selectedYear.value = year
    }

    fun searchArticles() {
        viewModelScope.launch {
            _codeUiState.value = NecCodeUiState.Loading

            val query = NecSearchQuery(
                searchText = _searchText.value,
                category = _selectedCategory.value,
                year = _selectedYear.value
            )

            necCodeSearchUseCase(query)
                .catch { e ->
                    _codeUiState.value = NecCodeUiState.Error(e.message ?: "Unknown error")
                }
                .collectLatest { articles ->
                    _codeUiState.value = NecCodeUiState.Success(articles)
                }
        }
    }

    fun getArticlesByCategory(category: NecCategory) {
        viewModelScope.launch {
            _codeUiState.value = NecCodeUiState.Loading

            necCodeRepository.getArticlesByCategory(category, _selectedYear.value)
                .catch { e ->
                    _codeUiState.value = NecCodeUiState.Error(e.message ?: "Unknown error")
                }
                .collectLatest { articles ->
                    _codeUiState.value = NecCodeUiState.Success(articles)
                }
        }
    }

    // Article functions
    fun selectArticle(articleId: Long) {
        viewModelScope.launch {
            val article = getNecArticleByIdUseCase(articleId)
            _selectedArticle.value = article

            // Load related articles
            if (article != null) {
                _relatedArticles.value = necCodeRepository.getRelatedArticles(article.id)
            }
        }
    }

    fun clearSelectedArticle() {
        _selectedArticle.value = null
        _relatedArticles.value = emptyList()
    }

    // Update functions
    fun updateFromYear(year: Int) {
        _fromYear.value = year
    }

    fun updateToYear(year: Int) {
        _toYear.value = year
    }

    fun getCodeUpdates() {
        viewModelScope.launch {
            _updateUiState.value = NecCodeUpdateUiState.Loading

            getNecCodeUpdatesUseCase(_fromYear.value, _toYear.value)
                .catch { e ->
                    _updateUiState.value = NecCodeUpdateUiState.Error(e.message ?: "Unknown error")
                }
                .collectLatest { updates ->
                    _updateUiState.value = NecCodeUpdateUiState.Success(updates)
                }
        }
    }

    // Violation check functions
    fun updateViolationParameter(key: String, value: String) {
        val currentParams = _violationParameters.value.toMutableMap()
        currentParams[key] = value
        _violationParameters.value = currentParams
    }

    fun checkViolations() {
        viewModelScope.launch {
            _violationUiState.value = CodeViolationUiState.Loading

            try {
                val violations = checkCodeViolationsUseCase(_violationParameters.value)
                _violationUiState.value = CodeViolationUiState.Success(violations)
            } catch (e: Exception) {
                _violationUiState.value = CodeViolationUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun clearViolationParameters() {
        _violationParameters.value = emptyMap()
        _violationUiState.value = CodeViolationUiState.Initial
    }

    // Bookmark functions
    fun loadBookmarks() {
        viewModelScope.launch {
            _bookmarkUiState.value = NecBookmarkUiState.Loading

            necCodeRepository.getBookmarkedArticles()
                .catch { e ->
                    _bookmarkUiState.value = NecBookmarkUiState.Error(e.message ?: "Unknown error")
                }
                .collectLatest { bookmarks ->
                    _bookmarkUiState.value = NecBookmarkUiState.Success(bookmarks)
                }
        }
    }

    fun addBookmark(articleId: Long, notes: String = "") {
        viewModelScope.launch {
            try {
                necCodeRepository.addBookmark(articleId, notes)
                loadBookmarks()
            } catch (e: Exception) {
                _bookmarkUiState.value = NecBookmarkUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateBookmark(bookmark: NecBookmark) {
        viewModelScope.launch {
            try {
                necCodeRepository.updateBookmark(bookmark)
                loadBookmarks()
            } catch (e: Exception) {
                _bookmarkUiState.value = NecBookmarkUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun removeBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            try {
                necCodeRepository.removeBookmark(bookmarkId)
                loadBookmarks()
            } catch (e: Exception) {
                _bookmarkUiState.value = NecBookmarkUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
