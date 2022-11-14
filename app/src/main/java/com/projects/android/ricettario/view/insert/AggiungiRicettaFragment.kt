package com.projects.android.ricettario.view.insert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAggiungiRicettaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            activity?.onBackPressedDispatcher?.addCallback(
                viewLifecycleOwner,
                true
            ) {
                if (!nomeAdd.text.isNullOrBlank()) {
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(
                        context,
                        "ERRORE: Inserire un titolo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            context?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    Portata.values()
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    portataAdd.adapter = adapter
                }

                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    TempoPreparazione.values()
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    tempoPreparazioneAdd.adapter = adapter
                }

                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    UnitaDiMisura.values()
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    unitaIngredienteAdd.adapter = adapter
                }
            }

            quantitaIngredienteAdd.setOnEditorActionListener { _, actionId, _ ->
                return@setOnEditorActionListener when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        nomeIngredienteAdd.requestFocus()
                        true
                    }

                    else -> false
                }
            }

            nomeIngredienteAdd.setOnEditorActionListener { _, actionId, _ ->
                return@setOnEditorActionListener when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if (!quantitaIngredienteAdd.text.isNullOrBlank() && !nomeIngredienteAdd.text.isNullOrBlank()) {
                            val bindingIngredienti =
                                ItemLayoutIngredientiBinding.inflate(layoutInflater)
                            _bindingIngredientiList.add(bindingIngredienti)
                            bindingIngredienti.apply {
                                context?.let {
                                    ArrayAdapter(
                                        it,
                                        android.R.layout.simple_spinner_item,
                                        UnitaDiMisura.values()
                                    ).also { adapter ->
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        unitaIngredienteItemAdd.adapter = adapter
                                    }
                                }
                                cancellaIngredienteItemAdd.setOnClickListener {
                                    aggiungiRicettaViewModel.updateRicetta { ricetta ->
                                        ricetta.copy(ingredientiList = ricetta.ingredientiList.also {
                                            it.removeAt(ingredientiLayoutAdd.indexOfChild(root))
                                        })
                                    }
                                    ingredientiLayoutAdd.removeView(root)
                                }

                                quantitaIngredienteItemAdd.text = quantitaIngredienteAdd.text
                                unitaIngredienteItemAdd.setSelection(unitaIngredienteAdd.selectedItemPosition)
                                nomeIngredienteItemAdd.text = nomeIngredienteAdd.text

                                quantitaIngredienteAdd.text.clear()
                                unitaIngredienteAdd.setSelection(0)
                                nomeIngredienteAdd.text.clear()
                                quantitaIngredienteAdd.requestFocus()

                                ingredientiLayoutAdd.addView(root)
                                aggiungiRicettaViewModel.updateRicetta { ricetta ->
                                    ricetta.copy(ingredientiList = ricetta.ingredientiList.also {
                                        it.add(
                                            Ingrediente(
                                                nomeIngredienteAdd.text.toString(),
                                                quantitaIngredienteItemAdd.text.toString().toInt(),
                                                unitaIngredienteAdd.selectedItem as UnitaDiMisura
                                            )
                                        )
                                    })
                                }

                                quantitaIngredienteItemAdd.doOnTextChanged { text, _, _, _ ->
                                    aggiungiRicettaViewModel.updateRicetta { ricetta ->
                                        ricetta.copy(ingredientiList = ricetta.ingredientiList.also {
                                            it[ingredientiLayoutAdd.indexOfChild(root)].quantita =
                                                text.toString().toInt()
                                        })
                                    }
                                }

                                unitaIngredienteItemAdd.onItemSelectedListener =
                                    object : AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(
                                            parent: AdapterView<*>?,
                                            view: View,
                                            position: Int,
                                            id: Long
                                        ) {
                                            aggiungiRicettaViewModel.updateRicetta { ricetta ->
                                                ricetta.copy(ingredientiList = ricetta.ingredientiList.also {
                                                    it[ingredientiLayoutAdd.indexOfChild(root)].unitaDiMisura =
                                                        UnitaDiMisura.values()[position]
                                                })
                                            }
                                        }

                                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                                    }

                                nomeIngredienteItemAdd.doOnTextChanged { text, _, _, _ ->
                                    aggiungiRicettaViewModel.updateRicetta { ricetta ->
                                        ricetta.copy(ingredientiList = ricetta.ingredientiList.also {
                                            it[ingredientiLayoutAdd.indexOfChild(root)].nome =
                                                text.toString()
                                        })
                                    }
                                }
                            }
                        }
                        true
                    }

                    else -> false
                }
            }

            // Listeners che al cambiamento del dato sulla UI applicano la modifica al ViewModel (UI->ViewModel)
            nomeAdd.doOnTextChanged { text, _, _, _ ->
                aggiungiRicettaViewModel.updateRicetta {
                    it.copy(nome = text.toString())
                }
            }

            preparazioneAdd.doOnTextChanged { text, _, _, _ ->
                aggiungiRicettaViewModel.updateRicetta { it.copy(preparazione = text.toString()) }
            }

            portataAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (parent != null) {
                        aggiungiRicettaViewModel.updateRicetta {
                            it.copy(
                                portata = parent.adapter.getItem(
                                    position
                                ) as Portata
                            )
                        }
                    }
                }
            }

            vegetarianoAdd.setOnCheckedChangeListener { _, b ->
                aggiungiRicettaViewModel.updateRicetta { it.copy(isVegetariana = b) }
            }
            serveCotturaAdd.setOnCheckedChangeListener { _, b ->
                aggiungiRicettaViewModel.updateRicetta { it.copy(serveCottura = b) }
            }
        }

        // Colleziono lo StateFlow del ViewModel e con esso aggiorno la UI (ViewModel->UI)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                aggiungiRicettaViewModel.ricetta.collect { ricetta ->
                    ricetta?.let {
                        binding.apply {
                            if (nomeAdd.text.toString() != ricetta.nome) { // previene un infinite-loop con il listener
                                nomeAdd.setText(ricetta.nome)
                            }
                            vegetarianoAdd.isChecked = ricetta.isVegetariana
                            serveCotturaAdd.isChecked = ricetta.serveCottura
                            portataAdd.setSelection(ricetta.portata.ordinal)
                            tempoPreparazioneAdd.setSelection(ricetta.tempoPreparazione.ordinal)
                            if (porzioniAdd.text.toString().toInt() != ricetta.porzioni) {
                                porzioniAdd.setText(ricetta.porzioni)
                            }
                            if (preparazioneAdd.text.toString() != ricetta.preparazione) {
                                preparazioneAdd.setText(ricetta.preparazione)
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