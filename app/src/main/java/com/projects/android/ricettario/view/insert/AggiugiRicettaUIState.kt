package com.projects.android.ricettario.view.insert

import com.projects.android.ricettario.model.Ingrediente
import com.projects.android.ricettario.model.Ricetta
import com.projects.android.ricettario.model.enums.Portata
import com.projects.android.ricettario.model.enums.TempoPreparazione

class AggiugiRicettaUIState {
    var nome: String? = null
    var portata: Portata? = null
    var porzioni: Int? = null
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

    fun formatRicetta(): String {
        if (nome.isNullOrBlank()) return "Inserire un nome"
        if (portata == null) return "Inserire una portata"
        if (porzioni == null) return "Inserire le porzioni"
        if (preparazione.isNullOrBlank()) return "Inserire una preparazione"
        if (ingredientiList == null || ingredientiList!!.isEmpty()) return "Inserire almeno un ingrediente"
        if (isVegetariana == null) return "Specificare se vegetariana"
        if (serveCottura == null) return "Specificare se serve cottura"
        if (tempoPreparazione == null) return "Inserire un tempo di preparazione"
        return ""
    }
}