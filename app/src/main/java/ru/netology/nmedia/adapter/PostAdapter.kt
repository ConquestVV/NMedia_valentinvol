package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

typealias OnLikeListener = (post: Post) -> Unit
typealias OnShareListener = (post: Post) -> Unit

class PostsAdapter(private val onLikeListener: OnLikeListener, private val onShareListener: OnShareListener) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener, onShareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
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
            if (post.likedByMe) {
                likeImg?.setImageResource(R.drawable.ic_liked_24)
            } else {
                likeImg?.setImageResource(R.drawable.ic_like_24)
            }

            likeCount?.text = formatCount(post.likes)

            shareCount?.text = formatCount(post.shares)

            watchersCount?.text = formatCount(post.watches)

            likeImg?.setOnClickListener {
                onLikeListener(post)
            }

            shareImg?.setOnClickListener {
                onShareListener(post)
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