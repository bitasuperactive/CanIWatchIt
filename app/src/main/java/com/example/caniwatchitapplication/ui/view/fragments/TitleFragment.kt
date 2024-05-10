package com.example.caniwatchitapplication.ui.view.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.databinding.FragmentTitleBinding

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
        
        val imdbId = args.titleDetails.imdbId
        val urlToImdb = "https://www.imdb.com/title/$imdbId"
        
        binding.webView.apply {
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
        
        override fun onPageFinished(view: WebView?, url: String?)
        {
            super.onPageFinished(view, url)
            hideProgressBar()
        }
        
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        )
        {
            super.onReceivedError(view, request, error)
            
            Toast.makeText(
                activity,
                "Se ha producido un error: ${error?.description}",
                Toast.LENGTH_LONG
            ).show()
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