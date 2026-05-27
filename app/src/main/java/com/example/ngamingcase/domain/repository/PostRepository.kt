package com.example.ngamingcase.domain.repository

import com.example.ngamingcase.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun observePosts(): Flow<List<Post>>
    suspend fun refreshPosts()
    suspend fun deletePost(postId: Int)
    suspend fun updatePost(postId: Int, title: String, body: String)
}
