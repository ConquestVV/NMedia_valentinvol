package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import kotlin.concurrent.thread
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException

private val empty = Post(
    id = 0,
    author = "Нетология. Университет интернет-профессий будущего",
    content = "",
    published = 0,
    likes = 0,
    likedByMe = false
)
class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            _data.postValue(FeedModel(loading = true))
            try {
                val posts = repository.getData()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

    fun like(id: Long) {
        val post = data.value?.posts?.find { it.id == id } ?: return

        thread {
            repository.like(id, post.likedByMe)
            val posts = repository.getData()

            _data.postValue(
                FeedModel(
                    posts = posts,
                    empty = posts.isEmpty()
                )
            )
        }
    }

//    fun share(id: Long) {
//        repository.share(id)
//    }

    fun remove(id: Long) {
        repository.remove(id)
    }

    fun save(content: String) {
        thread {
            edited.value?.let {
                val text = content.trim()
                if (it.content != text) {
                    repository.save(it.copy(content = text))
                }
            }
            _postCreated.postValue(Unit)
            edited.postValue(empty)
        }
    }


    fun edit(post: Post) {
        edited.value = post
    }

    fun cancelEdit(){
        edited.value = empty
    }

}