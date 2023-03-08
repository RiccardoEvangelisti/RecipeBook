package com.projects.android.recipebook.view.single

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.projects.android.recipebook.database.RecipeBookRepository
import com.projects.android.recipebook.utils.PictureUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SingleRecipeViewModel(recipeID: Int) : ViewModel() {

	private val recipeBookRepository = RecipeBookRepository.get()

	private val _state: MutableStateFlow<SingleRecipeState?> = MutableStateFlow(null)
	val state: StateFlow<SingleRecipeState?>
		get() = _state.asStateFlow()

	init {
		viewModelScope.launch {
			recipeBookRepository.getSingleRecipe(recipeID).collect { recipe ->
				_state.value = SingleRecipeState().also {
					it.recipe = recipe
				}
			}
		}
	}

	fun updateRecipe(onUpdate: (SingleRecipeState) -> Unit) {
		_state.update {
			it.also {
				if (it != null) {
					onUpdate(it)
				}
			}
		}
	}

	fun deleteRecipe(context: Context) {
		_state.value?.recipe?.photoFileName?.let {
			PictureUtils.deletePicture(context, it)
		}
		_state.value?.recipe?.let { recipeBookRepository.deleteRecipe(it) }
	}

	override fun onCleared() {
		super.onCleared()
		_state.value?.recipe?.let { recipeBookRepository.updateRecipe(it) }
	}
}

class SingleRecipeViewModelFactory(private val recipeID: Int) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T = SingleRecipeViewModel(recipeID) as T
}