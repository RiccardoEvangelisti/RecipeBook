package com.projects.android.ricettario.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.android.ricettario.databinding.ItemListRicetteBinding
import com.projects.android.ricettario.model.Ricetta

class RicetteListAdapter(private val ricette: List<Ricetta>, private val onRicettaClicked: (ricettaID: Int) -> Unit) :
		RecyclerView.Adapter<RicetteListHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RicetteListHolder {
		val inflater = LayoutInflater.from(parent.context)
		val binding = ItemListRicetteBinding.inflate(inflater, parent, false)
		return RicetteListHolder(binding)
	}

	override fun onBindViewHolder(holder: RicetteListHolder, position: Int) {
		val ricetta = ricette[position]
		holder.bind(ricetta, onRicettaClicked)
	}

	override fun getItemCount(): Int {
		return ricette.size
	}
}

class RicetteListHolder(private val binding: ItemListRicetteBinding) : RecyclerView.ViewHolder(binding.root) {

	fun bind(ricetta: Ricetta, onRicettaClicked: (ricettaID: Int) -> Unit) {

		binding.apply {
			root.setOnClickListener {
				onRicettaClicked(ricetta.id)
			}

			nomeItemList.text = ricetta.nome
			portataItemList.text = ricetta.portata.toString()
			tempoPreparazioneItemList.text = ricetta.tempoPreparazione.toString()
			vegetarianoItemList.text = if (ricetta.isVegetariana) "VEG" else "ONN"
			serveCotturaItemList.text = if (ricetta.serveCottura) "COTTURA" else "CRUDO"
		}
	}
}