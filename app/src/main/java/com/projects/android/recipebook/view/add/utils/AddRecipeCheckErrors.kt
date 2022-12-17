package com.projects.android.recipebook.view.add.utils

import com.google.android.material.textfield.TextInputLayout
import com.projects.android.recipebook.databinding.ItemAddIngredientBinding
import com.projects.android.recipebook.model.Ingredient
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.UnitOfMeasure

class AddRecipeCheckErrors {

	companion object {
		fun checkName(nameLayoutAdd: TextInputLayout, name: String?): Boolean {
			if (name.isNullOrBlank()) {
				nameLayoutAdd.error = "Required"
				return false
			} else {
				nameLayoutAdd.error = null
			}
			if (name.contains("#")) {
				nameLayoutAdd.error = "Cannot contain the # symbol"
				return false
			} else {
				nameLayoutAdd.error = null
			}
			return true
		}

		fun checkIsVeg(isVeg: Boolean?): Boolean {
			return isVeg != null
		}

		fun checkIsCooked(isCooked: Boolean?): Boolean {
			return isCooked != null
		}

		fun checkCourse(course: Course?): Boolean {
			return course != null
		}

		fun checkPortions(portionsLayoutAdd: TextInputLayout, portions: String?): Boolean {
			if (portions.isNullOrBlank()) {
				portionsLayoutAdd.error = "Required"
				return false
			} else {
				portionsLayoutAdd.error = null
			}
			return true
		}

		fun checkIngredients(
			nameIngredientLayoutAdd: TextInputLayout,
			bindingIngredientsList: MutableList<ItemAddIngredientBinding?>,
			ingredientsList: MutableList<Ingredient>?
		): Boolean {
			if (ingredientsList == null || ingredientsList.isEmpty()) {
				nameIngredientLayoutAdd.error = "Required at least one"
				return false
			} else {
				nameIngredientLayoutAdd.error = null
			}
			var check = true
			for ((index, ingredient) in ingredientsList.withIndex()) {
				if (!checkQuantityIngredientItem(
						bindingIngredientsList[index]!!.quantityIngredientItemLayoutAdd, ingredient.quantity, ingredient.unitOfMeasure
					) || !checkNameIngredientItem(bindingIngredientsList[index]!!.nameIngredientItemLayoutAdd, ingredient.name)
				) check = false
			}
			return check
		}

		fun checkQuantityIngredientItem(quantityIngredientItemLayoutAdd: TextInputLayout, quantity: String, unitOfMeasure: UnitOfMeasure): Boolean {
			if ((quantity.isBlank() || quantity.toInt() <= 0) && unitOfMeasure != UnitOfMeasure.TO_TASTE) {
				quantityIngredientItemLayoutAdd.error = "Required"
				return false
			} else {
				quantityIngredientItemLayoutAdd.error = null
			}
			return true
		}

		fun checkNameIngredientItem(nameIngredientItemLayoutAdd: TextInputLayout, name: String): Boolean {
			if (name.isBlank()) {
				nameIngredientItemLayoutAdd.error = "Required"
				return false
			} else {
				nameIngredientItemLayoutAdd.error = null
			}
			return true
		}

		fun checkPreparation(preparationLayoutAdd: TextInputLayout, preparation: String?): Boolean {
			if (preparation.isNullOrBlank()) {
				preparationLayoutAdd.error = "Required"
				return false
			} else {
				preparationLayoutAdd.error = null
			}
			return true
		}
	}
}