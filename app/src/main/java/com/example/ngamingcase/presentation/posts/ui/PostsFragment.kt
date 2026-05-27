package com.example.ngamingcase.presentation.posts.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
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
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun getSwipeDirs(rv: RecyclerView, vh: RecyclerView.ViewHolder): Int = if (adapter.getPostIdAt(vh.bindingAdapterPosition) == null) 0 else super.getSwipeDirs(rv, vh)
            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                adapter.getPostIdAt(vh.bindingAdapterPosition)?.let(viewModel::deletePost)
            }
        }).attachToRecyclerView(binding.recyclerView)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progress.visibility = if (state is PostsUiState.Loading) View.VISIBLE else View.GONE
                    when (state) {
                        is PostsUiState.Success -> {
                            adapter.submitList(state.items)
                            binding.empty.visibility = View.GONE
                        }
                        is PostsUiState.Empty -> {
                            binding.empty.visibility = View.VISIBLE
                            binding.empty.setText(R.string.empty)
                        }
                        is PostsUiState.Error -> binding.empty.apply {
                            visibility = View.VISIBLE
                            text = state.message
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
