package com.example.ngamingcase.domain.usecase

import com.example.ngamingcase.domain.repository.PostRepository
import javax.inject.Inject

class UpdatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: Int, title: String, body: String) = repository.updatePost(postId, title, body)
}
