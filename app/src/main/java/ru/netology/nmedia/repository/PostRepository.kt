package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
//    fun getData() : List<Post>
//    fun like(id: Long, likedByMe: Boolean) : Post
//    fun remove(id: Long)
//    fun save(post: Post)

    val data: LiveData<List<Post>>
    suspend fun getData()
    suspend fun like(id: Long, likedByMe: Boolean): Post
    suspend fun remove(id: Long)
    suspend fun save(post: Post): Post
}