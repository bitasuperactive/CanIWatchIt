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
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding

    lateinit var appViewModel: AppViewModel
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase(this)
        val watchmodeRepo = WatchmodeRepository(database)
        val githubRepo = GithubRepository()
        val pantryRepo = PantryRepository()
        val appViewModelProvider = AppViewModelProvider(watchmodeRepo, githubRepo, pantryRepo)
        
        appViewModel = ViewModelProvider(this, appViewModelProvider)[AppViewModel::class.java]

        val navHostFragment = binding.appNavHostFragment.getFragment<NavHostFragment>()
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)

        setupNavigationBlocker()

        MainScope().launch { UpdateDialog(this@MainActivity).show() }
    }

    /**
     * Bloquea la nevegación hasta que no se seleccione al menos una plataforma de streaming a
     * filtrar.
     */
    private fun setupNavigationBlocker()
    {
        // Hint para seleccionar al menos una plataforma de streaming
        val snackbarHint = Snackbar.make(
            binding.root,
            getString(R.string.select_at_least_one_streaming_source),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            anchorView = binding.bottomNavigationView
        }

        appViewModel.subscribedStreamingSources.observe(this) { subscribedSources ->

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
}