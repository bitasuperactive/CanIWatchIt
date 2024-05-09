package com.example.caniwatchitapplication.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.caniwatchitapplication.R
import com.example.caniwatchitapplication.data.repository.AppRepository
import com.example.caniwatchitapplication.databinding.ActivityMainBinding
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModel
import com.example.caniwatchitapplication.ui.viewmodel.AppViewModelProvider

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding
    lateinit var appViewModel: AppViewModel
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    
        val repository = AppRepository()
        val appViewModelProvider = AppViewModelProvider(repository)
        
        appViewModel = ViewModelProvider(this, appViewModelProvider)[AppViewModel::class.java]
    
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.appNavHostFragment) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)
    }
}