package com.projects.android.ricettario.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.android.ricettario.database.Filters
import com.projects.android.ricettario.database.RicettarioRepository
import com.projects.android.ricettario.model.Ricetta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RicetteListViewModel : ViewModel() {

	private val ricettarioRepository = RicettarioRepository.get()

	private val _ricette: MutableStateFlow<List<Ricetta>> = MutableStateFlow(emptyList())
	val ricette: StateFlow<List<Ricetta>>
		get() = _ricette.asStateFlow() // all'esterno una versione readonly

	init {
		viewModelScope.launch {
			ricettarioRepository.getRicette(Filters()).collect { ricette -> _ricette.value = ricette }
		}
	}

	fun getRicette(filtro: Filters) {
		viewModelScope.launch {
			ricettarioRepository.getRicette(filtro).collect { ricette -> _ricette.value = ricette }
		}
	}
}