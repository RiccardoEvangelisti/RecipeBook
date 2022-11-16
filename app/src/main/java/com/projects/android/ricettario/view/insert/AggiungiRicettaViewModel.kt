package com.projects.android.ricettario.view.insert

import androidx.lifecycle.ViewModel
import com.projects.android.ricettario.database.RicettarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AggiungiRicettaViewModel : ViewModel() {

    private val ricettarioRepository = RicettarioRepository.get()

    private val _state: MutableStateFlow<AggiugiRicettaUIState> =
        MutableStateFlow(AggiugiRicettaUIState())
    val state: StateFlow<AggiugiRicettaUIState?> =
        _state.asStateFlow() // all'esterno una versione readonly

    fun updateRicetta(onUpdate: (AggiugiRicettaUIState) -> AggiugiRicettaUIState) {
        _state.update {  onUpdate(it)  }
    }

    fun formatRicetta(): String {
        return _state.value.formatRicetta()
    }

    override fun onCleared() {
        super.onCleared()
        _state.value?.let { state -> ricettarioRepository.insertRicetta(state.toRicetta()) }
    }
}