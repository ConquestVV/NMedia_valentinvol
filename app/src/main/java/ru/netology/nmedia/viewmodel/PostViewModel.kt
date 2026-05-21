package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
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
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao
    )
    private val _state = MutableLiveData(FeedModelState())
    val data: LiveData<FeedModel> = repository.data.map(::FeedModel)
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private var lastAction: (() -> Unit)? = null

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getData()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun like(id: Long) {
        val post = data.value?.posts?.find { it.id == id } ?: return
        lastAction = { like(id) }

        viewModelScope.launch {
            try {
                repository.like(id, post.likedByMe)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }

//        val post = data.value?.posts?.find { it.id == id } ?: return
//
//        repository.likeAsync(id, post.likedByMe, object : PostRepository.LikeCallback {
//            override fun onSuccess(updatedPost: Post) {
//                val newPosts = data.value?.posts.orEmpty()
//                    .map { if (it.id == updatedPost.id) updatedPost else it }
//
//                _state.postValue(
//                    FeedModel(
//                        posts = newPosts,
//                        empty = newPosts.isEmpty()
//                    )
//                )
//            }
//
//            override fun onError(e: Throwable) {
//                _state.postValue(FeedModel(error = true))
//            }
//        })
    }

    fun remove(id: Long) {
        lastAction = { remove(id) }

        viewModelScope.launch {
            try {
                repository.remove(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
//        _state.value = _state.value?.copy(loading = true, error = false)
//        repository.removeAsync(id, object : PostRepository.RemoveCallback {
//            override fun onSuccess() {
//                val newPosts = data.value?.posts?.filter { it.id != id }.orEmpty()
//
//                _state.postValue(
//                    FeedModel(
//                        posts = newPosts,
//                        empty = newPosts.isEmpty()
//                    )
//                )
//            }
//
//            override fun onError(e: Throwable) {
//                _state.postValue(FeedModel(error = true))
//            }
//        })
    }

    fun retry() {
        lastAction?.invoke()
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getData()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) return
        edited.value = edited.value?.copy(content = text)
    }
}