package ru.netology.social_network.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.social_network.api.UserApiService
import ru.netology.social_network.dto.User
import ru.netology.social_network.model.StateModel
import ru.netology.social_network.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userApiService: UserApiService,
) : ViewModel() {

    val data: LiveData<List<User>> =
        userRepository.data
            .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _userIds = MutableLiveData<Set<Long>>()
    val userIds: LiveData<Set<Long>>
        get() = _userIds

    init {
        getUsers()
    }

    private fun getUsers() = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            userRepository.getAll()
            _dataState.postValue(StateModel())
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun getUserById(id: Long) = viewModelScope.launch {
        _dataState.postValue(StateModel(loading = true))
        try {
            val response = userApiService.getUserById(id)
            if (response.isSuccessful) {
                _user.value = response.body()
            }
            _dataState.postValue(StateModel())
        } catch (e: Exception) {
            _dataState.postValue(StateModel(error = true))
        }
    }

    fun getUsersIds(set: Set<Long>) =
        viewModelScope.launch {
            _userIds.value = set
        }
}