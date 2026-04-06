package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getData() : List<Post>
    fun like(id: Long, likedByMe: Boolean)
//    fun share(id: Long)
    fun remove(id: Long)
    fun save(post: Post)


}