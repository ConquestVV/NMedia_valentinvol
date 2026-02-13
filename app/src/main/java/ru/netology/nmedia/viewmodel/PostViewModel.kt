package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryFilesImpl
import ru.netology.nmedia.repository.PostRepositoryInMemory
import ru.netology.nmedia.repository.PostRepositorySharedPrefsImpl

private val empty = Post(
    id = 0,
    author = "Нетология. Университет интернет-профессий будущего",
    content = "",
    published = "Сейчас",
    likes = 0,
    likedByMe = false
)
class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryFilesImpl(application)

    val data = repository.getData()

    fun like(id: Long) {
        repository.like(id)
    }

    fun share(id: Long) {
        repository.share(id)
    }

    fun remove(id: Long) {
        repository.remove(id)
    }

    val edited = MutableLiveData(empty)

    fun save(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (it.content != text) {
                repository.save(it.copy(content = text))
            }
        }
        edited.value = empty
    }

//    fun edit(post: Post) {
//        edited.value = post
//    }

    fun edit(id: Long, content: String) {
        val text = content.trim()
        if (text.isBlank()) return

        val post = data.value?.find { it.id == id } ?: return
        repository.save(post.copy(content = text))
    }

    fun cancelEdit(){
        edited.value = empty
    }

}