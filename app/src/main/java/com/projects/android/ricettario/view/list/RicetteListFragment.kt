package com.projects.android.ricettario.view.list

import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
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
			cercaButton.setOnClickListener {
				val filtro = Filters()

				if (!cercaText.text.isNullOrBlank()) {
					filtro.string = cercaText.text.toString()
				}

				filtro.portate = mutableListOf()
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