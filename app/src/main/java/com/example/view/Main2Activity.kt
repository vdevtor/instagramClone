package com.example.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import com.example.fragments.HomeFragment
import com.example.fragments.NotificationFragment
import com.example.fragments.ProfileFragment
import com.example.fragments.SearchFragment
import com.example.instagramclone.R
import com.example.instagramclone.databinding.ActivityMain2Binding
import com.example.utils.Constans.Companion.estiveaqui
import com.example.utils.Constans.Companion.jaApertei
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class Main2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                movteToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true

            }
            R.id.navigation_Search -> {
                movteToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_add -> {
                item.isChecked = false
                startActivity(Intent(this@Main2Activity,AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                movteToFragment(NotificationFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                movteToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }

        }
        false


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
       if (estiveaqui){
           movteToFragment(ProfileFragment())
           estiveaqui = false
       }else{
           movteToFragment(HomeFragment())
       }
        binding.navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)



    }

    private fun movteToFragment(fragment: Fragment){
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(binding.fragmentContainer.id,fragment)
        fragmentTrans.commit()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (!jaApertei) {
            startActivity(Intent(this, Main2Activity::class.java))
            jaApertei = true
        }else{
            jaApertei = false
            finish()
        }

    }


}