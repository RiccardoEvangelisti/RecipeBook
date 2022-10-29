package com.projects.android.ricettario.utils

import com.projects.android.ricettario.model.Ingrediente
import com.projects.android.ricettario.model.Ricetta

class Utils {

	companion object {

		fun equals(r1: Ricetta, r2: Ricetta): Boolean {
			if (r1.nome != r2.nome) return false
			if (r1.portata != r2.portata) return false
			if (r1.porzioni != r2.porzioni) return false
			if (r1.preparazione != r2.preparazione) return false
			for (ir1 in r1.ingredientiList) {
				if (!r2.ingredientiList.any { ir2 -> equals(ir1, ir2) }) return false
			}
			for (ir2 in r2.ingredientiList) {
				if (!r1.ingredientiList.any { ir1 -> equals(ir2, ir1) }) return false
			}
			if (r1.isVegetariana != r2.isVegetariana) return false
			if (r1.tempoPreparazione != r2.tempoPreparazione) return false
			if (r1.serveCottura != r2.serveCottura) return false
			return true
		}

		fun equals(i1: Ingrediente, i2: Ingrediente): Boolean {
			if (i1.nome != i2.nome) return false
			if (i1.quantita != i2.quantita) return false
			if (i1.unitaDiMisura != i2.unitaDiMisura) return false
			return true
		}
	}
}