package com.example.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.AppError
import com.example.domain.model.Result
import com.example.domain.model.Song
import com.example.domain.usecase.SearchMusicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<Song> = emptyList(),
    val error: AppError? = null
)

@HiltViewModel
@OptIn(FlowPreview::class)
class SearchViewModel @Inject constructor(
    private val searchMusicUseCase: SearchMusicUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _query
                .debounce(500L)
                .filter { it.isNotBlank() }
                .collect { performSearch(it) }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        _uiState.value = _uiState.value.copy(query = newQuery)
        if (newQuery.isBlank()) {
            _uiState.value = _uiState.value.copy(results = emptyList(), error = null, isLoading = false)
        }
    }

    fun onSearchClicked() {
        if (_query.value.isNotBlank()) {
            performSearch(_query.value)
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = searchMusicUseCase(query)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        results = result.data,
                        error = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.error
                    )
                }
            }
        }
    }
}
