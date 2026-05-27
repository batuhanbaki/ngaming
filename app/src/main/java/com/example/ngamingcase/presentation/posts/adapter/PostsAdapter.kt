package com.example.ngamingcase.presentation.posts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ngamingcase.databinding.ItemAdBinding
import com.example.ngamingcase.databinding.ItemPostBinding
import com.example.ngamingcase.presentation.posts.model.PostListItem

class PostsAdapter(
    private val onPostClick: (Int, String, String) -> Unit
) : ListAdapter<PostListItem, RecyclerView.ViewHolder>(Diff) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PostListItem.PostUi -> 1
        is PostListItem.AdUi -> 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            PostVH(ItemPostBinding.inflate(inflater, parent, false))
        } else {
            AdVH(ItemAdBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PostListItem.PostUi -> (holder as PostVH).bind(item)
            is PostListItem.AdUi -> (holder as AdVH).bind(item)
        }
    }

    fun getItemAt(position: Int): PostListItem? = currentList.getOrNull(position)

    fun getPostIdAt(position: Int): Int? = (currentList.getOrNull(position) as? PostListItem.PostUi)?.post?.id

    inner class PostVH(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostListItem.PostUi) = with(binding) {
            title.text = item.post.title
            body.text = item.post.body
            meta.text = "Post #${item.post.id}"
            image.load("https://picsum.photos/300/300?random=${item.imageSeed}&grayscale")
            root.setOnClickListener { onPostClick(item.post.id, item.post.title, item.post.body) }
        }
    }

    class AdVH(private val binding: ItemAdBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostListItem.AdUi) = with(binding) {
            adTitle.text = item.title
            adDescription.text = item.description
            adCta.text = item.ctaText
        }
    }

    object Diff : DiffUtil.ItemCallback<PostListItem>() {
        override fun areItemsTheSame(oldItem: PostListItem, newItem: PostListItem): Boolean =
            when {
                oldItem is PostListItem.PostUi && newItem is PostListItem.PostUi -> oldItem.post.id == newItem.post.id
                oldItem is PostListItem.AdUi && newItem is PostListItem.AdUi -> oldItem.stableId == newItem.stableId
                else -> false
            }

        override fun areContentsTheSame(oldItem: PostListItem, newItem: PostListItem): Boolean = oldItem == newItem
    }
}
