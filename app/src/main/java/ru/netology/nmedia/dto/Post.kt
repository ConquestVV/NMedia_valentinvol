package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likes: Int = 0,
    var shares: Int = 0,
    var watches: Int = 0,
    var likedByMe: Boolean = false,
    var sharedByMe: Boolean = false
)
