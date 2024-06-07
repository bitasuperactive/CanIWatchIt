package com.example.caniwatchitapplication.ui.view.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.databinding.FragmentTitleBinding

/**
 * Fragmento destinado a mostrar una WebView de la página de IMDb correspondiente a un título
 * específico.
 */
class TitleFragment : Fragment(R.layout.fragment_title)
{
    private var _binding: FragmentTitleBinding? = null
    private val binding get() = _binding!!
    private val args: TitleFragmentArgs by navArgs()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FragmentTitleBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        
        val imdbId = args.titleIds.imdbId
        val urlToImdb = "https://www.imdb.com/title/$imdbId"
        
        binding.webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = TitleWebViewClient()
            loadUrl(urlToImdb)
        }
    }
    
    inner class TitleWebViewClient : WebViewClient()
    {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?)
        {
            super.onPageStarted(view, url, favicon)
            showProgressBar()
        }
        
        override fun onPageCommitVisible(view: WebView?, url: String?)
        {
            super.onPageCommitVisible(view, url)
            hideProgressBar()
        }
    }
    
    private fun hideProgressBar()
    {
        binding.webViewProgressBar.visibility = View.INVISIBLE
    }
    
    private fun showProgressBar()
    {
        binding.webViewProgressBar.visibility = View.VISIBLE
    }
}