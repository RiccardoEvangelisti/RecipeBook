package com.projects.android.ricettario.model

import com.projects.android.ricettario.model.enums.UnitaDiMisura

data class Ingrediente(val nome: String, val quantita: Int, val unitaDiMisura: UnitaDiMisura)