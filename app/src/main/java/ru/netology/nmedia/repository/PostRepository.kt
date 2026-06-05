package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    val newerCount: Flow<Int>
    fun getNewer(id: Long): Flow<Int>
    suspend fun showNewer()
    suspend fun getData()
    suspend fun like(id: Long, likedByMe: Boolean): Post
    suspend fun remove(id: Long)
    suspend fun save(post: Post): Post
}