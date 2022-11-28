package com.projects.android.recipebook.view.add

import android.text.Editable
import androidx.core.text.getSpans
import com.projects.android.recipebook.model.Ingredient
import com.projects.android.recipebook.model.Recipe
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import com.projects.android.recipebook.model.enums.UnitOfMeasure
import com.projects.android.recipebook.view.add.tag.TagSpan

class AddRecipeUIState {
	var name: String? = null
	var course: Course? = null
	var portions: String? = null
	var preparation: Editable? = null
	private var preparationString: String? = null
	var ingredientsList: MutableList<Ingredient>? = null
	var isVegetarian: Boolean? = null
	var preparationTime: PreparationTime? = null
	var isCooked: Boolean? = null
	var photoFileName: String? = null

	fun toRicetta(): Recipe {
		return Recipe(
			0, name!!, course!!, portions!!, preparationString!!, ingredientsList!!, isVegetarian!!, preparationTime!!, isCooked!!, photoFileName
		)
	}

	fun checkRicetta(): String? {
		if (name.isNullOrBlank()) return "Inserire un nome"
		if (name!!.contains("#")) return "Non può contenere il simbolo #"
		if (isVegetarian == null) return "Specificare se vegetariana"
		if (isCooked == null) return "Specificare se serve cottura"
		if (portions == null) return "Inserire le porzioni"
		if (course == null) return "Inserire una course"
		if (preparationTime == null) return "Inserire un tempo di preparazione"
		if (ingredientsList == null || ingredientsList!!.isEmpty()) return "Inserire almeno un ingrediente"
		for (ingrediente in ingredientsList!!) {
			if (ingrediente.name.isBlank()) return "Inserire il nome a tutti gli ingredienti"
			if (ingrediente.quantity.isBlank() && ingrediente.unitOfMeasure != UnitOfMeasure.TOTASTE) return "Inserire una quantità a tutti gli ingredienti"
		}
		if (preparation.isNullOrBlank()) return "Inserire una preparazione"
		return null
	}

	fun formatRicetta() {
		name = name!!.trim()

		for (span in preparation!!.getSpans<TagSpan>()) {
			preparation!!.replace(preparation!!.getSpanStart(span), preparation!!.getSpanEnd(span), "#${span.id}")
		}
		preparationString = preparation!!.trim().toString()

		for (ingrediente in ingredientsList!!) {
			ingrediente.name = ingrediente.name.trim()
		}
	}
}