package com.example.caniwatchitapplication.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.databinding.FragmentStreamingSourcesBinding
import com.example.caniwatchitapplication.ui.adapter.StreamingSourcesAdapter
import com.example.caniwatchitapplication.ui.adapter.SubscribedStreamingSourcesAdapter
import com.example.caniwatchitapplication.ui.view.MainActivity
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.util.Constants.Companion.MAX_STREAMING_SOURCE_LOGO_PX_SIZE
import com.example.caniwatchitapplication.util.Resource
import com.google.android.material.snackbar.Snackbar

class StreamingSourcesFragment : Fragment(R.layout.fragment_streaming_sources)
{
    private var _binding: FragmentStreamingSourcesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel
    private lateinit var subscribedStreamingSourcesAdapter: SubscribedStreamingSourcesAdapter
    private lateinit var availableStreamingSourcesAdapter: StreamingSourcesAdapter
    
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
        viewModel = (activity as MainActivity).appViewModel
        setupAdapters()
        
        viewModel.availableStreamingSources.observe(viewLifecycleOwner) { response ->
            when (response)
            {
                is Resource.Loading ->
                {
                    showProgressBar()
                }
                
                is Resource.Success ->
                {
                    response.data?.let {
                        hideProgressBar()
                        availableStreamingSourcesAdapter.submitList(it)
                    }
                }
                
                is Resource.Error ->
                {
                    response.message?.let {
                        hideProgressBar()
                        Snackbar.make(
                            binding.root,
                            "Se ha producido un error: $it",
                            Snackbar.LENGTH_LONG
                        ).apply {
                            setAnchorView(R.id.bottomNavigationView)
                        }.show()
                    }
                }
            }
        }
        
        availableStreamingSourcesAdapter.setupItemOnClickListener { service, isChecked ->
            
            if (isChecked)
            {
                viewModel.upsertSubscribedStreamingSource(service)
            } else
            {
                viewModel.deleteSubscribedStreamingSource(service)
            }
        }
        
        viewModel.getAllSubscribedStreamingSources().observe(viewLifecycleOwner) {
            
            subscribedStreamingSourcesAdapter.submitList(it)
        }
    }
    
    private fun setupAdapters()
    {
        subscribedStreamingSourcesAdapter = SubscribedStreamingSourcesAdapter()
        availableStreamingSourcesAdapter = StreamingSourcesAdapter(
            MAX_STREAMING_SOURCE_LOGO_PX_SIZE,
            viewModel.getAllSubscribedStreamingSources(),
            viewLifecycleOwner,
            false
        )
        
        binding.streamingSourcesDisplayer.rvSubscribedStreamingSources.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = subscribedStreamingSourcesAdapter
        }
        
        binding.rvAvailableStreamingSources.apply {
            layoutManager = GridLayoutManager(activity, 4)
            adapter = availableStreamingSourcesAdapter
            setHasFixedSize(true)
        }
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