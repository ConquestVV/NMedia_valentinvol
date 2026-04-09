package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getData(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun getDataAsync(callback: PostRepository.GetDataCallback) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val posts = response.body.string()
                        callback.onSuccess(gson.fromJson(posts, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun like(id: Long, likedByMe: Boolean) : Post{
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts/$id/likes")
            .let{
                if (likedByMe) {
                    it.delete()
                } else {
                    it.post("".toRequestBody(null))
                }
            }
            .build()

        return client.newCall(request)
            .execute()
            .use { response ->
                val body = response.body?.string() ?: throw RuntimeException("body is null")
                gson.fromJson(body, Post::class.java)
            }

    }

    override fun likeAsync(
        id: Long,
        likedByMe: Boolean,
        callback: PostRepository.LikeCallback
    ) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts/$id/likes")
            .let{
                if (likedByMe) {
                    it.delete()
                } else {
                    it.post("".toRequestBody(null))
                }
            }
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                response.use {
                    try {
                        val post = response.body?.string()
                            ?: throw RuntimeException("Response body is null")
                        callback.onSuccess(gson.fromJson(post, Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
        })
    }

    override fun save(post: Post){
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()

        return client.newCall(request)
            .execute()
            .close()

    }

    override fun saveAsync(
        post: Post,
        callback: PostRepository.SaveCallback
    ) {
        val request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val post = response.body?.string()
                    if (post == null) {
                        callback.onError(RuntimeException("Response body is null"))
                        return
                    }

                    val savedPost = gson.fromJson(post, Post::class.java)
                    callback.onSuccess(savedPost)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
        })
    }

    override fun remove(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeAsync(
        id: Long,
        callback: PostRepository.RemoveCallback
    ) {
        val request = Request.Builder()
            .url("${BASE_URL}/api/posts/$id")
            .delete()
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    callback.onSuccess()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
        })
    }


}