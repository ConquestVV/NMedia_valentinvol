package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.getValue


class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentNewPostBinding.inflate(layoutInflater)

        arguments?.textArg?.let {
            binding.edit.setText(it)
            binding.edit.setSelection(it.length)
        }

        viewModel.edited.observe(viewLifecycleOwner) { post ->
            if (post.id == 0L) return@observe

            binding.edit.setText(post.content)
            binding.edit.setSelection(post.content.length)
        }

        AndroidUtils.showKeyboard(binding.edit)

        binding.add.setOnClickListener {
            if (!binding.edit.text.isNullOrBlank()) {
                viewModel.changeContent(binding.edit.text.toString())
                viewModel.save()
                AndroidUtils.hideKeyboard(requireView())
            }
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }


        return binding.root
    }
}