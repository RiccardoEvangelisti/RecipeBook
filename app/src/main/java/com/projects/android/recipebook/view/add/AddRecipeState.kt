package com.projects.android.recipebook.view.add

import android.text.Editable
import androidx.core.text.getSpans
import com.projects.android.recipebook.databinding.FragmentAddRecipeBinding
import com.projects.android.recipebook.databinding.ItemAddIngredientBinding
import com.projects.android.recipebook.model.Ingredient
import com.projects.android.recipebook.model.Preparation
import com.projects.android.recipebook.model.Recipe
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import com.projects.android.recipebook.model.enums.UnitOfMeasure
import com.projects.android.recipebook.view.add.tag.TagSpan
import com.projects.android.recipebook.view.add.utils.AddRecipeCheckErrors

class AddRecipeState {
	var editMode: Boolean = false
	var canceled: Boolean = false
	var name: String? = null
	var course: Course? = null
	var portions: String? = null
	var preparationEditable: Editable? = null
	private var preparation: Preparation? = null
	var ingredientsList: MutableList<Ingredient>? = null
	var unitIngredient: UnitOfMeasure? = null
	var isVeg: Boolean? = null
	var preparationTime: PreparationTime? = null
	var isCooked: Boolean? = null
	var photoFileName: String? = null

	fun toRecipe(): Recipe {
		return Recipe(
			0, name!!, course!!, portions!!, preparation!!, ingredientsList!!, isVeg!!, preparationTime!!, isCooked!!, photoFileName
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
				AddRecipeCheckErrors.checkPreparation(binding.preparationLayoutAdd, preparationEditable)
	}

	fun formatRecipe() {
		name = name!!.trim()

		// search for every span, replace it with "#" and save the tag id into the preparation.tags array
		val tagsList = mutableListOf<String>()
		for (span in preparationEditable!!.getSpans<TagSpan>()) {
			preparationEditable!!.replace(preparationEditable!!.getSpanStart(span), preparationEditable!!.getSpanEnd(span), "#")
			tagsList.add(span.id)
		}
		val prepText = preparationEditable!!.trim().toString()
		preparation = Preparation(prepText, tagsList)

		for (ingredient in ingredientsList!!) {
			ingredient.name = ingredient.name.trim()
			if (ingredient.unitOfMeasure != UnitOfMeasure.TO_TASTE) {
				ingredient.quantity = ingredient.quantity.toInt().toString()
			}
		}
	}
}