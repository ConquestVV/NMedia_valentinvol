package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemory

private val empty = Post(
    id = 0,
    author = "Нетология. Университет интернет-профессий будущего",
    content = "",
    published = "Сейчас",
    likes = 0,
    likedByMe = false
)
class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemory()

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

    fun edit(post: Post) {
        edited.value = post
    }

}