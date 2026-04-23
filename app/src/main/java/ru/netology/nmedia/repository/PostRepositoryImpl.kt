package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.UnknownError

class PostRepositoryImpl: PostRepository {


    override fun getData(): List<Post> {
        try {
            val response = PostsApi.service.getData().execute()
            if (!response.isSuccessful) {
                throw UnknownError()
            }
            return response.body().orEmpty()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override fun getDataAsync(callback: PostRepository.GetDataCallback) {
        PostsApi.service.getData().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                try {
                    if (!response.isSuccessful) {
                        callback.onError(UnknownError())
                        return
                    }

                    callback.onSuccess(response.body() ?: throw UnknownError())
                } catch (e: Exception) {
                    callback.onError(UnknownError())
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(UnknownError())
            }
        })
    }

    override fun like(id: Long, likedByMe: Boolean) : Post{
        try {
            val response = if (likedByMe) {
                PostsApi.service.dislikeById(id).execute()
            } else {
                PostsApi.service.likeById(id).execute()
            }
            if (!response.isSuccessful) {
                throw UnknownError()
            }
            return response.body() ?: throw UnknownError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override fun likeAsync(
        id: Long,
        likedByMe: Boolean,
        callback: PostRepository.LikeCallback
    ) {
        val call = if (likedByMe) {
            PostsApi.service.dislikeById(id)
        } else {
            PostsApi.service.likeById(id)
        }

        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                try {
                    if (!response.isSuccessful) {
                        callback.onError(UnknownError("Server error: ${response.code()}"))
                        return
                    }

                    callback.onSuccess(response.body() ?: throw UnknownError())
                } catch (e: Exception) {
                    callback.onError(UnknownError())
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(UnknownError())
            }
        })
    }

    override fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post).execute()
            if (!response.isSuccessful) {
                throw UnknownError("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override fun saveAsync(
        post: Post,
        callback: PostRepository.SaveCallback
    ) {
        PostsApi.service.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                try {
                    if (!response.isSuccessful) {
                        callback.onError(UnknownError("Server error: ${response.code()}"))
                        return
                    }

                    callback.onSuccess(response.body() ?: throw UnknownError())
                } catch (e: Exception) {
                    callback.onError(UnknownError())
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(UnknownError())
            }
        })
    }

    override fun remove(id: Long) {
        try {
            val response = PostsApi.service.remove(id).execute()
            if (!response.isSuccessful) {
                throw UnknownError("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override fun removeAsync(
        id: Long,
        callback: PostRepository.RemoveCallback
    ) {
        PostsApi.service.remove(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                try {
                    if (!response.isSuccessful) {
                        callback.onError(UnknownError("Server error: ${response.code()}"))
                        return
                    }

                    callback.onSuccess()
                } catch (e: Exception) {
                    callback.onError(UnknownError())
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(UnknownError())
            }
        })
    }


}