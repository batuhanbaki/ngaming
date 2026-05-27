package com.example.ngamingcase.presentation.posts.viewmodel

import com.example.ngamingcase.presentation.posts.model.PostListItem

sealed class PostsUiState {
    data object Loading : PostsUiState()
    data class Success(val items: List<PostListItem>) : PostsUiState()
    data class Error(val message: String) : PostsUiState()
    data object Empty : PostsUiState()
}
