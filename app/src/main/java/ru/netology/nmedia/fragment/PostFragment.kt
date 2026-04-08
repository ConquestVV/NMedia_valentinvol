package ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentPostBinding.inflate(inflater, container, false)

        val id = requireArguments().getLong("postId")

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

        viewModel.data.observe(viewLifecycleOwner) { feedModel ->
            val post = feedModel.posts.find { it.id == id } ?: return@observe


            binding.cardPost.apply {
                author.text = post.author
                published.text = post.published.toString()
                content.text = post.content

                likeImg.isChecked = post.likedByMe

                likeImg?.text = formatCount(post.likes)

//                shareImg?.text = formatCount(post.shares)

//                watchersCount?.text = formatCount(post.watches)

                likeImg?.setOnClickListener {
//                     onInteractionListener.onLike(post)
                    viewModel.like(post.id)
                }

                shareImg.setOnClickListener {
//                    viewModel.share(post.id)
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                }

                more.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_post)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
//                                     onInteractionListener.onRemove(post)
                                    viewModel.remove(post.id)
                                    findNavController().navigateUp()
                                    true
                                }
                                R.id.edit -> {
//                                     onInteractionListener.onEdit(post)
                                    viewModel.edit(post)
                                    findNavController().navigate(
                                        R.id.action_postFragment_to_newPostFragment
                                    )
                                    true
                                }
                                else -> false
                            }
                        }
                    }.show()
                }
            }
        }

        return binding.root
    }
}