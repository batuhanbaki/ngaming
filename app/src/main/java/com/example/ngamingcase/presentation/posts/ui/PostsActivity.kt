package com.example.ngamingcase.presentation.posts.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ngamingcase.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, PostsFragment()).commit()
        }
    }
}
