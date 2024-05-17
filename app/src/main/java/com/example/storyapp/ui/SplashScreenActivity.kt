package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.database.SsPreferences
import com.example.storyapp.databinding.ActivitySplashScreenBinding
import com.example.storyapp.viewmodel.UserViewModel
import com.example.storyapp.viewmodel.ViewModelFactory

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        val ssPref = SsPreferences.getInstance(dataStore)
        val userViewModel = ViewModelProvider(this, ViewModelFactory(ssPref))[UserViewModel::class.java]

        userViewModel.getLogin().observe(this) {isLogin ->
            val intent = if (isLogin) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }

            binding.logo.animate()
                .setDuration(3000)
                .alpha(0f)
                .withEndAction {
                    startActivity(intent)
                    finish()
                }
        }
    }
}