package com.example.caniwatchitapplication.ui.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.databinding.FragmentSearchBinding
import com.example.caniwatchitapplication.ui.adapter.SubscribedStreamingSourcesAdapter
import com.example.caniwatchitapplication.ui.adapter.TitlesAdapter
import com.example.caniwatchitapplication.ui.view.MainActivity
import com.example.caniwatchitapplication.ui.viewmodel.SearchViewModel
import com.example.caniwatchitapplication.ui.viewmodel.StreamingSourcesViewModel
import com.example.caniwatchitapplication.util.Resource
import com.example.caniwatchitapplication.util.Transformations
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Fragmento principal en el que el usuario puede buscar los títulos deseados a visualizar.
 *
 * @see SearchViewModel
 * @see StreamingSourcesViewModel
 * @see TitlesAdapter
 * @see SubscribedStreamingSourcesAdapter
 */
class SearchFragment : Fragment(R.layout.fragment_search)
{
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private lateinit var viewModel: SearchViewModel
    private lateinit var streamingSourcesViewModel: StreamingSourcesViewModel
    private lateinit var subscribedStreamingSourcesAdapter: SubscribedStreamingSourcesAdapter
    private lateinit var titlesAdapter: TitlesAdapter
    private var loadingBrainJob: Job? = null

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

        mainActivity = activity as MainActivity
        viewModel = mainActivity.searchViewModel
        streamingSourcesViewModel = mainActivity.streamingSourcesViewModel

        setupLoadingBrainGif()

        setupSavedStateObservers()
        setupAdapters()
        setupSubscribedSourcesObserver()
        setupSearchHintButton()
        setupSearchListener()
        setupSearchedTitlesObserver()
        setupTitleOnClickListener()
    }

    private fun setupLoadingBrainGif()
    {
        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gif_loading_brain)
            .into(binding.ivLoadingBrain)

        binding.ivLoadingBrain.setOnClickListener {

            binding.etTitleToSearch.requestFocus()
            showKeyboard()
        }
    }

    /**
     * Restablece el estado previo de la interfaz.
     */
    private fun setupSavedStateObservers()
    {
        viewModel.hintVisibility.observe(viewLifecycleOwner) {
            binding.tvHintToSearch.visibility = it
        }
    }

    private fun setupAdapters()
    {
        subscribedStreamingSourcesAdapter = SubscribedStreamingSourcesAdapter()
        titlesAdapter = TitlesAdapter(
            this.requireView(),
            streamingSourcesViewModel,
            viewLifecycleOwner
        )

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
     * Actualiza el adaptador de las plataformas suscritas por el usuario.
     */
    private fun setupSubscribedSourcesObserver()
    {
        streamingSourcesViewModel.subscribedStreamingSources.observe(viewLifecycleOwner) {

            subscribedStreamingSourcesAdapter.submitList(it)
        }
    }

    /**
     * Abre el teclado virtual al pulsar la pista de búsqueda.
     */
    private fun setupSearchHintButton()
    {
        binding.tvHintToSearch.setOnClickListener {

            hideLodiangBrainGif()
            binding.etTitleToSearch.requestFocus()
            showKeyboard()
        }
    }

    /**
     * Actualiza el valor de búsqueda si este no es blanco.
     */
    private fun setupSearchListener()
    {
        binding.etTitleToSearch.addTextChangedListener { editable ->
            editable?.let {
                val searchValue = it.toString()

                // Must update even if it is blank
                viewModel.setQuery(searchValue)

                if (searchValue.isBlank()) {
                    // Clear adapter
                    titlesAdapter.submitList(emptyList())
                    hideNoTitlesFound()
                    showLodiangBrainGif()
                }
            }
        }
    }

    /**
     * Actualiza la interfaz acorde con el estado de la búsqueda realizada por el usuario.
     *
     * _Los errores simplemente se muestran en un SnackBar._
     */
    private fun setupSearchedTitlesObserver()
    {
        viewModel.searchedTitles.observe(viewLifecycleOwner) { resource ->

            when (resource) {
                is Resource.Loading -> {
                    // Clear adapter
                    titlesAdapter.submitList(emptyList())
                    hideHint()
                    hideLodiangBrainGif()
                    showProgressBar()
                }

                is Resource.Success -> {
                    resource.data?.let { titleDetailsList ->

                        hideProgressBar()

                        if (searchValueIsNotBlank() && titleDetailsList.isNotEmpty()) {
                            hideLodiangBrainGif()
                            // Only populate the adapter if there is a search
                            titlesAdapter.submitList(titleDetailsList)
                        } else {
                            // Clear adapter
                            titlesAdapter.submitList(emptyList())
                        }

                        if (titleDetailsList.isNotEmpty()) {
                            viewModel.addQueryCount()
                            hideNoTitlesFound()
                        } else {
                            showNoTitlesFound()
                        }
                    }
                }

                is Resource.Error -> {
                    binding.tvNoTitlesFound.visibility = View.VISIBLE
                    resource.message?.let {
                        hideProgressBar()
                        Snackbar.make(
                            binding.root,
                            getString(
                                R.string.unknown_error,
                                Transformations.detailWatchmodeErrorMsg(it)
                            ),
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
     * Navega al fragmento del título clicado.
     *
     * @see TitleFragment
     */
    private fun setupTitleOnClickListener()
    {
        titlesAdapter.setupOnItemClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToTitleFragment(it)
            findNavController().navigate(action)
        }
    }

    /**
     * Muestra implícitamente el teclado virtual.
     */
    private fun showKeyboard()
    {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etTitleToSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideHint()
    {
        viewModel.setHintVisibility(View.INVISIBLE)
    }

    private fun searchValueIsNotBlank() = binding.etTitleToSearch.text.isNotBlank()

    private fun showLodiangBrainGif()
    {
        loadingBrainJob = MainScope().launch {
            delay(12000)
            hideHint()
            binding.ivLoadingBrain.visibility = View.VISIBLE
        }
    }

    private fun hideLodiangBrainGif()
    {
        loadingBrainJob?.cancel()
        binding.ivLoadingBrain.visibility = View.INVISIBLE
    }

    private fun showNoTitlesFound() {
        binding.tvNoTitlesFound.visibility = View.VISIBLE
    }

    private fun hideNoTitlesFound() {
        binding.tvNoTitlesFound.visibility = View.INVISIBLE
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