package com.projects.android.recipebook.view.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.android.recipebook.database.Filters
import com.projects.android.recipebook.database.RecipeBookRepository
import com.projects.android.recipebook.model.Recipe
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddRecipeViewModel : ViewModel() {

	private val recipeBookRepository = RecipeBookRepository.get()

	private val _state: MutableStateFlow<AddRecipeUIState> = MutableStateFlow(AddRecipeUIState())
	val state: StateFlow<AddRecipeUIState?> = _state.asStateFlow() // all'esterno una versione readonly

	private val _recipes: MutableStateFlow<List<Recipe>> = MutableStateFlow(emptyList())
	val recipes: StateFlow<List<Recipe>>
		get() = _recipes.asStateFlow()

	init {
		_state.value.course = Course.SECOND
		_state.value.preparationTime = PreparationTime.THIRTY_MIN
		_state.value.isVegetarian = true
		_state.value.isCooked = true
		_state.value.ingredientsList = mutableListOf()
	}

	fun updateRicetta(onUpdate: (AddRecipeUIState) -> Unit) {
		_state.update { it.also { onUpdate(it) } }
	}

	fun getRecipes(filters: Filters) {
		viewModelScope.launch {
			recipeBookRepository.getRecipes(filters).collect { recipes -> _recipes.value = recipes }
		}
	}

	fun checkRicetta(): String? {
		return _state.value.checkRicetta()
	}

	override fun onCleared() {
		super.onCleared()
		_state.value.formatRicetta()
		_state.value.let { state -> recipeBookRepository.insertRecipe(state.toRicetta()) }
	}
}