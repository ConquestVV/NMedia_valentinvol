package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.dto.Post

object NewPostResultContract :
    ActivityResultContract<Post?, Pair<Long, String>?>() {

    override fun createIntent(context: Context, input: Post?): Intent {
        return Intent(context, NewPostActivity::class.java).apply {
            putExtra("postId", input?.id ?: 0L)
            putExtra(Intent.EXTRA_TEXT, input?.content ?: "")
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<Long, String>? {
        if (resultCode != Activity.RESULT_OK || intent == null) return null

        val id = intent.getLongExtra("postId", 0L)
        val content = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return null

        return id to content
    }
}