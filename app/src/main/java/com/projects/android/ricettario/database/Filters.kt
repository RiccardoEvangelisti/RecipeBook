package com.projects.android.ricettario.database

import com.projects.android.ricettario.model.enums.Portata
import com.projects.android.ricettario.model.enums.TempoPreparazione

data class Filters(
	var string: String? = null,
	var portata: Portata? = null,
	var isVegetariana: Boolean? = null,
	var tempoPreparazione: TempoPreparazione? = null,
	var serveCottura: Boolean? = null
)