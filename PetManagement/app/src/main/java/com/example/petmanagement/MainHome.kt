package com.example.petmanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.petmanagement.databinding.FragmentHomeBinding
import com.example.petmanagement.databinding.HomeBinding
import com.example.petmanagement.fragment.HomeRegi


class MainHome: AppCompatActivity() {

    private lateinit var binding: HomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // BottomNavigationViewのIDを修正
        binding.bottomNavigation.setupWithNavController(navController)

//        supportFragmentManager.beginTransaction()
//            .replace(R.id.FragmentHomeRegi, HomeRegi())
//            .commit()




    }
}