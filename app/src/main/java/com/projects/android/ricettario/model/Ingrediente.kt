package com.projects.android.ricettario.model

import com.projects.android.ricettario.model.enums.UnitaDiMisura

data class Ingrediente(var nome: String, var quantita: Int, var unitaDiMisura: UnitaDiMisura)