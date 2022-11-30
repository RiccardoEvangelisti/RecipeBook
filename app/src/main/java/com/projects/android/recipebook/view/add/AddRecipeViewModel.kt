package com.projects.android.recipebook.view.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.android.recipebook.database.Filters
import com.projects.android.recipebook.database.RecipeBookRepository
import com.projects.android.recipebook.model.Recipe
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddRecipeViewModel : ViewModel() {

	private val recipeBookRepository = RecipeBookRepository.get()

	private val _state: MutableStateFlow<AddRecipeState> = MutableStateFlow(AddRecipeState())
	val state: StateFlow<AddRecipeState?> = _state.asStateFlow()

	private val _recipes: MutableStateFlow<List<Recipe>> = MutableStateFlow(emptyList())
	val recipes: StateFlow<List<Recipe>>
		get() = _recipes.asStateFlow()

	init {
		getRecipes(Filters())

		_state.value.course = Course.SECOND
		_state.value.preparationTime = PreparationTime.THIRTY_MIN
		_state.value.isVegetarian = true
		_state.value.isCooked = true
		_state.value.ingredientsList = mutableListOf()
	}

	fun updateRecipe(onUpdate: (AddRecipeState) -> Unit) {
		_state.update { it.also { onUpdate(it) } }
	}

	fun getRecipes(filters: Filters) {
		viewModelScope.launch {
			recipeBookRepository.getRecipes(filters).collect { recipes -> _recipes.value = recipes }
		}
	}

	fun checkRecipe(): String? {
		return _state.value.checkRicetta()
	}

	override fun onCleared() {
		super.onCleared()
		_state.value.formatRicetta()
		_state.value.let { state -> recipeBookRepository.insertRecipe(state.toRicetta()) }
	}
}