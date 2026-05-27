package com.example.ngamingcase.domain.usecase

import com.example.ngamingcase.core.logging.AppLogger
import com.example.ngamingcase.domain.repository.PostRepository
import javax.inject.Inject

class UpdatePostUseCase @Inject constructor(
    private val repository: PostRepository,
    private val logger: AppLogger
) {
    suspend operator fun invoke(postId: Int, title: String, body: String) {
        logger.i("UpdatePostUseCase", "update requested. PostId: $postId")
        repository.updatePost(postId, title, body)
    }
}
