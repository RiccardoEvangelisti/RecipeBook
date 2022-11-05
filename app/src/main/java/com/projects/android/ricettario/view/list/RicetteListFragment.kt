package com.projects.android.ricettario.view.list

import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.android.ricettario.R
import com.projects.android.ricettario.database.Filters
import com.projects.android.ricettario.databinding.FragmentRicetteListBinding
import com.projects.android.ricettario.model.enums.Portata
import com.projects.android.ricettario.model.enums.TempoPreparazione
import kotlinx.coroutines.launch

class RicetteListFragment : Fragment() {

	// VIEW MODEL
	private val ricetteListViewModel: RicetteListViewModel by viewModels()

	// VIEW BINDING
	private var _binding: FragmentRicetteListBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

		// RECYCLER VIEW
		_binding = FragmentRicetteListBinding.inflate(inflater, container, false)
		binding.ricetteRecyclerView.layoutManager = LinearLayoutManager(context)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {

			cercaText.setOnEditorActionListener { _, actionId, _ ->
				return@setOnEditorActionListener when (actionId) {
					EditorInfo.IME_ACTION_SEARCH -> {
						cercaButton.callOnClick()
						true
					}

					else -> false
				}
			}

			vegetarianoSwitch.setOnClickListener {
				// se ho spento vegetarianoSwitch ed è già spento nonVegetarianoSwitch
				if (!vegetarianoSwitch.isChecked && !nonVegetarianoSwitch.isChecked) {
					nonVegetarianoSwitch.isChecked = true
				}
			}
			nonVegetarianoSwitch.setOnClickListener {
				// se ho spento nonVegetarianoSwitch ed è già spento vegetarianoSwitch
				if (!nonVegetarianoSwitch.isChecked && !vegetarianoSwitch.isChecked) {
					vegetarianoSwitch.isChecked = true
				}
			}

			serveCotturaSwitch.setOnClickListener {
				if (!serveCotturaSwitch.isChecked && !nonServeCotturaSwitch.isChecked) {
					nonServeCotturaSwitch.isChecked = true
				}
			}
			nonServeCotturaSwitch.setOnClickListener {
				if (!nonServeCotturaSwitch.isChecked && !serveCotturaSwitch.isChecked) {
					serveCotturaSwitch.isChecked = true
				}
			}


			cercaButton.setOnClickListener {
				val filtro = Filters()

				if (!cercaText.text.isNullOrBlank()) {
					filtro.string = cercaText.text.toString()
				}

				filtro.portate = mutableListOf()
				if (!antipastoToggleButton.isChecked && !primoToggleButton.isChecked && !secondoToggleButton.isChecked && !contornoToggleButton.isChecked && !dolceToggleButton.isChecked) {
					antipastoToggleButton.toggle()
					primoToggleButton.toggle()
					secondoToggleButton.toggle()
					contornoToggleButton.toggle()
					dolceToggleButton.toggle()
				}
				if (antipastoToggleButton.isChecked) {
					filtro.portate?.add(Portata.ANTIPASTO)
				}
				if (primoToggleButton.isChecked) {
					filtro.portate?.add(Portata.PRIMO)
				}
				if (secondoToggleButton.isChecked) {
					filtro.portate?.add(Portata.SECONDO)
				}
				if (contornoToggleButton.isChecked) {
					filtro.portate?.add(Portata.CONTORNO)
				}
				if (dolceToggleButton.isChecked) {
					filtro.portate?.add(Portata.DOLCE)
				}

				filtro.tempoPreparazione = when (tempoPrepRadioGroup.checkedRadioButtonId) {
					R.id.cinqueMinRadioButton -> TempoPreparazione.CINQUE_MIN
					R.id.trentaMinRadioButton -> TempoPreparazione.TRENTA_MIN
					R.id.unaOraRadioButton -> TempoPreparazione.UN_ORA
					R.id.dueOreRadioButton -> TempoPreparazione.DUE_ORE
					R.id.quattroOreRadioButton -> TempoPreparazione.QUATTRO_ORE
					R.id.oltreRadioButton -> TempoPreparazione.ILLIMITATO_TEMPO
					else -> null
				}

				if (!(vegetarianoSwitch.isChecked && nonVegetarianoSwitch.isChecked)) {
					filtro.isVegetariana = vegetarianoSwitch.isChecked
				}

				if (!(serveCotturaSwitch.isChecked && nonServeCotturaSwitch.isChecked)) {
					filtro.serveCottura = serveCotturaSwitch.isChecked
				}

				ricetteListViewModel.getRicette(filtro)
			}

			dropFiltri.setOnClickListener {
				if (groupFiltri.visibility == VISIBLE) {
					groupFiltri.visibility = GONE
					dropFiltri.setImageIcon(Icon.createWithResource(context, R.drawable.ic_baseline_keyboard_arrow_down_24))
				} else {
					groupFiltri.visibility = VISIBLE
					dropFiltri.setImageIcon(Icon.createWithResource(context, R.drawable.ic_baseline_keyboard_arrow_up_24))
				}
			}
		}

		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				ricetteListViewModel.ricette.collect { ricette ->
					binding.ricetteRecyclerView.adapter = RicetteListAdapter(ricette) { ricettaID ->
						findNavController().navigate(RicetteListFragmentDirections.fromListaToDettaglio(ricettaID))
					}
					// Separatore tra gli item
					binding.ricetteRecyclerView.addItemDecoration(DividerItemDecoration(context, VERTICAL))
				}
			}
		}

		binding.aggiungiRicettaFAB.setOnClickListener {
			findNavController().navigate(R.id.from_lista_to_aggiungi)
		}
	}

	// VIEW BINDING
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}