package com.example.caniwatchitapplication.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.data.db.AppDatabase
import com.example.caniwatchitapplication.data.repository.AppRepository
import com.example.caniwatchitapplication.databinding.ActivityMainBinding
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModelProvider
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding
    lateinit var appViewModel: AppViewModel
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val repository = AppRepository(AppDatabase(this))
        val appViewModelProvider = AppViewModelProvider(repository)
        
        appViewModel = ViewModelProvider(this, appViewModelProvider)[AppViewModel::class.java]
        
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.appNavHostFragment) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)
        
        // Block the navigation until at least once service is selected.
        appViewModel.getAllSubscribedStreamingSources().observe(this) { services ->
            
            if (services.isEmpty())
            {
                // Navigate to the services fragment using the navigation menu.
                binding.bottomNavigationView.selectedItemId = R.id.streamingSourcesFragment
                // Disable search fragment navigation.
                binding.bottomNavigationView.menu.getItem(0).isEnabled = false
                
                Snackbar.make(
                    binding.root,
                    getString(R.string.select_at_least_one_streaming_source),
                    Snackbar.LENGTH_LONG
                ).apply {
                    anchorView = binding.bottomNavigationView
                }.show()
            } else
            {
                // Enable search fragment navigation.
                binding.bottomNavigationView.menu.getItem(0).isEnabled = true
            }
        }
    }
}