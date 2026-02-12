package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import ru.netology.nmedia.util.AndroidUtils


class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val content = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (content != null) {
            binding.edit.setText(content)
            binding.edit.setSelection(content.length)
        }

        AndroidUtils.showKeyboard(binding.edit)
        binding.add.setOnClickListener {
            if (binding.edit.text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                val postId = intent.getLongExtra("postId", 0L)
                val content = binding.edit.text.toString()
                val intent = Intent().apply {
                    putExtra(Intent.EXTRA_TEXT, content)
                    putExtra("postId", postId)

                }

                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
    }
}