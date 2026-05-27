package com.example.ngamingcase.domain.usecase

import com.example.ngamingcase.core.logging.AppLogger
import com.example.ngamingcase.domain.repository.PostRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repository: PostRepository,
    private val logger: AppLogger
) {
    suspend operator fun invoke(postId: Int) {
        logger.i("DeletePostUseCase", "delete requested. PostId: $postId")
        repository.deletePost(postId)
    }
}
