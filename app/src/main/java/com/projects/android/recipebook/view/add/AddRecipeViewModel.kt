package com.projects.android.recipebook.view.add

import androidx.lifecycle.ViewModel
import com.projects.android.recipebook.database.RecipeBookRepository
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddRecipeViewModel : ViewModel() {

	private val recipeBookRepository = RecipeBookRepository.get()

	private val _state: MutableStateFlow<AddRecipeUIState> = MutableStateFlow(AddRecipeUIState())
	val state: StateFlow<AddRecipeUIState?> = _state.asStateFlow() // all'esterno una versione readonly

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

	fun checkRicetta(): String? {
		return _state.value.checkRicetta()
	}

	override fun onCleared() {
		super.onCleared()
		_state.value.formatRicetta()
		_state.value.let { state -> recipeBookRepository.insertRecipe(state.toRicetta()) }
	}
}