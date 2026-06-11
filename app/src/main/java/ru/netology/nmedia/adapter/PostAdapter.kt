package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onVideoClick(video: String) {}
    fun onPostClick(post: Post) {}
    fun onImage(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    private fun formatCount(count: Int): String =
        when {
            count < 1_000 -> count.toString()
            count < 10_000 -> {
                val hundreds = (count % 1_000) / 100
                if (hundreds == 0) "${count / 1_000}K"
                else "${count / 1_000}.${hundreds}K"
            }
            count < 1_000_000 -> "${count / 1_000}K"
            else -> {
                val hundreds = (count % 1_000_000) / 100_000
                if (hundreds == 0) "${count / 1_000_000}M"
                else "${count / 1_000_000}.${hundreds}M"
            }
        }

    fun bind(post: Post) = with(binding) {
        author.text = post.author
        published.text = post.published.toString()
        content.text = post.content

        post.authorAvatar?.let { imageName ->
            val url = "http://10.0.2.2:9999/avatars/$imageName"
            Glide.with(avatar.context)
                .load(url)
                .placeholder(R.drawable.ic_loading_100dp)
                .error(R.drawable.ic_error_100dp)
                .circleCrop()
                .timeout(10_000)
                .into(avatar)
        } ?: avatar.setImageResource(R.drawable.ic_error_100dp)

        likeImg.isChecked = post.likedByMe
        likeImg.text = formatCount(post.likes)

        likeImg.setOnClickListener {
            onInteractionListener.onLike(post)
        }

        attachment.visibility = View.GONE
        attachment.setOnClickListener(null)

        when (post.attachment?.type) {
            AttachmentType.IMAGE -> {
                attachment.visibility = View.VISIBLE
                attachment.load("http://10.0.2.2:9999/media/${post.attachment.url}") {
                    placeholder(R.drawable.ic_loading_100dp)
                    error(R.drawable.ic_error_100dp)
                }
                attachment.setOnClickListener {
                    onInteractionListener.onImage(post)
                }
            }
            else -> {
                attachment.visibility = View.GONE
                attachment.setOnClickListener(null)
            }
        }

        shareImg.setOnClickListener {
            onInteractionListener.onShare(post)
        }

        content.setOnClickListener {
            onInteractionListener.onPostClick(post)
        }

        more.setOnClickListener { view ->
            PopupMenu(view.context, view).apply {
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

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem == newItem
}