package ru.netology.nmedia

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
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

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likes = 10,
            shares = 5,
            watches = 34,
            likedByMe = false,
            sharedByMe = false
        )

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

        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            if (post.likedByMe) {
                likeImg?.setImageResource(R.drawable.ic_liked_24)
            }
            likeCount?.text = formatCount(post.likes)

            likeImg?.setOnClickListener {
                post.likedByMe = !post.likedByMe
                likeImg.setImageResource(
                    if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
                )
                if (post.likedByMe) post.likes++ else post.likes--
                likeCount?.text = formatCount(post.likes)
            }

            shareCount?.text = formatCount(post.shares)

            shareImg?.setOnClickListener {
                post.sharedByMe = !post.sharedByMe
                post.shares++
                shareCount?.text = formatCount(post.shares)
            }

            watchersCount?.text = formatCount(post.watches)
        }

    }
}