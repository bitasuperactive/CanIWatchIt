package com.example.caniwatchitapplication.ui.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.caniwatchitapplication.ui.adapter.SubscribedServicesAdapter
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

class SearchFragment : Fragment(R.layout.fragment_search)
{
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel
    private lateinit var subscribedServicesAdapter: SubscribedServicesAdapter
    private lateinit var titlesAdapter: TitlesAdapter
    
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
        setupAdapter()
        
        viewModel.getAllSubscribedServices().observe(viewLifecycleOwner) {
            
            subscribedServicesAdapter.submitList(it)
        }
        
        binding.tvHintToSearch.setOnClickListener {
            
            binding.etTitleToSearch.requestFocus()
            // Show the keyboard.
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etTitleToSearch, InputMethodManager.SHOW_IMPLICIT)
        }
        
        // Set functionality of the delete image inside the EditText.
        binding.etTitleToSearch.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP)
            {
                val drawableEnd =
                    binding.etTitleToSearch.compoundDrawablesRelative[2] // 2 representa el índice de la imagen a la derecha
                if (drawableEnd != null && event.rawX >= binding.etTitleToSearch.right - drawableEnd.bounds.width())
                {
                    binding.etTitleToSearch.text = null
                    return@setOnTouchListener true
                }
            }
            false
        }
        
        viewModel.searchedTitles.observe(viewLifecycleOwner) { response ->
            when (response)
            {
                is Resource.Loading ->
                {
                    showProgressBar()
                }
                
                is Resource.Success ->
                {
                    response.data?.let { response ->
                        hideProgressBar()
                        val titlesOrderedBySource =
                            response.sortedBy{ it.sources.isEmpty() }
                        titlesAdapter.submitList(titlesOrderedBySource)
                        binding.tvNoTitlesFound.visibility =
                            if (response.isEmpty()) View.VISIBLE else View.INVISIBLE
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
        
        var searchJob: Job? = null
        binding.etTitleToSearch.addTextChangedListener {
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(SEARCH_FOR_TITLES_DELAY)
                it?.let {
                    val searchValue = it.toString()
                    if (searchValue.isNotBlank())
                    {
                        hideSearchHint()
                        viewModel.searchForTitles(it.toString())
                    } else
                    {
                        titlesAdapter.submitList(emptyList())
                        showSearchHint()
                    }
                }
            }
        }
        
        titlesAdapter.setupOnClickListener {
            val bundle = bundleOf("titleDetails" to it)
            
            findNavController().navigate(
                R.id.action_searchFragment_to_titleFragment,
                bundle
            )
        }
    }
    
    private fun setupAdapter()
    {
        subscribedServicesAdapter = SubscribedServicesAdapter()
        titlesAdapter = TitlesAdapter(viewModel, viewLifecycleOwner)
        
        binding.servicesDisplayer.rvSubscribedServices.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            adapter = subscribedServicesAdapter
        }
        
        binding.rvSearchedTitles.apply {
            this.layoutManager = LinearLayoutManager(activity)
            this.adapter = titlesAdapter
        }
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