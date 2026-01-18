package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemory

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemory()

    val data = repository.getData()

    fun like() {
        repository.like()
    }

    fun share() {
        repository.share()
    }


}