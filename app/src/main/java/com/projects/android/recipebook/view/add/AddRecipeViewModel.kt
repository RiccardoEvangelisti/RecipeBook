package com.projects.android.recipebook.view.add

import android.text.Editable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.projects.android.recipebook.database.Filters
import com.projects.android.recipebook.database.RecipeBookRepository
import com.projects.android.recipebook.databinding.FragmentAddRecipeBinding
import com.projects.android.recipebook.databinding.ItemAddIngredientBinding
import com.projects.android.recipebook.model.Recipe
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddRecipeViewModel(recipeID: Int) : ViewModel() {

	private val recipeBookRepository = RecipeBookRepository.get()

	private val _state: MutableStateFlow<AddRecipeState?> = MutableStateFlow(null)
	val state: StateFlow<AddRecipeState?>
		get() = _state.asStateFlow()

	// recipes searched for tag preparation system
	private val _recipes: MutableStateFlow<List<Recipe>?> = MutableStateFlow(null)
	val recipes: StateFlow<List<Recipe>?>
		get() = _recipes.asStateFlow()

	init {
		viewModelScope.launch {
			// If the navigation comes from SingleRecipe, enable the edit mode
			if (recipeID >= 0) {
				recipeBookRepository.getSingleRecipe(recipeID).also { recipe ->
					_state.value = AddRecipeState().also {
						it.editMode = true
						it.name = recipe.name
						it.portions = recipe.portions
						it.isVeg = recipe.isVeg
						it.isCooked = recipe.isCooked
						it.course = recipe.course
						it.preparationTime = recipe.preparationTime
						it.preparationEditable = Editable.Factory.getInstance().newEditable(recipe.preparation.text)
						it.ingredientsList = recipe.ingredientsList
					}
				}
			} else {
				// Default values
				_state.value = AddRecipeState().also {
					it.course = Course.SECOND
					it.preparationTime = PreparationTime.THIRTY_MIN
					it.isVeg = true
					it.isCooked = true
					it.portions = 1.toString()
					it.ingredientsList = mutableListOf()
				}
			}
		}
	}

	fun updateState(onUpdate: (AddRecipeState) -> Unit) {
		_state.update {
			it?.also {
				onUpdate(it)
			}
		}
	}

	fun getRecipes(filters: Filters) {
		viewModelScope.launch {
			recipeBookRepository.getRecipes(filters).collect { recipes -> _recipes.value = recipes }
		}
	}

	fun checkRecipe(binding: FragmentAddRecipeBinding, bindingIngredientsList: MutableList<ItemAddIngredientBinding?>): Boolean {
		return _state.value!!.checkRecipe(binding, bindingIngredientsList)
	}

	override fun onCleared() {
		super.onCleared()
		if (!_state.value?.canceled!!) {
			_state.value?.formatRecipe()
			if (state.value!!.editMode) {
				_state.value?.let { state -> recipeBookRepository.updateRecipe(state.toRecipe()) }
			} else {
				_state.value?.let { state -> recipeBookRepository.insertRecipe(state.toRecipe()) }
			}
		}
	}
}

class AddRecipeViewModelFactory(private val recipeID: Int) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T = AddRecipeViewModel(recipeID) as T
}