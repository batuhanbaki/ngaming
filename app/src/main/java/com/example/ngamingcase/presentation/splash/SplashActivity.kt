package com.example.ngamingcase.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ngamingcase.databinding.ActivitySplashBinding
import com.example.ngamingcase.presentation.posts.ui.PostsActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            delay(1200)
            startActivity(Intent(this@SplashActivity, PostsActivity::class.java))
            finish()
        }
    }
}
