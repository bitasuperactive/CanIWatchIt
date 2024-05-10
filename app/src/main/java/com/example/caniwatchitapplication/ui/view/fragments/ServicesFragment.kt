package com.example.caniwatchitapplication.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.databinding.FragmentServicesBinding
import com.example.caniwatchitapplication.ui.adapter.ServicesAdapter
import com.example.caniwatchitapplication.ui.adapter.SubscribedServicesAdapter
import com.example.caniwatchitapplication.ui.view.MainActivity
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.util.Constants.Companion.MAX_SERVICE_LOGO_PX_SIZE
import com.example.caniwatchitapplication.util.Resource
import com.google.android.material.snackbar.Snackbar

class ServicesFragment : Fragment(R.layout.fragment_services)
{
    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel
    private lateinit var subscribedServicesAdapter: SubscribedServicesAdapter
    private lateinit var availableServicesAdapter: ServicesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).appViewModel
        setupAdapters()
        
        viewModel.availableServices.observe(viewLifecycleOwner) { response ->
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
                        availableServicesAdapter.submitList(it)
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
        
        availableServicesAdapter.setupItemOnClickListener { service, isChecked ->
            
            if (isChecked)
            {
                viewModel.upsertSubscribedService(service)
            } else
            {
                viewModel.deleteSubscribedService(service)
            }
        }
        
        viewModel.getAllSubscribedServices().observe(viewLifecycleOwner) {
            
            subscribedServicesAdapter.submitList(it)
        }
    }
    
    private fun setupAdapters()
    {
        subscribedServicesAdapter = SubscribedServicesAdapter()
        availableServicesAdapter = ServicesAdapter(
            MAX_SERVICE_LOGO_PX_SIZE,
            viewModel.getAllSubscribedServices(),
            viewLifecycleOwner,
            false
        )
        
        binding.servicesDisplayer.rvSubscribedServices.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = subscribedServicesAdapter
        }
        
        binding.rvAvailableServices.apply {
            layoutManager = GridLayoutManager(activity, 4)
            adapter = availableServicesAdapter
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