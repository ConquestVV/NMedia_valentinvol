package ru.netology.nmedia.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data = dao.getData()
        .map(List<PostEntity>::toDto)

    override suspend fun getData() {
        try {
            val response = PostsApi.service.getData()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post): Post {
        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
            return body
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun remove(id: Long) {
        val post = dao.getById(id) ?: throw UnknownError
        dao.remove(id)

        try {
            val response = PostsApi.service.remove(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            dao.insert(post)
            throw NetworkError
        } catch (e: Exception) {
            dao.insert(post)
            throw UnknownError
        }
    }

    override suspend fun like(id: Long, likedByMe: Boolean): Post {
        dao.like(id)

        try {
            val response = if (likedByMe) {
                PostsApi.service.dislikeById(id)
            } else {
                PostsApi.service.likeById(id)
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
            return body
        } catch (e: IOException) {
            dao.like(id)
            throw NetworkError
        } catch (e: Exception) {
            dao.like(id)
            throw UnknownError
        }
    }

    override val newerCount: Flow<Int> = dao.getNewerCount()

    override suspend fun showNewer() {
        dao.showAllNewer()
    }

    override fun getNewer(id: Long): Flow<Int> = flow {
        var latestId = if (id != 0L) id else dao.maxId()

        while (true) {
            delay(10_000L)

            val response = PostsApi.service.getNewer(latestId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            val newPosts = body.filter { dao.countById(it.id) == 0 }
            if (newPosts.isNotEmpty()) {
                dao.insert(newPosts.toEntity(hidden = true))
                latestId = maxOf(latestId, newPosts.maxOf { it.id })
            }

            emit(newPosts.size)
        }
    }.catch { e -> throw AppError.from(e) }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            // TODO: add support for other types
            val postWithAttachment = post.copy(attachment = Attachment(
                media.id,
                AttachmentType.IMAGE
            )
            )
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = PostsApi.service.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}