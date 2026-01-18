package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val shares: Int = 0,
    val watches: Int = 0,
    val likedByMe: Boolean = false,
    val sharedByMe: Boolean = false
)
