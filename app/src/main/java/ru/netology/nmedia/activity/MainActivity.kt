package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                v.paddingLeft + systemBars.left,
                v.paddingTop + systemBars.top,
                v.paddingRight + systemBars.right,
                v.paddingBottom + systemBars.bottom)
            insets
        }

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

        viewModel.data.observe(this) { post ->
            with(binding) {
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
            }
        }

        binding.likeImg?.setOnClickListener {
            viewModel.like()
        }

        binding.shareImg?.setOnClickListener {
            viewModel.share()
        }
    }
}