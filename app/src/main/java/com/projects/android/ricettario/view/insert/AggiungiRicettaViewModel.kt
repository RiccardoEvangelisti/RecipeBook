package com.projects.android.ricettario.view.insert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.android.ricettario.database.RicettarioRepository
import com.projects.android.ricettario.model.Ingrediente
import com.projects.android.ricettario.model.Ricetta
import com.projects.android.ricettario.model.enums.Portata
import com.projects.android.ricettario.model.enums.TempoPreparazione
import com.projects.android.ricettario.model.enums.UnitaDiMisura
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AggiungiRicettaViewModel : ViewModel() {

	private val ricettarioRepository = RicettarioRepository.get()

	private val _ricetta: MutableStateFlow<Ricetta?> = MutableStateFlow(null)
	val ricetta: StateFlow<Ricetta?> = _ricetta.asStateFlow() // all'esterno una versione readonly

	init {
		viewModelScope.launch {
			_ricetta.value = Ricetta(0,
			                         "",
			                         Portata.SECONDO,
			                         1,
			                         "",
			                         List(0, init = { Ingrediente("", 0, UnitaDiMisura.CHILOGRAMMO) }),
			                         true,
			                         TempoPreparazione.TRENTA_MIN,
			                         true)
		}
	}

	fun updateRicetta(onUpdate: (Ricetta) -> Ricetta) {
		_ricetta.update { oldRicetta -> oldRicetta?.let { onUpdate(it) } }
	}

	override fun onCleared() {
		super.onCleared()
		ricetta.value?.let { ricetta -> ricettarioRepository.insertRicetta(ricetta) }
	}
}