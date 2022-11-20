package com.projects.android.recipebook.view.single

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.projects.android.recipebook.database.RecipeBookRepository
import com.projects.android.recipebook.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RicettaSingolaViewModel(ricettaID: Int) : ViewModel() {

	private val recipeBookRepository = RecipeBookRepository.get()

	private val _recipe: MutableStateFlow<Recipe?> = MutableStateFlow(null)
	val recipe: StateFlow<Recipe?> = _recipe.asStateFlow() // all'esterno una versione readonly

	init {
		viewModelScope.launch {
			_recipe.value = recipeBookRepository.getSingleRecipe(ricettaID)
		}
	}

	fun updateRicetta(onUpdate: (Recipe) -> Recipe) {
		_recipe.update { oldRicetta -> oldRicetta?.let { onUpdate(it) } }
	}

	fun deleteRicetta() {
		_recipe.value?.let { recipeBookRepository.deleteRecipe(it) }
	}

	override fun onCleared() {
		super.onCleared()
		recipe.value?.let { ricetta -> recipeBookRepository.updateRecipe(ricetta) }
	}
}

class RicettaSingolaViewModelFactory(private val ricettaID: Int) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T = RicettaSingolaViewModel(ricettaID) as T
}