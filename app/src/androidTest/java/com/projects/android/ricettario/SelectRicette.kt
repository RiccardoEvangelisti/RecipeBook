package com.projects.android.ricettario

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.projects.android.ricettario.database.Filters
import com.projects.android.ricettario.database.RicettarioDao
import com.projects.android.ricettario.database.RicettarioDatabase
import com.projects.android.ricettario.model.*
import com.projects.android.ricettario.model.enums.Portata
import com.projects.android.ricettario.model.enums.TempoPreparazione
import com.projects.android.ricettario.model.enums.UnitaDiMisura
import com.projects.android.ricettario.utils.Utils
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SelectRicette {

	private lateinit var db: RicettarioDatabase
	private lateinit var dao: RicettarioDao

	private var ricette: MutableList<Ricetta> = mutableListOf<Ricetta>()

	@Before
	fun createDb() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		db = Room.inMemoryDatabaseBuilder(context, RicettarioDatabase::class.java).build()
		dao = db.ricettarioDao()
	}

	@After
	@Throws(IOException::class)
	fun closeDb() {
		db.close()
	}

	@Test
	@Throws(Exception::class)
	fun test_Scrivi_e_Leggi() {
		val ricetta = Ricetta("Pollo con patate",
		                      Portata.SECONDO,
		                      1,
		                      "Cucinare bene",
		                      List(1, init = { Ingrediente("Petto", 1, UnitaDiMisura.CHILOGRAMMO) }),
		                      false,
		                      TempoPreparazione.TRENTA_MIN,
		                      true)

		dao.insertRicetta(ricetta)

		var res = dao.getRicette(Filters("pollo"))
		assertTrue("Test base nome", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters("pollo "))
		assertTrue("Test1 formattazione di Fts4", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters("pollo co"))
		assertTrue("Test2 formattazione di Fts4", res.isEmpty())

		res = dao.getRicette(Filters("patate"))
		assertTrue("Test3 formattazione di Fts4", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters("PaTaTe"))
		assertTrue("Test4 formattazione di Fts4", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters("Cucinare"))
		assertTrue("Test base preparazione", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters("pollo Cucinare BeNe"))
		assertTrue("Test search in nome e preparazione", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters("'"))
		assertFalse("Test robustezza", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters("petto"))
		assertTrue("Test ingredienti", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters(""))
		assertTrue("Test seleziona tutto", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters(null, Portata.SECONDO))
		assertTrue("Test solo portata", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters("pollo", Portata.SECONDO))
		assertTrue("Test nome e portata", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters(null, null, false))
		assertTrue("Test isVegetariana", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters(null, null, null, TempoPreparazione.TRENTA_MIN))
		assertTrue("Test tempoPreparazione", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters(null, null, null, null, true))
		assertTrue("Test serveCottura", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		res = dao.getRicette(Filters("pollo", Portata.SECONDO, false, TempoPreparazione.TRENTA_MIN, true))
		assertTrue("Test TUTTE INSIEME", res.isNotEmpty() && Utils.equals(ricetta, res[0]))
	}

	@Test
	@Throws(Exception::class)
	fun insertRicette() {
		ricette.add(Ricetta("Pollo con patate",
		                    Portata.SECONDO,
		                    1,
		                    "Cucinare bene",
		                    List(1, init = { Ingrediente("Petto", 1, UnitaDiMisura.CHILOGRAMMO) }),
		                    false,
		                    TempoPreparazione.CINQUE_MIN,
		                    true))

		ricette.add(Ricetta("Pesce con patate",
		                    Portata.ANTIPASTO,
		                    1,
		                    "Friggere bene",
		                    List(1, init = { Ingrediente("Pesce", 1000, UnitaDiMisura.GRAMMO) }),
		                    false,
		                    TempoPreparazione.TRENTA_MIN,
		                    true))

		dao.insertRicetta(ricette[0])
		dao.insertRicetta(ricette[1])

		var res = dao.getRicette(Filters("bene"))
		assertTrue("Test due preparazioni", res.size == 2)

		res = dao.getRicette(Filters("Pollo pesce"))
		assertFalse("Test due nomi diversi", res.size == 2)

		res = dao.getRicette(Filters(null, Portata.ANTIPASTO))
		assertTrue("Test due nomi diversi", res.size == 1)

		res = dao.getRicette(Filters(null, null, null, TempoPreparazione.ILLIMITATO_TEMPO))
		assertTrue("Test due nomi diversi", res.size == 2)

		res = dao.getRicette(Filters())
		assertTrue("Test ingredienti UnitaDiMisura",
		           res.size == 2 && res[0].ingredientiList[0].unitaDiMisura == UnitaDiMisura.CHILOGRAMMO && res[1].ingredientiList[0].unitaDiMisura == UnitaDiMisura.GRAMMO)

		res = dao.getRicette(Filters())
		assertTrue("Test ingredienti Quantita",
		           res.size == 2 && res[0].ingredientiList[0].quantita == 1 && res[1].ingredientiList[0].quantita == 1000)
	}
}