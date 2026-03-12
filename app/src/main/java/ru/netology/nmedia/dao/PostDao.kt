package ru.netology.nmedia.dao

import ru.netology.nmedia.dto.Post

interface PostDao {
    fun getData(): List<Post>
    fun save(post: Post): Post
    fun like(id: Long)
    fun share(id: Long)
    fun remove(id: Long)
}