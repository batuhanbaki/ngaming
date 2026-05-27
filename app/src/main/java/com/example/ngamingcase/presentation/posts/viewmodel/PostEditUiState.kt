package com.example.ngamingcase.presentation.posts.viewmodel

data class PostEditUiState(
    val postId: Int? = null,
    val title: String = "",
    val body: String = "",
    val titleError: String? = null,
    val bodyError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

