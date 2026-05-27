package com.example.ngamingcase.domain.usecase

import com.example.ngamingcase.core.logging.AppLogger
import com.example.ngamingcase.domain.model.Post
import com.example.ngamingcase.domain.repository.PostRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetPostsUseCase @Inject constructor(
    private val repository: PostRepository,
    private val logger: AppLogger
) {
    operator fun invoke(): Flow<List<Post>> {
        logger.d("GetPostsUseCase", "observe posts invoked")
        return repository.observePosts()
    }

    suspend fun refresh() {
        logger.i("GetPostsUseCase", "refresh invoked")
        repository.refreshPosts()
    }
}
