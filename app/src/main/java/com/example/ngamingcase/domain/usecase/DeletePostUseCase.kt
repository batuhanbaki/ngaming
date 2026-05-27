package com.example.ngamingcase.domain.usecase

import com.example.ngamingcase.domain.repository.PostRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: Int) = repository.deletePost(postId)
}
