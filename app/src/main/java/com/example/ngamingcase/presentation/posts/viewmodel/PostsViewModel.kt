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
import kotlin.random.Random

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

    fun refresh() = viewModelScope.launch {
        _uiState.update { PostsUiState.Loading }
        runCatching { getPostsUseCase.refresh() }
            .onFailure { _uiState.value = PostsUiState.Error(it.message ?: "Failed to load posts") }
    }

    private fun observePosts() {
        viewModelScope.launch {
            getPostsUseCase()
                .catch { _uiState.value = PostsUiState.Error(it.message ?: "Unknown error") }
                .collect { posts ->
                    _uiState.value = if (posts.isEmpty()) {
                        PostsUiState.Empty
                    } else {
                        PostsUiState.Success(posts.toUiItems())
                    }
                }
        }
    }

    fun deletePost(postId: Int) = viewModelScope.launch { deletePostUseCase(postId) }

    fun updatePost(postId: Int, title: String, body: String) =
        viewModelScope.launch { updatePostUseCase(postId, title, body) }

    private fun List<Post>.toUiItems(): List<PostListItem> {
        val feedSeed = fold(17) { acc, post -> 31 * acc + post.id }
        val random = Random(feedSeed)
        val adDescriptions = listOf(
            "Discover more gaming content",
            "Recommended for you",
            "Ngaming Partner Content",
            "Special promotion"
        )
        val result = mutableListOf<PostListItem>()

        var postsSinceAd = 0
        var nextAdAfter = randomInterval(random)

        forEachIndexed { index, post ->
            result += PostListItem.PostUi(
                post = post,
                imageSeed = post.id.takeIf { it > 0 } ?: (index + 1)
            )
            postsSinceAd++

            if (postsSinceAd == nextAdAfter && index != lastIndex) {
                val description = adDescriptions[random.nextInt(adDescriptions.size)]
                result += PostListItem.AdUi(
                    stableId = "ad_after_${post.id}_$postsSinceAd",
                    title = "Sponsored",
                    description = description,
                    ctaText = "Tap to explore"
                )
                postsSinceAd = 0
                nextAdAfter = randomInterval(random)
            }
        }
        return result
    }

    private fun randomInterval(random: Random): Int = if (random.nextBoolean()) 4 else 5
}
