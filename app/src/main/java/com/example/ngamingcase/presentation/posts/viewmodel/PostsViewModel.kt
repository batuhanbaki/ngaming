package com.example.ngamingcase.presentation.posts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ngamingcase.core.logging.AppLogger
import com.example.ngamingcase.domain.model.Post
import com.example.ngamingcase.domain.usecase.DeletePostUseCase
import com.example.ngamingcase.domain.usecase.GetPostsUseCase
import com.example.ngamingcase.domain.usecase.UpdatePostUseCase
import com.example.ngamingcase.presentation.common.ErrorMapper
import com.example.ngamingcase.presentation.posts.model.PostListItem
import com.example.ngamingcase.presentation.posts.validation.PostInputValidator
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
    private val updatePostUseCase: UpdatePostUseCase,
    private val postInputValidator: PostInputValidator,
    private val errorMapper: ErrorMapper,
    private val logger: AppLogger
) : ViewModel() {
    private val _uiState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()
    private val _postEditUiState = MutableStateFlow<PostEditUiState?>(null)
    val postEditUiState: StateFlow<PostEditUiState?> = _postEditUiState.asStateFlow()
    private var cachedPosts: List<Post> = emptyList()

    init {
        logger.i("PostsViewModel", "initial load triggered")
        observePosts()
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        logger.i("PostsViewModel", "retry clicked / refresh requested")
        logger.d("PostsViewModel", "loading state emitted")
        _uiState.update { PostsUiState.Loading }
        runCatching { getPostsUseCase.refresh() }
            .onFailure {
                val errorMessage = errorMapper.map(it)
                logger.e("PostsViewModel", "error state emitted", it)
                _uiState.value = PostsUiState.Error(errorMessage)
            }
    }

    private fun observePosts() {
        viewModelScope.launch {
            getPostsUseCase()
                .catch {
                    val message = errorMapper.map(it)
                    logger.e("PostsViewModel", "error state emitted from observe", it)
                    _uiState.value = PostsUiState.Error(message)
                }
                .collect { posts ->
                    cachedPosts = posts
                    _uiState.value = if (posts.isEmpty()) {
                        logger.w("PostsViewModel", "empty state emitted")
                        PostsUiState.Empty
                    } else {
                        val items = posts.toUiItems()
                        logger.i("PostsViewModel", "success state emitted with item count: ${items.size}")
                        PostsUiState.Success(items)
                    }
                }
        }
    }

    fun deletePost(postId: Int) = viewModelScope.launch {
        logger.i("PostsViewModel", "post delete requested. PostId: $postId")
        deletePostUseCase(postId)
        logger.d("PostsViewModel", "feed rebuilt after delete/update")
    }

    fun updatePost(postId: Int, title: String, body: String) =
        viewModelScope.launch {
            logger.i("PostsViewModel", "save clicked. PostId: $postId")
            val validation = postInputValidator.validate(title, body)
            if (!validation.isValid) {
                logger.w("PostsViewModel", "validation failed. PostId: $postId")
                _postEditUiState.value = _postEditUiState.value?.copy(
                    title = title,
                    body = body,
                    titleError = validation.titleError,
                    bodyError = validation.bodyError,
                    isSaving = false,
                    isSaved = false
                )
                return@launch
            }
            logger.i("PostsViewModel", "update started. PostId: $postId")
            _postEditUiState.value = _postEditUiState.value?.copy(
                title = title,
                body = body,
                titleError = null,
                bodyError = null,
                isSaving = true,
                isSaved = false,
                errorMessage = null
            )
            runCatching { updatePostUseCase(postId, title.trim(), body.trim()) }
                .onSuccess {
                    logger.i("PostsViewModel", "update success. PostId: $postId")
                    _postEditUiState.value = _postEditUiState.value?.copy(
                        isSaving = false,
                        isSaved = true
                    )
                }
                .onFailure {
                    logger.e("PostsViewModel", "update failed. PostId: $postId", it)
                    _postEditUiState.value = _postEditUiState.value?.copy(
                        isSaving = false,
                        isSaved = false,
                        errorMessage = errorMapper.map(it)
                    )
                }
        }

    fun onPostClicked(postId: Int) {
        val post = cachedPosts.firstOrNull { it.id == postId } ?: return
        logger.i("PostsViewModel", "post edit opened. PostId: $postId")
        _postEditUiState.value = PostEditUiState(
            postId = post.id,
            title = post.title,
            body = post.body
        )
    }

    fun onEditDismissed() {
        _postEditUiState.value?.postId?.let { logger.d("PostsViewModel", "bottom sheet dismissed. PostId: $it") }
        _postEditUiState.value = null
    }

    private fun List<Post>.toUiItems(): List<PostListItem> {
        val feedSeed = fold(17) { acc, post -> 31 * acc + post.id }
        logger.d("FeedBuilder", "Building feed list. Posts: ${size}, Seed: $feedSeed")
        val random = Random(feedSeed)
        val adDescriptions = listOf(
            "Discover more gaming content",
            "Recommended for you",
            "Ngaming Partner Content",
            "Special promotion"
        )
        val result = mutableListOf<PostListItem>()
        var adCount = 0
        var postsSinceAd = 0
        var nextAdAfter = randomInterval(random)

        forEachIndexed { index, post ->
            result += PostListItem.PostUi(post = post, imageSeed = post.id.takeIf { it > 0 } ?: (index + 1))
            postsSinceAd++

            if (postsSinceAd == nextAdAfter && index != lastIndex) {
                val description = adDescriptions[random.nextInt(adDescriptions.size)]
                logger.i("FeedBuilder", "Ad inserted. AfterPosts: $nextAdAfter, Index: $index")
                result += PostListItem.AdUi(
                    stableId = "ad_after_${post.id}_$postsSinceAd",
                    title = "Sponsored",
                    description = description,
                    ctaText = "Tap to explore"
                )
                adCount++
                postsSinceAd = 0
                nextAdAfter = randomInterval(random)
            }
        }
        logger.i("FeedBuilder", "Feed built. Posts: ${size}, Ads: $adCount, TotalItems: ${result.size}")
        return result
    }

    private fun randomInterval(random: Random): Int = if (random.nextBoolean()) 4 else 5
}
