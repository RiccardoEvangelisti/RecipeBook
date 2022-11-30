package com.projects.android.recipebook.view.single

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.projects.android.recipebook.R
import com.projects.android.recipebook.databinding.FragmentSingleRecipeBinding
import com.projects.android.recipebook.utils.PictureUtils.Companion.getScaledBitmap
import com.projects.android.recipebook.view.add.tag.TagSpan
import kotlinx.coroutines.launch
import java.io.File

class SingleRecipeFragment : Fragment() {

	// SAFE ARGS
	private val args: SingleRecipeFragmentArgs by navArgs()

	// VIEW MODEL
	private val singleRecipeViewModel: SingleRecipeViewModel by viewModels {
		SingleRecipeViewModelFactory(args.recipeID)
	}

	// VIEW BINDING
	private var _binding: FragmentSingleRecipeBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_binding = FragmentSingleRecipeBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// APPBAR: MENU
		setupMenu()

		// Custom back navigation
		activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, true) {
			if (binding.nameSingle.text.isNotBlank()) {
				findNavController().navigateUp()
			} else {
				Toast.makeText(context, "ERRORE: Inserire un Nome Recipe", Toast.LENGTH_SHORT).show()
			}
		}

		binding.apply {
			nameSingle.doOnTextChanged { text, _, _, _ ->
				singleRecipeViewModel.updateRecipe {
					it.recipe?.name = text.toString()
				}
			}

			preparationSingle.doOnTextChanged { _, _, _, _ ->
				//recipeSingolaViewModel.updateRecipe { it.copy(preparation = text.toString()) } //TODO
			}
		}

		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
				singleRecipeViewModel.state.collect { state ->
					state?.apply {
						recipe?.let {
							binding.apply {
								if (nameSingle.text.toString() != recipe!!.name) { // previene un infinite-loop con il listener
									nameSingle.setText(recipe!!.name)
								}

								preparationSingle.setText(recipe!!.preparation.text) // set the text with all "#"
								var startTag = -1
								for ((i, name) in tagNames!!.withIndex()) { // for every "#"
									startTag = preparationSingle.text.indexOf("#".first(), startTag + 1, true) // take the index of first "#"

									val spannableText: Spannable = SpannableString("#$name")
									spannableText.setSpan(
										TagSpan(recipe!!.preparation.tags[i]), 0, spannableText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
									) // create a span with id and name of tag
									preparationSingle.movementMethod = LinkMovementMethod.getInstance()
									preparationSingle.text.replace(startTag, startTag + 1, spannableText) // replace old "#"
								}

								if (photoSingle.tag != recipe!!.photoFileName) { // Update photo only when the name is different
									val photoFile = recipe!!.photoFileName?.let {
										File(requireContext().applicationContext.filesDir, it)
									}
									if (photoFile?.exists() == true) {
										photoSingle.doOnLayout { measuredView ->
											val scaledBitmap = getScaledBitmap(
												requireContext(), photoFile.path, measuredView.width, measuredView.height
											)
											photoSingle.setImageBitmap(scaledBitmap)
											photoSingle.tag = recipe!!.photoFileName
										}
									} else {
										photoSingle.setImageBitmap(null)
										photoSingle.tag = null
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
	}

	// APPBAR: MENU
	private fun setupMenu() {
		(requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
			override fun onPrepareMenu(menu: Menu) {
				// Handle for example visibility of menu items
			}

			override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
				menuInflater.inflate(R.menu.fragment_menu_single_recipe, menu)
			}

			override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
				return when (menuItem.itemId) {
					R.id.delete_recipe -> {
						viewLifecycleOwner.lifecycleScope.launch {
							singleRecipeViewModel.deleteRecipe()
						}
						findNavController().navigateUp()
						true
					}

					else -> false
				}
			}
		}, viewLifecycleOwner, Lifecycle.State.RESUMED)
	}
}