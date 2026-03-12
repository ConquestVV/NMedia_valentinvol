package ru.netology.nmedia.repository

import androidx.lifecycle.map
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

class PostRepositoryRoomImpl(
    private val dao: PostDao,
) : PostRepository {

    override fun getData() = dao.getData().map { list ->
        list.map {
            it.toDto()
        }
    }

    override fun like(id: Long) {
        dao.like(id)
    }

    override fun share(id: Long) {
        dao.share(id)
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override fun remove(id: Long) {
        dao.remove(id)
    }
}