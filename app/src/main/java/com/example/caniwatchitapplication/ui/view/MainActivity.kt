package com.example.caniwatchitapplication.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.data.database.AppDatabase
import com.example.caniwatchitapplication.data.repository.AppRepository
import com.example.caniwatchitapplication.databinding.ActivityMainBinding
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModelProvider
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding

    /**
     * Snackbar indicativo de que se debe seleccionar al menos una plataforma de streaming.
     */
    private lateinit var snackbarHint: Snackbar

    lateinit var appViewModel: AppViewModel
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase(this)
        val repository = AppRepository(database)
        val appViewModelProvider = AppViewModelProvider(repository)
        
        appViewModel = ViewModelProvider(this, appViewModelProvider)[AppViewModel::class.java]
        
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.appNavHostFragment) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)

        snackbarHint = Snackbar.make(
            binding.root,
            getString(R.string.select_at_least_one_streaming_source),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            anchorView = binding.bottomNavigationView
        }

        setupNavigationBlocker()
    }

    /**
     * Bloquea la nevegación hasta que no se seleccione al menos una plataforma de streaming a
     * filtrar.
     */
    private fun setupNavigationBlocker()
    {
        appViewModel.getAllSubscribedStreamingSources().observe(this) { subscribedSources ->

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