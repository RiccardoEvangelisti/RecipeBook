package com.projects.android.ricettario.view.insert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.projects.android.ricettario.databinding.FragmentAggiungiRicettaBinding
import com.projects.android.ricettario.model.enums.Portata
import com.projects.android.ricettario.model.enums.TempoPreparazione
import kotlinx.coroutines.launch

class AggiungiRicettaFragment : Fragment() {

	// VIEW MODEL
	private val aggiungiRicettaViewModel: AggiungiRicettaViewModel by viewModels()

	// VIEW BINDING
	private var _binding: FragmentAggiungiRicettaBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentAggiungiRicettaBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {

			context?.let {
				ArrayAdapter(it, android.R.layout.simple_spinner_item, Portata.values()).also { adapter ->
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					portate.adapter = adapter
				}

				ArrayAdapter(it, android.R.layout.simple_spinner_item, TempoPreparazione.values()).also { adapter ->
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					binding.tempoPreparazione.adapter = adapter
				}
			}


			nome.doOnTextChanged { text, _, _, _ ->
				aggiungiRicettaViewModel.updateRicetta { oldRicetta ->
					oldRicetta.copy(nome = text.toString())
				}
			}

			preparazione.doOnTextChanged { text, _, _, _ ->
				aggiungiRicettaViewModel.updateRicetta { it.copy(preparazione = text.toString()) }
			}
		}

		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
				aggiungiRicettaViewModel.ricetta.collect { ricetta ->
					ricetta?.let {
						binding.apply {
							if (nome.text.toString() != ricetta.nome) { // previene un infinite-loop con il listener
								nome.setText(ricetta.nome)
							}
							if (preparazione.text.toString() != ricetta.preparazione) {
								preparazione.setText(ricetta.preparazione)
							}
						}
					}
				}
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}