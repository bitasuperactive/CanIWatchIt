package com.example.caniwatchitapplication.ui.view.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.databinding.FragmentTitleBinding
import com.example.caniwatchitapplication.ui.view.MainActivity
import com.example.caniwatchitapplication.ui.viewmodel.TitleViewModel

/**
 * Fragmento destinado a mostrar una WebView de la página de IMDb correspondiente a un título
 * específico.
 *
 * @see TitleViewModel
 */
class TitleFragment : Fragment(R.layout.fragment_title)
{
    /**
     * Permite manejar el estado de la carga web.
     */
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

    private var _binding: FragmentTitleBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TitleViewModel
    private val args: TitleFragmentArgs by navArgs()
    private lateinit var urlToImdb: String

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

        val mainActivity = activity as MainActivity
        viewModel = mainActivity.titleViewModel
        val imdbId = args.titleIds.imdbId
        urlToImdb = "https://www.imdb.com/title/$imdbId"

        setupSavedStateObservers()
        loadWebView()
    }

    /**
     * Restablece la configuración del WebView.
     */
    private fun setupSavedStateObservers()
    {
        viewModel.javaScriptEnabled.observe(viewLifecycleOwner) {
            binding.webView.settings.javaScriptEnabled = it
        }
    }

    /**
     * Si el usuario ya ha establecido la configuración de JavaScript, deshabilita las cookies y
     * carga el enlace correspondiente en el WebView. En caso contrario, muestra el diálogo
     * de configuración.
     *
     * @see denyCookies
     * @see showJavaScriptDialog
     */
    private fun loadWebView()
    {

        if (viewModel.dialogAlreadyShown.value == true) {
            denyCookies()
            binding.webView.apply {
                webViewClient = TitleWebViewClient()
                loadUrl(urlToImdb)
            }
        } else {
            showJavaScriptDialog()
        }
    }

    /**
     * Deshabilita las cookies para el WebView.
     */
    private fun denyCookies()
    {
        CookieManager.getInstance().apply {
            setAcceptCookie(false)
            setAcceptThirdPartyCookies(binding.webView, false)
        }
    }

    /**
     * Muestra un diálogo al usuario para que autorice la activación de JavaScript para los WebViews
     * e inicia la carga del enlace correspondiente.
     *
     * @see loadWebView
     */
    private fun showJavaScriptDialog()
    {
        val dialog = AlertDialog.Builder(this.context).apply {
            setTitle(getString(R.string.enable_javascript_title))
            setMessage(getString(R.string.enable_javascript_message, urlToImdb))
            setPositiveButton(getString(R.string.activate)) { _, _ ->
                viewModel.enableJavaScript()
                loadWebView()
            }
            setNegativeButton(getString(R.string.deny)) { _, _ -> loadWebView() }
            setOnDismissListener { loadWebView() }
        }.create()

        dialog.show()

        viewModel.dialogShown()
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