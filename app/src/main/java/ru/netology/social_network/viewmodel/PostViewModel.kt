package ru.netology.social_network.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.social_network.auth.AppAuth
import ru.netology.social_network.dto.MediaUpload
import ru.netology.social_network.dto.Post
import ru.netology.social_network.enumeration.AttachmentType
import ru.netology.social_network.model.MediaModel
import ru.netology.social_network.model.StateModel
import ru.netology.social_network.repository.PostRepository
import ru.netology.social_network.untils.SingleLiveEvent
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "2023-10-27T17:00:00.000Z",
    mentionedMe = false,
    likedByMe = false,
)

private val noMedia = MediaModel()

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {

    private val cached = postRepository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> =
        appAuth.authStateFlow
            .flatMapLatest { (myId, _) ->
                cached.map { pagingData ->
                    pagingData.map { post ->
                        post.copy(
                            ownedByMe = post.authorId == myId,
                            likedByMe = post.likeOwnerIds.contains(myId)
                        )
                    }
                }
            }

    val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _media = MutableLiveData(noMedia)
    val media: LiveData<MediaModel>
        get() = _media

    private val scope = MainScope()

    fun save() {
        edited.value?.let { post ->
            viewModelScope.launch {
                _dataState.postValue(StateModel(loading = true))
                try {
                    when (_media.value) {
                        noMedia ->
                            postRepository.savePost(post)
                        else ->
                            _media.value?.inputStream?.let {
                                MediaUpload(it)
                            }?.let {
                                postRepository.saveWithAttachment(post, it, _media.value?.type!!)
                            }
                    }
                    _dataState.value = StateModel()
                    _postCreated.value = Unit
                } catch (e: IOException) {
                    _dataState.postValue(StateModel(error = true))
                } catch (e: Exception) {
                    throw UnknownError()
                }
            }
        }
        edited.value = empty
        _media.value = noMedia
    }

    fun changeContent(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (edited.value?.content != text) {
                edited.value = edited.value?.copy(content = text)
            }
        }
    }

    fun changeMedia(
        uri: Uri?,
        inputStream: InputStream?,
        type: AttachmentType?,
    ) {
        _media.value = MediaModel(uri, inputStream, type)
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            postRepository.removeById(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            postRepository.likeById(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun unlikeById(id: Long) = viewModelScope.launch {
        try {
            postRepository.unlikeById(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun changeMentionIds(id: Long) {
        edited.value?.let {
            if (edited.value?.mentionIds?.contains(id) == false) {
                edited.value = edited.value?.copy(
                    mentionIds = it.mentionIds.plus(id)
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}

