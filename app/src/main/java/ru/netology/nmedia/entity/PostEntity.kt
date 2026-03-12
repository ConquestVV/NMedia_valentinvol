package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val shares: Int = 0,
    val watches: Int = 0,
    val likedByMe: Boolean = false,
    val sharedByMe: Boolean = false,
    val video: String? = null
) {
    fun toDto() = Post(id, author, content, published, likes, shares, watches, likedByMe, sharedByMe, video)

    companion object {
        fun fromDto(dto: Post) = PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likes, dto.shares, dto.watches, dto.likedByMe, dto.sharedByMe, dto.video)
    }
}