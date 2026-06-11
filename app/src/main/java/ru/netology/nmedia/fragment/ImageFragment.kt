package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import ru.netology.nmedia.databinding.FragmentImageBinding

class ImageFragment : Fragment() {

    private var binding: FragmentImageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentImageBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        val imageUrl = requireArguments().getString(ARG_URL).orEmpty()

        fragmentBinding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        fragmentBinding.image.load(imageUrl)

        return fragmentBinding.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_URL = "imageUrl"
    }
}