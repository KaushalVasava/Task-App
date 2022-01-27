package com.lahsuak.apps.mylist.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.R
import androidx.navigation.ui.setupActionBarWithNavController
import com.lahsuak.apps.mylist.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    companion object{
        var shareTxt:String?=null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //shared text received from other apps
        if (intent?.action == Intent.ACTION_SEND) {
            if ("text/plain" == intent.type) {
                shareTxt = intent.getStringExtra(Intent.EXTRA_TEXT)
                // binding.editText.setText(txt)
                Log.d("TAG", "onCreate: $shareTxt")
            }
        }
        //this is for transparent status bar and navigation bar
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            true
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars =
            true
        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment)
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)//,appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        //Pass argument appBarConfiguration in navigateUp() method
        // for hamburger icon respond to click events
        //navConfiguration
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}