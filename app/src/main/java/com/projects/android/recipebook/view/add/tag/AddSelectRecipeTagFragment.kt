package com.projects.android.recipebook.view.add.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.projects.android.recipebook.databinding.FragmentAddSelectRecipeTagBinding
import com.projects.android.recipebook.utils.PictureUtils
import kotlinx.coroutines.launch
import java.io.File

class AddSelectRecipeTagFragment : Fragment() {

	// VIEW BINDING
	private var _binding: FragmentAddSelectRecipeTagBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_binding = FragmentAddSelectRecipeTagBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)


	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}