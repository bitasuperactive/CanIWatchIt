package com.example.caniwatchitapplication.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.data.database.AppDatabase
import com.example.caniwatchitapplication.data.repository.GithubRepository
import com.example.caniwatchitapplication.data.repository.PantryRepository
import com.example.caniwatchitapplication.data.repository.WatchmodeRepository
import com.example.caniwatchitapplication.databinding.ActivityMainBinding
import com.example.caniwatchitapplication.ui.view.dialogs.UpdateDialog
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.ui.viewmodel.SearchViewModel
import com.example.caniwatchitapplication.ui.viewmodel.StreamingSourcesViewModel
import com.example.caniwatchitapplication.ui.viewmodel.TitleViewModel
import com.example.caniwatchitapplication.ui.viewmodel.providers.AppViewModelProvider
import com.example.caniwatchitapplication.ui.viewmodel.providers.FragmentViewModelProvider
import com.example.caniwatchitapplication.util.Constants.Companion.QUERY_LIMIT_PER_DAY
import com.example.caniwatchitapplication.util.Constants.Companion.SEARCH_FOR_TITLES_DELAY
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding
    lateinit var appViewModel: AppViewModel
    lateinit var searchViewModel: SearchViewModel
    lateinit var streamingSourcesViewModel: StreamingSourcesViewModel
    lateinit var titleViewModel: TitleViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Room
        val database = AppDatabase(this)

        // Repositories
        val watchmodeRepo = WatchmodeRepository(database)
        val githubRepo = GithubRepository()
        val pantryRepo = PantryRepository()

        // ViewModels
        appViewModel = ViewModelProvider(
            this,
            AppViewModelProvider(watchmodeRepo, githubRepo, pantryRepo)
        )[AppViewModel::class.java]
        searchViewModel = ViewModelProvider(
            this,
            FragmentViewModelProvider(appViewModel)
        )[SearchViewModel::class.java]
        streamingSourcesViewModel = ViewModelProvider(
            this,
            FragmentViewModelProvider(appViewModel)
        )[StreamingSourcesViewModel::class.java]
        titleViewModel = ViewModelProvider(this)[TitleViewModel::class.java]

        // Navigation
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.appNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Observers
        setupSubscribedSourcesObserver()
        setupSearchObserver()

        // Update manager
        if (savedInstanceState == null) {
            MainScope().launch { UpdateDialog(this@MainActivity).show() }
        }
    }

    /**
     * Bloquea la nevegación hasta que no se seleccione al menos una plataforma de streaming a
     * filtrar.
     */
    private fun setupSubscribedSourcesObserver()
    {
        // Pista para seleccionar al menos una plataforma de streaming
        val snackbarHint = Snackbar.make(
            binding.root,
            getString(R.string.select_at_least_one_streaming_source),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            anchorView = binding.bottomNavigationView
        }

        streamingSourcesViewModel.subscribedStreamingSources.observe(this) { subscribedSources ->

            if (subscribedSources.isEmpty()) {
                // Navigate to the streaming sources fragment using the navigation menu
                binding.bottomNavigationView.selectedItemId = R.id.streamingSourcesFragment
                // Disable navigation to search fragment
                binding.bottomNavigationView.menu.getItem(0).isEnabled = false
                snackbarHint.show()

            } else {
                // Enable navigation to search fragment
                binding.bottomNavigationView.menu.getItem(0).isEnabled = true
                snackbarHint.dismiss()
            }
        }
    }

    /**
     * Mediante un trabajo autocancelable y, tras un delay predefino, realiza la búsqueda de la
     * entrada del usuario si este no ha superado el límite diario.
     *
     * _El contexto de la actividad es requerido para evitar peticiones innecesarias al volver
     * de la navegación a otros fragmentos._
     *
     * @see SearchViewModel.query
     * @see WatchmodeRepository.isQueryLimitReached
     * @see SearchViewModel.searchForTitles
     * @see SEARCH_FOR_TITLES_DELAY
     */
    private fun setupSearchObserver()
    {
        var job: Job? = null
        val snackbar = Snackbar.make(
            binding.root,
            getString(R.string.query_limit_reached, QUERY_LIMIT_PER_DAY),
            Snackbar.LENGTH_LONG
        ).apply {
            anchorView = binding.bottomNavigationView
        }

        searchViewModel.query.observe(this) { query ->

            job?.cancel()

            job = MainScope().launch {
                if (query.isBlank()) {
                    return@launch
                }

                delay(SEARCH_FOR_TITLES_DELAY)

                if (!searchViewModel.isQueryLimitReached().await()) {
                    searchViewModel.searchForTitles(query)
                } else {
                    snackbar.show()
                }
            }
            job?.start()
        }
    }
}