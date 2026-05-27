package com.example.ngamingcase.domain.usecase

import com.example.ngamingcase.domain.model.Post
import com.example.ngamingcase.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(): Flow<List<Post>> = repository.observePosts()
    suspend fun refresh() = repository.refreshPosts()
}
