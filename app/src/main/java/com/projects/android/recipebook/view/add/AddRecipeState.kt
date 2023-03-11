package com.projects.android.recipebook.view.add

import com.projects.android.recipebook.databinding.FragmentAddRecipeBinding
import com.projects.android.recipebook.databinding.ItemAddIngredientBinding
import com.projects.android.recipebook.model.Ingredient
import com.projects.android.recipebook.model.Recipe
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import com.projects.android.recipebook.model.enums.UnitOfMeasure
import com.projects.android.recipebook.view.add.utils.AddRecipeCheckErrors

class AddRecipeState {
	var editMode: Boolean = false
	var canceled: Boolean = false
	var id: Int? = null
	var name: String? = null
	var course: Course? = null
	var portions: String? = null
	var preparation: String? = null
	var ingredientsList: MutableList<Ingredient>? = null
	var unitIngredient: UnitOfMeasure? = null
	var isVeg: Boolean? = null
	var preparationTime: PreparationTime? = null
	var isCooked: Boolean? = null
	var pictureFileName: String? = null
	var pictureFileNamePrevious: String? = null
	var pictureFileNameTemp: String? = null

	fun toRecipe(): Recipe {
		return Recipe(
			id ?: 0, name!!, course!!, portions!!, preparation!!, ingredientsList!!, isVeg!!, preparationTime!!, isCooked!!, pictureFileName
		)
	}

	fun checkRecipe(binding: FragmentAddRecipeBinding, bindingIngredientsList: MutableList<ItemAddIngredientBinding?>): Boolean {
		// name
		return AddRecipeCheckErrors.checkName(binding.nameLayoutAdd, name) &&
				// isVeg
				AddRecipeCheckErrors.checkIsVeg(isVeg) &&
				// isCooked
				AddRecipeCheckErrors.checkIsCooked(isCooked) &&
				// course
				AddRecipeCheckErrors.checkCourse(course) &&
				// portions
				AddRecipeCheckErrors.checkPortions(binding.portionsLayoutAdd, portions) &&
				// ingredients
				AddRecipeCheckErrors.checkIngredients(binding.nameIngredientLayoutAdd, bindingIngredientsList, ingredientsList) &&
				// preparation
				AddRecipeCheckErrors.checkPreparation(binding.preparationLayoutAdd, preparation)
	}

	fun formatRecipe() {
		name = name!!.trim()
		for (ingredient in ingredientsList!!) {
			ingredient.name = ingredient.name.trim()
			if (ingredient.unitOfMeasure != UnitOfMeasure.TO_TASTE) {
				ingredient.quantity = ingredient.quantity.toInt().toString()
			}
		}
	}
}