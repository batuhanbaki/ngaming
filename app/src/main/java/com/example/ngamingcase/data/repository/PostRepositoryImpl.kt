package com.example.ngamingcase.data.repository

import com.example.ngamingcase.core.logging.AppLogger
import com.example.ngamingcase.data.mapper.toDomain
import com.example.ngamingcase.data.remote.api.PostsApiService
import com.example.ngamingcase.domain.model.Post
import com.example.ngamingcase.domain.repository.PostRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val apiService: PostsApiService,
    private val logger: AppLogger
) : PostRepository {
    private val posts = MutableStateFlow<List<Post>>(emptyList())

    override fun observePosts(): Flow<List<Post>> = posts.asStateFlow()

    override suspend fun refreshPosts() {
        posts.value = emptyList()
        logger.i("PostsRepository", "Fetching posts from remote")
        runCatching { apiService.getPosts().map { it.toDomain() } }
            .onSuccess {
                posts.value = it
                logger.i("PostsRepository", "Posts fetched successfully. Count: ${it.size}")
            }
            .onFailure {
                logger.e(
                    "PostsRepository",
                    "Failed to fetch posts: ${it::class.java.simpleName} ${it.message}",
                    it
                )
                throw it
            }
    }

    override suspend fun deletePost(postId: Int) {
        posts.value = posts.value.filterNot { it.id == postId }
        logger.i("PostsRepository", "Post deleted locally. PostId: $postId")
    }

    override suspend fun updatePost(postId: Int, title: String, body: String) {
        posts.value =
            posts.value.map { if (it.id == postId) it.copy(title = title, body = body) else it }
        logger.i("PostsRepository", "Post updated locally. PostId: $postId")
    }
}
