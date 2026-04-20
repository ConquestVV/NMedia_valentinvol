package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    author = "Нетология. Университет интернет-профессий будущего",
    content = "",
    published = 0,
    likes = 0,
    likedByMe = false,
    authorAvatar = "netology.jpg",
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
        _data.value = FeedModel(loading = true, error = false, empty = false)

        repository.getDataAsync(object : PostRepository.GetDataCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(
                    posts = posts,
                    loading = false,
                    error = false,
                    empty = posts.isEmpty()
                ))
            }

            override fun onError(e: Throwable) {
                _data.postValue(FeedModel(
                    loading = false,
                    error = true,
                    empty = false
                ))
            }
        })
    }

    fun like(id: Long) {
        val post = data.value?.posts?.find { it.id == id } ?: return

        repository.likeAsync(id, post.likedByMe, object : PostRepository.LikeCallback {
            override fun onSuccess(updatedPost: Post) {
                val newPosts = data.value?.posts.orEmpty()
                    .map { if (it.id == updatedPost.id) updatedPost else it }

                _data.postValue(
                    FeedModel(
                        posts = newPosts,
                        empty = newPosts.isEmpty()
                    )
                )
            }

            override fun onError(e: Throwable) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun remove(id: Long) {
        _data.value = _data.value?.copy(loading = true, error = false)
        repository.removeAsync(id, object : PostRepository.RemoveCallback {
            override fun onSuccess() {
                val newPosts = data.value?.posts?.filter { it.id != id }.orEmpty()

                _data.postValue(
                    FeedModel(
                        posts = newPosts,
                        empty = newPosts.isEmpty()
                    )
                )
            }

            override fun onError(e: Throwable) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (it.content != text) {
                repository.saveAsync(it.copy(content = text), object : PostRepository.SaveCallback {
                    override fun onSuccess(post: Post) {
                        _postCreated.postValue(Unit)
                        edited.postValue(empty)
                    }

                    override fun onError(e: Throwable) {
                        _data.value = _data.value?.copy(error = true)
                    }
                })
            } else {
                _postCreated.postValue(Unit)
                edited.postValue(empty)
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }
}