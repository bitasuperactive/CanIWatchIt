package com.example.caniwatchitapplication.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caniwatchitapplication.BuildConfig
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.databinding.FragmentStreamingSourcesBinding
import com.example.caniwatchitapplication.ui.adapter.StreamingSourcesAdapter
import com.example.caniwatchitapplication.ui.adapter.SubscribedStreamingSourcesAdapter
import com.example.caniwatchitapplication.ui.view.MainActivity
import com.example.caniwatchitapplication.ui.viewmodel.StreamingSourcesViewModel
import com.example.caniwatchitapplication.util.Resource
import com.example.caniwatchitapplication.util.Transformations
import com.google.android.material.snackbar.Snackbar

/**
 * Fragmento secundario en el que usuario puede suscribirse a las plataformas de streaming
 * que tenga contratadas para filtrar la búsqueda de los títulos.
 *
 * @see StreamingSourcesViewModel
 * @see StreamingSourcesAdapter
 * @see SubscribedStreamingSourcesAdapter
 */
class StreamingSourcesFragment : Fragment(R.layout.fragment_streaming_sources)
{
    private var _binding: FragmentStreamingSourcesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StreamingSourcesViewModel
    private lateinit var availableStreamingSourcesAdapter: StreamingSourcesAdapter
    private lateinit var subscribedStreamingSourcesAdapter: SubscribedStreamingSourcesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FragmentStreamingSourcesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        viewModel = mainActivity.streamingSourcesViewModel

        setupSavedStateObservers()
        setupAdapters()
        setupAvailableSourcesObserver()
        setupSubscribedSourcesObserver()
        setupSourceOnClickListener()
        setupInformationOnClickListener()
    }

    /**
     * Restablece el estado previo de la interfaz.
     */
    private fun setupSavedStateObservers()
    {
        binding.tvBuildVersionName.text = getString(R.string.app_version_name, BuildConfig.VERSION_NAME)
        viewModel.informationVisibility.observe(this.viewLifecycleOwner) {
            binding.tvInformation.visibility = it
        }
    }

    private fun setupAdapters()
    {
        availableStreamingSourcesAdapter = StreamingSourcesAdapter(
            viewModel.subscribedStreamingSources,
            viewLifecycleOwner
        )
        subscribedStreamingSourcesAdapter = SubscribedStreamingSourcesAdapter()

        binding.rvAvailableStreamingSources.apply {
            layoutManager = GridLayoutManager(activity, 4)
            adapter = availableStreamingSourcesAdapter
            setHasFixedSize(true)
        }

        binding.streamingSourcesDisplayer.rvStreamingSources.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = subscribedStreamingSourcesAdapter
        }
    }

    /**
     * Actualiza la interfaz acorde con el estado de la petición de plataformas disponibles.
     *
     * _Los errores simplemente se muestran en un SnackBar._
     */
    private fun setupAvailableSourcesObserver()
    {
        viewModel.availableStreamingSources.observe(viewLifecycleOwner) { resource ->

            when (resource) {
                is Resource.Loading -> {
                    availableStreamingSourcesAdapter.submitList(emptyList())
                    showProgressBar()
                }

                is Resource.Success -> {
                    resource.data?.let {
                        hideProgressBar()
                        availableStreamingSourcesAdapter.submitList(it)
                    }
                }

                is Resource.Error -> {
                    hideHint()
                    binding.tvUnableToFetchSources.visibility = View.VISIBLE
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
     * Actualiza el adaptador de las plataformas suscritas por el usuario.
     */
    private fun setupSubscribedSourcesObserver()
    {
        viewModel.subscribedStreamingSources.observe(viewLifecycleOwner) {

            subscribedStreamingSourcesAdapter.submitList(it)
        }
    }

    /**
     * Al hacer clic en una plataforma disponible, el usuario se suscribe a la misma y esta se
     * almacena en la base de datos Room.
     *
     * @see StreamingSourcesViewModel.upsertSubscribedStreamingSource
     */
    private fun setupSourceOnClickListener()
    {
        availableStreamingSourcesAdapter.setupOnItemClickListener { source, isChecked ->

            if (isChecked) {
                viewModel.upsertSubscribedStreamingSource(source)
            } else {
                viewModel.deleteSubscribedStreamingSource(source)
            }
        }
    }

    /**
     * Al hacer clic en el TextView informativo, este se oculta.
     */
    private fun setupInformationOnClickListener()
    {
        binding.tvInformation.setOnClickListener {
            viewModel.setInformationVisibility(View.GONE)
        }
    }

    private fun hideHint()
    {
        binding.tvInformation.visibility = View.GONE
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