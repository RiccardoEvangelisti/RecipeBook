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

	private var ricette: MutableList<Ricetta> = mutableListOf()

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
	fun test_Scrivi_e_Leggi_Singola() {
		val ricetta = Ricetta("Pollo con patate",
		                      Portata.SECONDO,
		                      1,
		                      "Cucinare bene",
		                      List(1, init = { Ingrediente("Petto", 1, UnitaDiMisura.CHILOGRAMMO) }),
		                      false,
		                      TempoPreparazione.TRENTA_MIN,
		                      true)
		dao.insertRicetta(ricetta)

		val res = dao.getRicettaSingola(1)
		assertTrue("Test base nome", Utils.equals(ricetta, res))
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

		var filtro = Filters()
		filtro.string = "pollo"
		var res = dao.getRicette(filtro)
		assertTrue("Test base nome", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.string = "pollo "
		res = dao.getRicette(filtro)
		assertTrue("Test1 formattazione di Fts4", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.string = "pollo co"
		res = dao.getRicette(filtro)
		assertTrue("Test2 formattazione di Fts4", res.isEmpty())

		filtro = Filters()
		filtro.string = "patate"
		res = dao.getRicette(filtro)
		assertTrue("Test3 formattazione di Fts4", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.string = "PaTaTe"
		res = dao.getRicette(filtro)
		assertTrue("Test4 formattazione di Fts4", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.string = "Cucinare"
		res = dao.getRicette(filtro)
		assertTrue("Test base preparazione", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.string = "pollo Cucinare BeNe"
		res = dao.getRicette(filtro)
		assertTrue("Test search in nome e preparazione", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.string = "'"
		res = dao.getRicette(filtro)
		assertFalse("Test robustezza", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.string = "petto"
		res = dao.getRicette(filtro)
		assertTrue("Test ingredienti", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.string = ""
		res = dao.getRicette(filtro)
		assertTrue("Test seleziona tutto", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.portata = Portata.SECONDO
		res = dao.getRicette(filtro)
		assertTrue("Test solo portata", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.string = "pollo"
		filtro.portata = Portata.SECONDO
		res = dao.getRicette(filtro)
		assertTrue("Test nome e portata", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.isVegetariana = false
		res = dao.getRicette(filtro)
		assertTrue("Test isVegetariana", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.tempoPreparazione = TempoPreparazione.TRENTA_MIN
		res = dao.getRicette(filtro)
		assertTrue("Test tempoPreparazione", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.serveCottura = true
		res = dao.getRicette(filtro)
		assertTrue("Test serveCottura", res.isNotEmpty() && Utils.equals(ricetta, res[0]))

		filtro = Filters()
		filtro.string = "pollo"
		filtro.portata = Portata.SECONDO
		filtro.isVegetariana = false
		filtro.tempoPreparazione = TempoPreparazione.TRENTA_MIN
		filtro.serveCottura = true
		res = dao.getRicette(filtro)
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

		var filtro = Filters()
		filtro.string = "bene"
		var res = dao.getRicette(filtro)
		assertTrue("Test due preparazioni", res.size == 2)

		filtro = Filters()
		filtro.string = "Pollo pesce"
		res = dao.getRicette(filtro)
		assertFalse("Test due nomi diversi", res.size == 2)

		filtro = Filters()
		filtro.portata = Portata.ANTIPASTO
		res = dao.getRicette(filtro)
		assertTrue("Test due nomi diversi", res.size == 1)

		filtro = Filters()
		filtro.tempoPreparazione = TempoPreparazione.ILLIMITATO_TEMPO
		res = dao.getRicette(filtro)
		assertTrue("Test due nomi diversi", res.size == 2)

		filtro = Filters()
		res = dao.getRicette(filtro)
		assertTrue("Test ingredienti UnitaDiMisura",
		           res.size == 2 && res[0].ingredientiList[0].unitaDiMisura == UnitaDiMisura.CHILOGRAMMO && res[1].ingredientiList[0].unitaDiMisura == UnitaDiMisura.GRAMMO)

		filtro = Filters()
		res = dao.getRicette(filtro)
		assertTrue("Test ingredienti Quantita",
		           res.size == 2 && res[0].ingredientiList[0].quantita == 1 && res[1].ingredientiList[0].quantita == 1000)
	}

	@Test
	@Throws(Exception::class)
	fun deleteRicette() {
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

		var filtro = Filters()
		filtro.string = "pesce"
		var res = dao.getRicette(filtro)
		ricette[0].id = res[0].id

		dao.deleteRicetta(ricette[0])

		filtro = Filters()
		res = dao.getRicette(filtro)
		assertTrue("Test delete1", res.size == 1)
		assertTrue("Test delete2", res[0].nome == "Pollo con patate")
	}

	@Test
	@Throws(Exception::class)
	fun updateRicetta() {
		ricette.add(Ricetta("Pollo con patate",
		                    Portata.SECONDO,
		                    1,
		                    "Cucinare bene",
		                    List(1, init = { Ingrediente("Petto", 1, UnitaDiMisura.CHILOGRAMMO) }),
		                    false,
		                    TempoPreparazione.CINQUE_MIN,
		                    true))

		dao.insertRicetta(ricette[0])

		var filtro = Filters()
		filtro.string = "Pollo"
		var res = dao.getRicette(filtro)
		ricette[0].id = res[0].id
		ricette[0].nome = "Amatriciana"

		dao.updateRicetta(ricette[0])

		filtro = Filters()
		res = dao.getRicette(filtro)
		assertTrue("Test update", res[0].nome == "Amatriciana")
	}
}