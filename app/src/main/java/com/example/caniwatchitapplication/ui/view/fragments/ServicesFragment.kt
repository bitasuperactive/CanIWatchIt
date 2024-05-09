package com.example.caniwatchitapplication.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.databinding.FragmentServicesBinding
import com.example.caniwatchitapplication.ui.adapter.ServicesAdapter
import com.example.caniwatchitapplication.ui.view.MainActivity
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.util.Resource

class ServicesFragment : Fragment(R.layout.fragment_services)
{
    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel
    private lateinit var servicesAdapter: ServicesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).appViewModel
        setupAdapter()
        
        viewModel.availableServices.observe(viewLifecycleOwner) { response ->
            when(response)
            {
                is Resource.Loading -> {
                    showProgressBar()
                }
        
                is Resource.Success -> {
                    response.data?.let {
                        hideProgressBar()
                        servicesAdapter.submitList(it)
                    }
                }
        
                is Resource.Error -> {
                    response.message?.let {
                        hideProgressBar()
                        Toast.makeText(
                            activity,
                            "Se ha producido un error: $it",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        
        servicesAdapter.setupOnClickListener {
            // TODO - Not implemented.
        }
    }
    
    private fun setupAdapter()
    {
        servicesAdapter = ServicesAdapter(150)
        
        binding.rvAvailableServices.apply {
            layoutManager = GridLayoutManager(activity, 5)
            adapter = servicesAdapter
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