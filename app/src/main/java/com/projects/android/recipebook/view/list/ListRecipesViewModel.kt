package com.projects.android.recipebook.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.android.recipebook.database.Filters
import com.projects.android.recipebook.database.RecipeBookRepository
import com.projects.android.recipebook.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListRecipesViewModel : ViewModel() {

	private val recipeBookRepository = RecipeBookRepository.get()

	private val _ricette: MutableStateFlow<List<Recipe>> = MutableStateFlow(emptyList())
	val ricette: StateFlow<List<Recipe>>
		get() = _ricette.asStateFlow() // all'esterno una versione readonly

	init {
		viewModelScope.launch {
			recipeBookRepository.getRecipes(Filters()).collect { ricette -> _ricette.value = ricette }
		}
	}

	fun getRicette(filtro: Filters) {
		viewModelScope.launch {
			recipeBookRepository.getRecipes(filtro).collect { ricette -> _ricette.value = ricette }
		}
	}
}