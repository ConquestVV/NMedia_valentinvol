package ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.netology.nmedia.R
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedBinding.inflate(layoutInflater)

        val viewModel: PostViewModel by activityViewModels()

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    bundleOf("content" to post.content)
                )
//                newPostLauncher.launch(post)
            }

            override fun onLike(post: Post) {
                viewModel.like(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.remove(post.id)
            }

            override fun onVideoClick(video: String) {
                runCatching {
                    startActivity(Intent(Intent.ACTION_VIEW, video.toUri()))
                }
            }

            override fun onPostClick(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    bundleOf("postId" to post.id)
                )
            }
        })
        binding.list.adapter = adapter

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.retry() }
                    .show()
            }
        }
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }

//        viewModel.data.observe(viewLifecycleOwner) { state ->
//            adapter.submitList(state.posts)
//            binding.progress.isVisible = state.loading
//            binding.list.isVisible = !state.loading && !state.error
//            binding.errorGroup.isVisible = state.error
//            binding.emptyText.isVisible = state.empty
//        }

        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }
}