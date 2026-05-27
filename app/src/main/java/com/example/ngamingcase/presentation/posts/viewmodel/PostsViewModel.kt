package com.example.ngamingcase.presentation.posts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngamingcase.domain.model.Post
import com.example.ngamingcase.domain.usecase.DeletePostUseCase
import com.example.ngamingcase.domain.usecase.GetPostsUseCase
import com.example.ngamingcase.domain.usecase.UpdatePostUseCase
import com.example.ngamingcase.presentation.posts.model.PostListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val updatePostUseCase: UpdatePostUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()

    init {
        observePosts()
        refresh()
    }

    private fun observePosts() {
        viewModelScope.launch {
            getPostsUseCase()
                .catch { _uiState.value = PostsUiState.Error(it.message ?: "Unknown error") }
                .collect { posts ->
                    _uiState.value = if (posts.isEmpty()) PostsUiState.Empty else PostsUiState.Success(posts.toUiItems())
                }
        }
    }

    private fun refresh() = viewModelScope.launch {
        _uiState.update { PostsUiState.Loading }
        runCatching { getPostsUseCase.refresh() }
            .onFailure { _uiState.value = PostsUiState.Error(it.message ?: "Failed to load posts") }
    }

    fun deletePost(postId: Int) = viewModelScope.launch { deletePostUseCase(postId) }

    fun updatePost(postId: Int, title: String, body: String) =
        viewModelScope.launch { updatePostUseCase(postId, title, body) }

    private fun List<Post>.toUiItems(): List<PostListItem> {
        val result = mutableListOf<PostListItem>()
        forEachIndexed { index, post ->
            result += PostListItem.PostUi(post)
            if ((index + 1) % 5 == 0 && index != lastIndex) result += PostListItem.AdUi("ad_${index + 1}")
        }
        return result
    }
}
