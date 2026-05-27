package com.example.ngamingcase.data.repository

import com.example.ngamingcase.data.mapper.toDomain
import com.example.ngamingcase.data.remote.api.PostsApiService
import com.example.ngamingcase.domain.model.Post
import com.example.ngamingcase.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val apiService: PostsApiService
) : PostRepository {
    private val posts = MutableStateFlow<List<Post>>(emptyList())

    override fun observePosts(): Flow<List<Post>> = posts.asStateFlow()

    override suspend fun refreshPosts() {
        if (posts.value.isNotEmpty()) return
        posts.value = apiService.getPosts().map { it.toDomain() }
    }

    override suspend fun deletePost(postId: Int) {
        posts.value = posts.value.filterNot { it.id == postId }
    }

    override suspend fun updatePost(postId: Int, title: String, body: String) {
        posts.value = posts.value.map {
            if (it.id == postId) it.copy(title = title, body = body) else it
        }
    }
}
