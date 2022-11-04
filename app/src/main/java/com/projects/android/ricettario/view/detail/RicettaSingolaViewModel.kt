package com.projects.android.ricettario.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.projects.android.ricettario.database.RicettarioRepository
import com.projects.android.ricettario.model.Ricetta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RicettaSingolaViewModel(ricettaID: Int) : ViewModel() {

	private val ricettarioRepository = RicettarioRepository.get()

	private val _ricetta: MutableStateFlow<Ricetta?> = MutableStateFlow(null)
	val ricetta: StateFlow<Ricetta?> = _ricetta.asStateFlow() // all'esterno una versione readonly

	init {
		viewModelScope.launch {
			_ricetta.value = ricettarioRepository.getRicettaSingola(ricettaID)
		}
	}

	fun updateRicetta(onUpdate: (Ricetta) -> Ricetta) {
		_ricetta.update { oldRicetta -> oldRicetta?.let { onUpdate(it) } }
	}

	fun deleteRicetta() {
		_ricetta.value?.let { ricettarioRepository.deleteRicetta(it) }
	}

	override fun onCleared() {
		super.onCleared()
		ricetta.value?.let { ricetta -> ricettarioRepository.updateRicetta(ricetta) }
	}
}

class RicettaSingolaViewModelFactory(private val ricettaID: Int) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T =
		RicettaSingolaViewModel(ricettaID) as T
}