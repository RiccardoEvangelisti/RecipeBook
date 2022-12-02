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

	private val _recipes: MutableStateFlow<List<Recipe>> = MutableStateFlow(emptyList())
	val recipes: StateFlow<List<Recipe>>
		get() = _recipes.asStateFlow()

	init {
		viewModelScope.launch {
			recipeBookRepository.getRecipes(Filters()).collect { recipes -> _recipes.value = recipes }
		}
	}

	fun getRecipes(filter: Filters) {
		viewModelScope.launch {
			recipeBookRepository.getRecipes(filter).collect { recipes -> _recipes.value = recipes }
		}
	}
}