package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}

    fun onVideoClick(video: String) {}
}

class PostsAdapter(private val onInteractionListener: OnInteractionListener) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun formatCount(count: Int): String =
        when {
            count < 1_000L -> count.toString()
            count < 10_000L -> {
                val hundreds = (count % 1_000L) / 100L
                if (hundreds == 0L) "${count / 1_000L}K"
                else "${count / 1_000L}.${hundreds}K"
            }
            count < 1_000_000L -> "${count / 1_000L}K"
            else -> {
                val hundreds = (count % 1_000_000L) / 100_000L
                if (hundreds == 0L) "${count / 1_000_000L}M"
                else "${count / 1_000_000L}.${hundreds}M"
            }
        }

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content

            likeImg.isChecked = post.likedByMe

            likeImg?.text = formatCount(post.likes)

            shareImg?.text = formatCount(post.shares)

            watchersCount?.text = formatCount(post.watches)

            post.video?.let { videoLink ->
                video?.setOnClickListener {
                    onInteractionListener.onVideoClick(videoLink)
                }
            }
            videoGroup.isVisible = post.video != null



            likeImg?.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            shareImg?.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            more.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}