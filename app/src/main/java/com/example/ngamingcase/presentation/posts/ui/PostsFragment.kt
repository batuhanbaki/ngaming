package com.example.ngamingcase.presentation.posts.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ngamingcase.R
import com.example.ngamingcase.databinding.FragmentPostsBinding
import com.example.ngamingcase.presentation.detail.PostDetailBottomSheet
import com.example.ngamingcase.presentation.posts.adapter.PostsAdapter
import com.example.ngamingcase.presentation.posts.viewmodel.PostsUiState
import com.example.ngamingcase.presentation.posts.viewmodel.PostsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostsFragment : Fragment(R.layout.fragment_posts) {
    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostsViewModel by viewModels()
    private lateinit var adapter: PostsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostsBinding.bind(view)

        adapter = PostsAdapter { id, title, body ->
            PostDetailBottomSheet.newInstance(id, title, body).show(childFragmentManager, "detail")
        }
        binding.recyclerView.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun getSwipeDirs(rv: RecyclerView, vh: RecyclerView.ViewHolder): Int {
                return if (adapter.getPostIdAt(vh.bindingAdapterPosition) == null) 0 else super.getSwipeDirs(rv, vh)
            }

            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                val position = vh.bindingAdapterPosition
                val postId = adapter.getPostIdAt(position)
                if (postId != null) {
                    viewModel.deletePost(postId)
                } else {
                    adapter.notifyItemChanged(position)
                }
            }
        }).attachToRecyclerView(binding.recyclerView)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: PostsUiState) {
        binding.swipeRefresh.isRefreshing = false
        when (state) {
            is PostsUiState.Loading -> {
                binding.recyclerView.visibility = View.GONE
                binding.empty.visibility = View.GONE
                binding.shimmerContainer.visibility = View.VISIBLE
                binding.shimmerContainer.startShimmer()
            }

            is PostsUiState.Success -> {
                binding.shimmerContainer.stopShimmer()
                binding.shimmerContainer.visibility = View.GONE
                binding.empty.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(state.items)
            }

            is PostsUiState.Empty -> {
                binding.shimmerContainer.stopShimmer()
                binding.shimmerContainer.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.empty.visibility = View.VISIBLE
                binding.empty.setText(R.string.empty)
            }

            is PostsUiState.Error -> {
                binding.shimmerContainer.stopShimmer()
                binding.shimmerContainer.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.empty.visibility = View.VISIBLE
                binding.empty.text = state.message
            }
        }
    }

    override fun onDestroyView() {
        binding.shimmerContainer.stopShimmer()
        super.onDestroyView()
        _binding = null
    }
}
