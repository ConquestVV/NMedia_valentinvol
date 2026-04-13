package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getData() : List<Post>
    fun like(id: Long, likedByMe: Boolean) : Post
    fun remove(id: Long)
    fun save(post: Post)

    fun getDataAsync(callback: GetDataCallback)
    fun likeAsync(id: Long, likedByMe: Boolean, callback: LikeCallback)
    fun removeAsync(id: Long, callback: RemoveCallback)
    fun saveAsync(post: Post, callback: SaveCallback)

    interface GetDataCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    interface LikeCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }

    interface RemoveCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }

    interface SaveCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }
}