package com.projects.android.recipebook.utils

import android.content.Context
import com.projects.android.recipebook.model.Ingredient
import com.projects.android.recipebook.model.Recipe

class Utils {

	companion object {

		fun equals(r1: Recipe, r2: Recipe): Boolean {
			if (r1.name != r2.name) return false
			if (r1.course != r2.course) return false
			if (r1.portions != r2.portions) return false
			if (r1.preparation != r2.preparation) return false
			for (ir1 in r1.ingredientsList) {
				if (!r2.ingredientsList.any { ir2 -> equals(ir1, ir2) }) return false
			}
			for (ir2 in r2.ingredientsList) {
				if (!r1.ingredientsList.any { ir1 -> equals(ir2, ir1) }) return false
			}
			if (r1.isVeg != r2.isVeg) return false
			if (r1.preparationTime != r2.preparationTime) return false
			if (r1.isCooked != r2.isCooked) return false
			return true
		}

		fun equals(i1: Ingredient, i2: Ingredient): Boolean {
			if (i1.name != i2.name) return false
			if (i1.quantity != i2.quantity) return false
			if (i1.unitOfMeasure != i2.unitOfMeasure) return false
			return true
		}

		fun clearCache(context: Context) {
			val dir = context.applicationContext.cacheDir
			if (dir.isDirectory) {
				if (dir.listFiles() != null) {
					for (file in dir.listFiles()!!) {
						file.delete()
					}
				}
			}
		}
	}
}