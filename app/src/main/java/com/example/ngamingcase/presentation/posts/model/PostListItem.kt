package com.example.ngamingcase.presentation.posts.model

import com.example.ngamingcase.domain.model.Post

sealed class PostListItem {
    data class PostUi(
        val post: Post,
        val imageSeed: Int
    ) : PostListItem()

    data class AdUi(
        val stableId: String,
        val title: String,
        val description: String,
        val ctaText: String
    ) : PostListItem()
}
