package com.example.caniwatchitapplication.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.databinding.FragmentSearchBinding
import com.example.caniwatchitapplication.ui.adapter.TitlesAdapter
import com.example.caniwatchitapplication.ui.view.MainActivity
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.util.Constants.Companion.SEARCH_FOR_TITLES_DELAY
import com.example.caniwatchitapplication.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search)
{
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : AppViewModel
    private lateinit var titlesAdapter: TitlesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).appViewModel
        setupAdapter()
        
        viewModel.searchedTitles.observe(viewLifecycleOwner) { response ->
            when(response)
            {
                is Resource.Loading -> {
                    showProgressBar()
                }
    
                is Resource.Success -> {
                    response.data?.let {
                        hideProgressBar()
                        titlesAdapter.submitList(it)
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
        titlesAdapter = TitlesAdapter(viewModel)
        
        binding.rvSearchedMovies.apply {
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