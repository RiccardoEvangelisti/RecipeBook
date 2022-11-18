package com.projects.android.ricettario.view.insert

import androidx.lifecycle.ViewModel
import com.projects.android.ricettario.database.RicettarioRepository
import com.projects.android.ricettario.model.enums.Portata
import com.projects.android.ricettario.model.enums.TempoPreparazione
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

    init {
        _state.value.portata = Portata.SECONDO
        _state.value.tempoPreparazione = TempoPreparazione.TRENTA_MIN
        _state.value.isVegetariana = true
        _state.value.serveCottura = false
        _state.value.ingredientiList = mutableListOf()
    }

    fun updateRicetta(onUpdate: (AggiugiRicettaUIState) -> Unit) {
        _state.update { it.also { onUpdate(it) } }
    }

    fun formatRicetta(): String {
        return _state.value.formatRicetta()
    }

    override fun onCleared() {
        super.onCleared()
        _state.value?.let { state -> ricettarioRepository.insertRicetta(state.toRicetta()) }
    }
}