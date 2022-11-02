package com.projects.android.ricettario.view.detail

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.projects.android.ricettario.R
import com.projects.android.ricettario.databinding.FragmentRicettaSingolaBinding
import kotlinx.coroutines.launch

class RicettaSingolaFragment : Fragment() {

	// SAFE ARGS
	private val args: RicettaSingolaFragmentArgs by navArgs()

	// VIEW MODEL
	private val ricettaSingolaViewModel: RicettaSingolaViewModel by viewModels {
		RicettaSingolaViewModelFactory(args.ricettaID)
	}

	// VIEW BINDING
	private var _binding: FragmentRicettaSingolaBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentRicettaSingolaBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// APPBAR: MENU
		setupMenu()

		// Custom back navigation
		activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, true) {
			if (binding.nome.text.isNotBlank()) {
				findNavController().navigateUp()
			} else {
				Toast.makeText(context, "ERRORE: Inserire un Nome Ricetta", Toast.LENGTH_SHORT).show()
			}
		}

		binding.apply {
			nome.doOnTextChanged { text, _, _, _ ->
				ricettaSingolaViewModel.updateRicetta { oldRicetta ->
					oldRicetta.copy(nome = text.toString())
				}
			}

			preparazione.doOnTextChanged { text, _, _, _ ->
				ricettaSingolaViewModel.updateRicetta { it.copy(preparazione = text.toString()) }
			}
		}

		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
				ricettaSingolaViewModel.ricetta.collect { ricetta ->
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

	// APPBAR: MENU
	private fun setupMenu() {
		(requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
			override fun onPrepareMenu(menu: Menu) {
				// Handle for example visibility of menu items
			}

			override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
				menuInflater.inflate(R.menu.ricetta_singola_fragment_menu, menu)
			}

			override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
				ricettaSingolaViewModel.deleteRicetta()
				return true;
			}
		}, viewLifecycleOwner, Lifecycle.State.RESUMED)
	}
}