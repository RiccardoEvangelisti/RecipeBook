package com.projects.android.ricettario.database

import com.projects.android.ricettario.model.enums.Portata
import com.projects.android.ricettario.model.enums.TempoPreparazione

data class Filters(
	val string: String? = null,
	val portata: Portata? = null,
	val isVegetariana: Boolean? = null,
	val tempoPreparazione: TempoPreparazione? = null,
	val serveCottura: Boolean? = null
)