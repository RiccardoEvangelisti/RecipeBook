package com.projects.android.ricettario.view.insert

import com.projects.android.ricettario.model.Ingrediente
import com.projects.android.ricettario.model.Ricetta
import com.projects.android.ricettario.model.enums.Portata
import com.projects.android.ricettario.model.enums.TempoPreparazione
import com.projects.android.ricettario.model.enums.UnitaDiMisura

class AggiugiRicettaUIState {
    var nome: String? = null
    var portata: Portata? = null
    var porzioni: String? = null
    var preparazione: String? = null
    var ingredientiList: MutableList<Ingrediente>? = null
    var isVegetariana: Boolean? = null
    var tempoPreparazione: TempoPreparazione? = null
    var serveCottura: Boolean? = null

    fun toRicetta(): Ricetta {
        return Ricetta(
            0,
            nome!!,
            portata!!,
            porzioni!!,
            preparazione!!,
            ingredientiList!!,
            isVegetariana!!,
            tempoPreparazione!!,
            serveCottura!!
        )
    }

    fun checkRicetta(): String? {
        if (nome.isNullOrBlank()) return "Inserire un nome"
        if (isVegetariana == null) return "Specificare se vegetariana"
        if (serveCottura == null) return "Specificare se serve cottura"
        if (porzioni == null) return "Inserire le porzioni"
        if (portata == null) return "Inserire una portata"
        if (tempoPreparazione == null) return "Inserire un tempo di preparazione"
        if (ingredientiList == null || ingredientiList!!.isEmpty()) return "Inserire almeno un ingrediente"
        for (ingrediente in ingredientiList!!) {
            if (ingrediente.nome.isNullOrBlank()) return "Inserire il nome a tutti gli ingredienti"
            if (ingrediente.unitaDiMisura == null) return "Inserire una unità di misura a tutti gli ingredienti"
            if (ingrediente.quantita.isNullOrBlank() && ingrediente.unitaDiMisura != UnitaDiMisura.QUANTOBASTA) return "Inserire una quantità a tutti gli ingredienti"
        }
        if (preparazione.isNullOrBlank()) return "Inserire una preparazione"
        return null
    }

    fun formatRicetta() {
        nome = nome!!.trim()
        preparazione = preparazione!!.trim()
        for (ingrediente in ingredientiList!!) {
            ingrediente.nome = ingrediente.nome.trim()
        }
    }
}