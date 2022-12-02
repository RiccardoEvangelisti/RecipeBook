package com.projects.android.recipebook.view.add

import android.text.Editable
import androidx.core.text.getSpans
import com.projects.android.recipebook.model.Ingredient
import com.projects.android.recipebook.model.Preparation
import com.projects.android.recipebook.model.Recipe
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import com.projects.android.recipebook.model.enums.UnitOfMeasure
import com.projects.android.recipebook.view.add.tag.TagSpan

class AddRecipeState {
	var name: String? = null
	var course: Course? = null
	var portions: String? = null
	var preparationEditable: Editable? = null
	private var preparation: Preparation? = null
	var ingredientsList: MutableList<Ingredient>? = null
	var isVeg: Boolean? = null
	var preparationTime: PreparationTime? = null
	var isCooked: Boolean? = null
	var photoFileName: String? = null

	fun toRecipe(): Recipe {
		return Recipe(
			0, name!!, course!!, portions!!, preparation!!, ingredientsList!!, isVeg!!, preparationTime!!, isCooked!!, photoFileName
		)
	}

	fun checkRecipe(): String? {
		if (name.isNullOrBlank()) return "Enter the name"
		if (name!!.contains("#")) return "The name cannot contain the # symbol"
		if (isVeg == null) return "Specify if veg"
		if (isCooked == null) return "Specify if it's cooked"
		if (portions == null) return "Enter the portions"
		if (course == null) return "Choose the course"
		if (preparationTime == null) return "Enter a preparation time"
		if (ingredientsList == null || ingredientsList!!.isEmpty()) return "Insert al least one ingredient"
		for (ingredient in ingredientsList!!) {
			if (ingredient.name.isBlank()) return "Enter the name of all ingredients"
			if (ingredient.quantity.isBlank() && ingredient.unitOfMeasure != UnitOfMeasure.TO_TASTE) return "Enter the quantity of all ingredients"
		}
		if (preparationEditable.isNullOrBlank()) return "Enter the preparation"
		return null
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
		}
	}
}