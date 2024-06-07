package com.example.caniwatchitapplication.ui.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.databinding.FragmentSearchBinding
import com.example.caniwatchitapplication.ui.adapter.SubscribedStreamingSourcesAdapter
import com.example.caniwatchitapplication.ui.adapter.TitlesAdapter
import com.example.caniwatchitapplication.ui.view.MainActivity
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.util.Constants.Companion.SEARCH_FOR_TITLES_DELAY
import com.example.caniwatchitapplication.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Fragmento principal en el que el usuario puede buscar los títulos deseados a visualizar.
 *
 * @see TitlesAdapter
 * @see SubscribedStreamingSourcesAdapter
 */
class SearchFragment : Fragment(R.layout.fragment_search)
{
    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel
    private lateinit var titlesAdapter: TitlesAdapter
    private lateinit var subscribedStreamingSourcesAdapter: SubscribedStreamingSourcesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).appViewModel

        setupAdapters()
        setupSearchHintButton()
        setupSearchEraseButton(view)
        setupSearchListener()
        setupTitleOnClickListener()
        setupSubscribedSourcesObserver()
        setupSearchedTitlesObserver()
    }

    private fun setupAdapters()
    {
        titlesAdapter = TitlesAdapter(this.requireView(), viewModel, viewLifecycleOwner)
        subscribedStreamingSourcesAdapter = SubscribedStreamingSourcesAdapter()

        binding.rvSearchedTitles.apply {
            this.layoutManager = LinearLayoutManager(activity)
            this.adapter = titlesAdapter
        }

        binding.streamingSourcesDisplayer.rvStreamingSources.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = subscribedStreamingSourcesAdapter
        }
    }

    /**
     * Abre el teclado virtual al pulsar la pista de búsqueda.
     */
    private fun setupSearchHintButton()
    {
        binding.tvHintToSearch.setOnClickListener {

            binding.etTitleToSearch.requestFocus()
            showKeyboard()
        }
    }

    /**
     * Permite borrar el texto introducido en el campo de búsqueda pulsando la cruz del límite
     * derecho.
     */
    // TODO - COPY-PASTED
    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchEraseButton(view: View)
    {
        binding.etTitleToSearch.setOnTouchListener { _, event ->
            view.performClick()

            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd =
                    binding.etTitleToSearch.compoundDrawablesRelative[2] // 2 representa el índice de la imagen a la derecha
                if (drawableEnd != null
                    // Se añaden 36 píxeles al tamaño del botón virtual para facilitar la pulsación
                    && event.rawX >= binding.etTitleToSearch.right - (drawableEnd.bounds.width() + 36)) {
                    binding.etTitleToSearch.text = null
                    showKeyboard()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    /**
     * Al cambiar la búsqueda del título, tras un delay predefinido, la ejecuta.
     *
     * @see SEARCH_FOR_TITLES_DELAY
     */
    private fun setupSearchListener()
    {
        var searchJob: Job? = null
        binding.etTitleToSearch.addTextChangedListener {
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(SEARCH_FOR_TITLES_DELAY)
                it?.let {
                    val searchValue = it.toString()
                    if (searchValue.isNotBlank()) {
                        hideSearchHint()
                        viewModel.searchForTitles(it.toString())
                    } else {
                        titlesAdapter.submitList(emptyList())
                        binding.tvNoTitlesFound.visibility = View.INVISIBLE
                        showSearchHint()
                    }
                }
            }
        }
    }

    /**
     * Al hacer click en un título navega al fragmento correspondiente.
     *
     * @see TitleFragment
     */
    private fun setupTitleOnClickListener()
    {
        titlesAdapter.setupOnItemClickListener {
            val bundle = bundleOf("titleIds" to it)

            findNavController().navigate(
                R.id.action_searchFragment_to_titleFragment,
                bundle
            )
        }
    }

    /**
     * Actualiza el adaptador de las plataformas suscritas por el usuario.
     */
    private fun setupSubscribedSourcesObserver()
    {
        viewModel.subscribedStreamingSources.observe(viewLifecycleOwner) {

            subscribedStreamingSourcesAdapter.submitList(it)
        }
    }

    /**
     * Actualiza la interfaz acorde con el estado de la búsqueda realizada por el usuario.
     *
     * _Los errores simplemente se muestran en un SnackBar._
     *
     * @see Resource
     */
    private fun setupSearchedTitlesObserver()
    {
        viewModel.searchedTitles.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Success -> {
                    resource.data?.let { titleDetailsList ->
                        hideProgressBar()
                        titlesAdapter.submitList(titleDetailsList)
                        binding.tvNoTitlesFound.visibility =
                            if (titleDetailsList.isEmpty()) View.VISIBLE else View.INVISIBLE
                    }
                }

                is Resource.Error -> {
                    resource.message?.let {
                        hideProgressBar()
                        Snackbar.make(
                            binding.root,
                            getString(R.string.unknown_error, it),
                            Snackbar.LENGTH_LONG
                        ).apply {
                            setAnchorView(R.id.bottomNavigationView)
                        }.show()
                    }
                }
            }
        }
    }

    /**
     * Muestra el teclado virtual.
     */
    // TODO - COPY-PASTED
    private fun showKeyboard()
    {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etTitleToSearch, InputMethodManager.SHOW_IMPLICIT)
    }
    
    private fun hideSearchHint()
    {
        binding.tvHintToSearch.visibility = View.INVISIBLE
    }
    
    private fun showSearchHint()
    {
        binding.tvHintToSearch.visibility = View.VISIBLE
    }
    
    private fun hideProgressBar()
    {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }
    
    private fun showProgressBar()
    {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }
}