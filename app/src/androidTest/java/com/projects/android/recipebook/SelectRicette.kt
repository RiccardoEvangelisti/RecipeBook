package com.projects.android.recipebook

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.projects.android.recipebook.database.Filters
import com.projects.android.recipebook.database.RecipeBookDao
import com.projects.android.recipebook.database.RecipeBookDatabase
import com.projects.android.recipebook.model.*
import com.projects.android.recipebook.model.enums.Course
import com.projects.android.recipebook.model.enums.PreparationTime
import com.projects.android.recipebook.model.enums.UnitOfMeasure
import com.projects.android.recipebook.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SelectRicette {

	private lateinit var db: RecipeBookDatabase
	private lateinit var dao: RecipeBookDao

	private var ricette: MutableList<Recipe> = mutableListOf()

	@Before
	fun createDb() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		db = Room.inMemoryDatabaseBuilder(context, RecipeBookDatabase::class.java).build()
		dao = db.recipeBookDao()
	}

	@After
	@Throws(IOException::class)
	fun closeDb() {
		db.close()
	}

	@Test
	@Throws(Exception::class)
	fun test_write_and_read_Singola() {
		GlobalScope.launch {
			val recipe = Recipe(
				0,
				"Pollo con patate",
				Course.SECOND,
				"1",
				"Cucinare bene",
				MutableList(1, init = { Ingredient("Petto", "1", UnitOfMeasure.GRAM) }),
				false,
				PreparationTime.THIRTY_MIN,
				true
			)
			dao.insertRecipe(recipe)

			val res = dao.getSingleRecipe(1)
			assertTrue("Test base nome", Utils.equals(recipe, res))
		}
	}

	@Test
	@Throws(Exception::class)
	fun test_write_and_read() {
		GlobalScope.launch {
			val recipe = Recipe(
				0,
				"Pollo con patate",
				Course.SECOND,
				"1",
				"Cucinare bene",
				MutableList(1, init = { Ingredient("Petto", "1", UnitOfMeasure.GRAM) }),
				false,
				PreparationTime.THIRTY_MIN,
				true
			)

			dao.insertRecipe(recipe)

			var filtro = Filters()
			filtro.string = "pollo"
			var res = dao.getRecipes(filtro).toList()
			assertTrue("Test base nome", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.string = "pollo "
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test1 formattazione di Fts4", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.string = "pollo co"
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test2 formattazione di Fts4", res.isEmpty())

			filtro = Filters()
			filtro.string = "patate"
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test3 formattazione di Fts4", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.string = "PaTaTe"
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test4 formattazione di Fts4", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.string = "Cucinare"
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test base preparazione", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.string = "pollo Cucinare BeNe"
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test search in nome e preparazione", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.string = "'"
			res = dao.getRecipes(filtro).toList()
			assertFalse("Test robustezza", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.string = "petto"
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test ingredienti", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.string = ""
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test seleziona tutto", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.courses = MutableList(1, init = { Course.SECOND })
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test solo course", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.string = "pollo"
			filtro.courses = MutableList(1, init = { Course.SECOND })
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test nome e course", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.isVeg = false
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test isVeg", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.preparationTime = PreparationTime.THIRTY_MIN
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test preparationTime", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.isCooked = true
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test serveCottura", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))

			filtro = Filters()
			filtro.string = "pollo"
			filtro.courses = MutableList(1, init = { Course.SECOND })
			filtro.isVeg = false
			filtro.preparationTime = PreparationTime.THIRTY_MIN
			filtro.isCooked = true
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test TUTTE INSIEME", res.isNotEmpty() && Utils.equals(recipe, res[0][0]))
		}
	}

	@Test
	@Throws(Exception::class)
	fun insertRecipes() {
		GlobalScope.launch {
			ricette.add(
				Recipe(
					0,
					"Pollo con patate",
					Course.SECOND,
					"1",
					"Cucinare bene",
					MutableList(1, init = { Ingredient("Petto", "1", UnitOfMeasure.GRAM) }),
					false,
					PreparationTime.FIVE_MIN,
					true
				)
			)

			ricette.add(
				Recipe(
					0,
					"Pesce con patate",
					Course.STARTER,
					"1",
					"Friggere bene",
					MutableList(1, init = { Ingredient("Pesce", "1000", UnitOfMeasure.GRAM) }),
					false,
					PreparationTime.THIRTY_MIN,
					true
				)
			)

			dao.insertRecipe(ricette[0])
			dao.insertRecipe(ricette[1])

			var filtro = Filters()
			filtro.string = "bene"
			var res = dao.getRecipes(filtro).toList()
			assertTrue("Test due preparazioni", res.size == 2)

			filtro = Filters()
			filtro.string = "Pollo pesce"
			res = dao.getRecipes(filtro).toList()
			assertFalse("Test due nomi diversi", res.size == 2)

			filtro = Filters()
			filtro.courses = MutableList(1, init = { Course.STARTER })
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test due nomi diversi", res.size == 1)

			filtro = Filters()
			filtro.preparationTime = PreparationTime.UNLIMITED
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test due nomi diversi", res.size == 2)

			filtro = Filters()
			res = dao.getRecipes(filtro).toList()
			assertTrue(
				"Test ingredienti UnitOfMeasure",
				res.size == 2 && res[0][0].ingredientsList[0].unitOfMeasure == UnitOfMeasure.GRAM && res[0][1].ingredientsList[0].unitOfMeasure == UnitOfMeasure.GRAM
			)

			filtro = Filters()
			res = dao.getRecipes(filtro).toList()
			assertTrue(
				"Test ingredienti Quantita",
				res.size == 2 && res[0][0].ingredientsList[0].quantity == "1" && res[0][1].ingredientsList[0].quantity == "1000"
			)
		}
	}

	@Test
	@Throws(Exception::class)
	fun deleteRecipes() {
		GlobalScope.launch {
			ricette.add(
				Recipe(
					0,
					"Pollo con patate",
					Course.SECOND,
					"1",
					"Cucinare bene",
					MutableList(1, init = { Ingredient("Petto", "1", UnitOfMeasure.GRAM) }),
					false,
					PreparationTime.FIVE_MIN,
					true
				)
			)

			ricette.add(
				Recipe(
					0,
					"Pesce con patate",
					Course.STARTER,
					"1",
					"Friggere bene",
					MutableList(1, init = { Ingredient("Pesce", "1000", UnitOfMeasure.GRAM) }),
					false,
					PreparationTime.THIRTY_MIN,
					true
				)
			)

			dao.insertRecipe(ricette[0])
			dao.insertRecipe(ricette[1])

			var filtro = Filters()
			filtro.string = "pesce"
			var res = dao.getRecipes(filtro).toList()
			ricette[0].id = res[0][0].id

			dao.deleteRecipe(ricette[0])

			filtro = Filters()
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test delete1", res.size == 1)
			assertTrue("Test delete2", res[0][0].name == "Pollo con patate")
		}
	}

	@Test
	@Throws(Exception::class)
	fun updateRecipe() {
		GlobalScope.launch {
			ricette.add(
				Recipe(
					0,
					"Pollo con patate",
					Course.SECOND,
					"1",
					"Cucinare bene",
					MutableList(1, init = { Ingredient("Petto", "1", UnitOfMeasure.GRAM) }),
					false,
					PreparationTime.FIVE_MIN,
					true
				)
			)

			dao.insertRecipe(ricette[0])

			var filtro = Filters()
			filtro.string = "Pollo"
			var res = dao.getRecipes(filtro).toList()
			ricette[0].id = res[0][0].id
			ricette[0].name = "Amatriciana"

			dao.updateRecipe(ricette[0])

			filtro = Filters()
			res = dao.getRecipes(filtro).toList()
			assertTrue("Test update", res[0][0].name == "Amatriciana")
		}
	}
}