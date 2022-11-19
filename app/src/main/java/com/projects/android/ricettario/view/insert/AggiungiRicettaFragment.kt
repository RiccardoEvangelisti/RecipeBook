package com.projects.android.ricettario.view.insert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.projects.android.ricettario.databinding.FragmentAggiungiRicettaBinding
import com.projects.android.ricettario.databinding.ItemLayoutIngredientiBinding
import com.projects.android.ricettario.model.Ingrediente
import com.projects.android.ricettario.model.enums.Portata
import com.projects.android.ricettario.model.enums.TempoPreparazione
import com.projects.android.ricettario.model.enums.UnitaDiMisura
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

	private var _bindingIngredientiList = mutableListOf<ItemLayoutIngredientiBinding?>()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentAggiungiRicettaBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {

			// Condizioni per navigateUp e quindi salvataggio della ricetta
			activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, true) {
				val check = aggiungiRicettaViewModel.checkRicetta()
				if (!check.isNullOrBlank()) {
					Toast.makeText(context, "ERRORE: $check", Toast.LENGTH_SHORT).show()
				} else {
					findNavController().navigateUp()
				}
			}

			requireContext().let {
				// Inizializzazione spinner Portata
				ArrayAdapter(it, android.R.layout.simple_spinner_item, Portata.values()).also { adapter ->
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					portataAdd.adapter = adapter
				}
				// Inizializzazione spinner TempoPreparazione
				ArrayAdapter(it, android.R.layout.simple_spinner_item, TempoPreparazione.values()).also { adapter ->
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					tempoPreparazioneAdd.adapter = adapter
				}
				// Inizializzazione spinner UnitaIngrediente
				ArrayAdapter(it, android.R.layout.simple_spinner_item, UnitaDiMisura.values()).also { adapter ->
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					unitaIngredienteAdd.adapter = adapter
				}
			}

			// Listeners UI->ViewModel (applicano la modifica al ViewModel al cambiamento del dato sulla UI)
			nomeAdd.doOnTextChanged { text, _, _, _ ->
				aggiungiRicettaViewModel.updateRicetta { it.nome = text.toString() }
			}

			vegetarianoAdd.setOnCheckedChangeListener { _, b ->
				aggiungiRicettaViewModel.updateRicetta { it.isVegetariana = b }
			}
			serveCotturaAdd.setOnCheckedChangeListener { _, b ->
				aggiungiRicettaViewModel.updateRicetta { it.serveCottura = b }
			}

			portataAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
				override fun onNothingSelected(parent: AdapterView<*>?) {
				}

				override fun onItemSelected(
					parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
					parent!!.let {
						aggiungiRicettaViewModel.updateRicetta {
							it.portata = parent.adapter.getItem(position) as Portata
						}
					}
				}
			}

			tempoPreparazioneAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
				override fun onNothingSelected(parent: AdapterView<*>?) {
				}

				override fun onItemSelected(
					parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
					parent!!.let {
						aggiungiRicettaViewModel.updateRicetta {
							it.tempoPreparazione = parent.adapter.getItem(position) as TempoPreparazione
						}
					}
				}
			}

			porzioniAdd.doOnTextChanged { text, _, _, _ ->
				aggiungiRicettaViewModel.updateRicetta {
					it.porzioni = text.toString()
				}
			}

			unitaIngredienteAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
				override fun onItemSelected(
					parent: AdapterView<*>?, view: View, position: Int, id: Long) {
					// Gestione UnitaDiMisura.QUANTOBASTA
					if (position == UnitaDiMisura.QUANTOBASTA.ordinal) {
						quantitaIngredienteAdd.setText("")
						quantitaIngredienteAdd.visibility = GONE
					} else {
						quantitaIngredienteAdd.visibility = VISIBLE
					}
					nomeIngredienteAdd.requestFocus()
				}

				override fun onNothingSelected(parent: AdapterView<*>?) {}
			}

			nomeIngredienteAdd.setOnEditorActionListener { _, actionId, _ ->
				return@setOnEditorActionListener when (actionId) {
					EditorInfo.IME_ACTION_DONE -> {
						// Se nomeIngredienteAdd e' consistente e (quantitaIngredienteAdd e' consistente oppure unitaIngrediente==QUANTOBASTA)
						if ((!quantitaIngredienteAdd.text.isNullOrBlank() || unitaIngredienteAdd.selectedItemPosition == UnitaDiMisura.QUANTOBASTA.ordinal) && !nomeIngredienteAdd.text.isNullOrBlank()) {
							// Si genera il binding
							val bindingIngredienti = ItemLayoutIngredientiBinding.inflate(layoutInflater)
							_bindingIngredientiList.add(bindingIngredienti)
							bindingIngredienti.apply {
								// Inizializzione spinner UnitaDiMisura
								requireContext().let {
									ArrayAdapter(it,
									             android.R.layout.simple_spinner_item,
									             UnitaDiMisura.values()).also { adapter ->
										adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
										unitaIngredienteItemAdd.adapter = adapter
									}
								}

								// Riempimento UI nuovo ingrediente
								quantitaIngredienteItemAdd.text = quantitaIngredienteAdd.text
								unitaIngredienteItemAdd.setSelection(unitaIngredienteAdd.selectedItemPosition)
								if (unitaIngredienteItemAdd.selectedItemPosition == UnitaDiMisura.QUANTOBASTA.ordinal) {
									quantitaIngredienteItemAdd.visibility = GONE
								}
								nomeIngredienteItemAdd.text = nomeIngredienteAdd.text

								// Cleanup
								quantitaIngredienteAdd.text.clear()
								unitaIngredienteAdd.setSelection(0)
								unitaIngredienteAdd.visibility = VISIBLE
								nomeIngredienteAdd.text.clear()

								// Si aggiunge la view al linearLayout
								ingredientiLayoutAdd.addView(root)

								aggiungiRicettaViewModel.updateRicetta { stato ->
									stato.ingredientiList = stato.ingredientiList.also { list ->
										list!!.add(Ingrediente(nomeIngredienteItemAdd.text.toString(),
										                       quantitaIngredienteItemAdd.text.toString(),
										                       unitaIngredienteItemAdd.selectedItem as UnitaDiMisura))
									}
								}

								// Listeners UI->ViewModel (applicano la modifica al ViewModel al cambiamento del dato sulla UI)
								quantitaIngredienteItemAdd.doOnTextChanged { text, _, _, _ ->
									aggiungiRicettaViewModel.updateRicetta { state ->
										state.ingredientiList = state.ingredientiList.also { list ->
											list!![ingredientiLayoutAdd.indexOfChild(root)].quantita = text.toString()
										}
									}
								}

								unitaIngredienteItemAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
									override fun onItemSelected(
										parent: AdapterView<*>?, view: View, position: Int, id: Long) {
										if (position == UnitaDiMisura.QUANTOBASTA.ordinal) {
											quantitaIngredienteItemAdd.setText("")
											quantitaIngredienteItemAdd.visibility = INVISIBLE
										} else {
											quantitaIngredienteItemAdd.visibility = VISIBLE
										}
										aggiungiRicettaViewModel.updateRicetta { stato ->
											stato.ingredientiList = stato.ingredientiList.also { list ->
												list!![ingredientiLayoutAdd.indexOfChild(root)].unitaDiMisura =
													UnitaDiMisura.values()[position]
											}
										}
									}

									override fun onNothingSelected(parent: AdapterView<*>?) {}
								}

								nomeIngredienteItemAdd.doOnTextChanged { text, _, _, _ ->
									aggiungiRicettaViewModel.updateRicetta { stato ->
										stato.ingredientiList = stato.ingredientiList.also { list ->
											list!![ingredientiLayoutAdd.indexOfChild(root)].nome = text.toString()
										}
									}
								}

								cancellaIngredienteItemAdd.setOnClickListener {
									aggiungiRicettaViewModel.updateRicetta { stato ->
										stato.ingredientiList = stato.ingredientiList.also { list ->
											list!!.removeAt(ingredientiLayoutAdd.indexOfChild(root))
										}
									}
									ingredientiLayoutAdd.removeView(root)
								}
							}
						}
						true
					}

					else -> false
				}
			}

			preparazioneAdd.doOnTextChanged { text, _, _, _ ->
				aggiungiRicettaViewModel.updateRicetta {
					it.preparazione = text.toString()
				}
			}
		}

		// Colleziono lo StateFlow del ViewModel e con esso aggiorno la UI (ViewModel->UI)
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
				aggiungiRicettaViewModel.state.collect { ricetta ->
					ricetta?.let {
						binding.apply {
							ricetta.nome?.let {
								if (nomeAdd.text.toString() != ricetta.nome) { // previene un infinite-loop con il listener
									nomeAdd.setText(ricetta.nome)
								}
							}
							ricetta.isVegetariana?.let {
								vegetarianoAdd.isChecked = ricetta.isVegetariana!!
							}
							ricetta.serveCottura?.let {
								serveCotturaAdd.isChecked = ricetta.serveCottura!!
							}
							ricetta.portata?.let {
								portataAdd.setSelection(ricetta.portata!!.ordinal)
							}
							ricetta.tempoPreparazione?.let {
								tempoPreparazioneAdd.setSelection(ricetta.tempoPreparazione!!.ordinal)
							}
							ricetta.porzioni?.let {
								if (porzioniAdd.text.toString() != ricetta.porzioni.toString()) {
									porzioniAdd.setText(ricetta.porzioni.toString())
								}
							}
							ricetta.preparazione?.let {
								if (preparazioneAdd.text.toString() != ricetta.preparazione) {
									preparazioneAdd.setText(ricetta.preparazione)
								}
							}
							ricetta.ingredientiList?.let {
								for (i in _bindingIngredientiList.indices) {
									_bindingIngredientiList[i]!!.apply {
										val ingrediente = ricetta.ingredientiList!![i]
										if (nomeIngredienteItemAdd.text.toString() != ingrediente.nome) {
											nomeIngredienteItemAdd.setText(ingrediente.nome)
										}
										if (quantitaIngredienteItemAdd.text.toString() != ingrediente.quantita) {
											quantitaIngredienteItemAdd.setText(ingrediente.quantita)
										}
										if (unitaIngredienteItemAdd.selectedItemPosition != ingrediente.unitaDiMisura.ordinal) {
											unitaIngredienteItemAdd.setSelection(ingrediente.unitaDiMisura.ordinal)
										}
									}
								}
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
		for (i in _bindingIngredientiList.indices) {
			_bindingIngredientiList[i] = null
		}
	}
}