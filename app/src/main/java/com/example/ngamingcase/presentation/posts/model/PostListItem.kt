package com.example.ngamingcase.presentation.posts.model

import com.example.ngamingcase.domain.model.Post

sealed class PostListItem {
    data class PostUi(val post: Post) : PostListItem()
    data class AdUi(val stableId: String) : PostListItem()
}
