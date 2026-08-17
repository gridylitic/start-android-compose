package fyi.ingrid.starter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// The three states a fetch can be in, made explicit as UI state - the screens
// render one branch each. Loading and Error are not afterthoughts here; they
// are first-class values the view model emits.
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
    data class Ready<T>(val data: T) : UiState<T>
}

class ListViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<PokemonSummary>>>(UiState.Loading)
    val state: StateFlow<UiState<List<PokemonSummary>>> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            _state.value = try {
                UiState.Ready(PokeApi.list())
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Something went wrong.")
            }
        }
    }
}

class DetailViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<Pokemon>>(UiState.Loading)
    val state: StateFlow<UiState<Pokemon>> = _state.asStateFlow()

    fun load(name: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            _state.value = try {
                UiState.Ready(PokeApi.detail(name))
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Something went wrong.")
            }
        }
    }
}
