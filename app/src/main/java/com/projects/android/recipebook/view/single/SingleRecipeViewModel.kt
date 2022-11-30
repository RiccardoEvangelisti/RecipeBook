package com.projects.android.recipebook.view.single

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.projects.android.recipebook.database.RecipeBookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SingleRecipeViewModel(ricettaID: Int) : ViewModel() {

	private val recipeBookRepository = RecipeBookRepository.get()

	private val _state: MutableStateFlow<SingleRecipeState> = MutableStateFlow(SingleRecipeState())
	val state: StateFlow<SingleRecipeState>
		get() = _state.asStateFlow()

	init {
		viewModelScope.launch {
			_state.value.recipe = recipeBookRepository.getSingleRecipe(ricettaID)

			for (tag in _state.value.recipe!!.preparation.tags) {
				_state.value.tagNames = mutableListOf()
				_state.value.tagNames!!.add(recipeBookRepository.getSingleRecipe(tag.toInt()).name)
			}

		}
	}

	fun updateRecipe(onUpdate: (SingleRecipeState) -> Unit) {
		_state.update { it.also { onUpdate(it) } }
	}

	fun deleteRecipe() {
		_state.value.recipe?.let { recipeBookRepository.deleteRecipe(it) }
	}

	override fun onCleared() {
		super.onCleared()
		_state.value.recipe?.let { recipeBookRepository.updateRecipe(it) }
	}
}

class SingleRecipeViewModelFactory(private val recipeID: Int) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T = SingleRecipeViewModel(recipeID) as T
}