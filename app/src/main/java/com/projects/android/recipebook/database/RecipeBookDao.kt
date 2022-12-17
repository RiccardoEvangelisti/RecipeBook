package com.projects.android.recipebook.database

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.projects.android.recipebook.model.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeBookDao {

	@Query("SELECT rowid, * FROM recipebook WHERE rowid=(:id)")
	fun getSingleRecipe(id: Int): Flow<Recipe>

	@RawQuery(observedEntities = [Recipe::class])
	fun getRecipes(query: SimpleSQLiteQuery): Flow<List<Recipe>>

	fun getRecipes(filters: Filters): Flow<List<Recipe>> {
		var query = "SELECT rowid, * FROM recipeBook_fts JOIN recipeBook ON rowid = docid WHERE 1 == 1"
		val args = mutableListOf<String>()
		with(filters) {
			if (!string.isNullOrBlank()) {
				query += " "
				query += "AND recipeBook_fts MATCH ?"
				args.add(string!! + "*")
			}
			courses?.let {
				if (it.isNotEmpty()) {
					query += " "
					query += "AND recipeBook.course IN ("
					for (i in 1..it.size) {
						query += "?"
						if (i != it.size) {
							query += ", "
						}
					}
					query += ")"
					it.forEach { course ->
						args.add(course.ordinal.toString())
					}
				}
			}
			isVeg?.let {
				query += " "
				query += "AND recipeBook.isVeg IS $isVeg"
			}
			preparationTime?.let {
				query += " "
				query += "AND recipeBook.preparationTime <= ?"
				args.add(it.ordinal.toString())
			}
			isCooked?.let {
				query += " "
				query += "AND recipeBook.isCooked IS $isCooked"
			}
		}
		val simpleSQLiteQuery = SimpleSQLiteQuery(query, args.toTypedArray())
		return getRecipes(simpleSQLiteQuery)
	}

	@Insert
	suspend fun insertRecipe(recipe: Recipe)

	@Delete
	fun deleteRecipe(recipe: Recipe)

	@Update
	fun updateRecipe(recipe: Recipe)
}