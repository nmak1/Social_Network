package ru.netology.social_network.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.social_network.dto.User
import ru.netology.social_network.model.FeedModelState
import ru.netology.social_network.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _data = MutableLiveData<User>()
    val data: LiveData<User>
        get() = _data

    private val _state = MutableLiveData<FeedModelState>()
    val state: LiveData<FeedModelState>
        get() = _state

    fun loginAttempt(login: String?, password: String?) {
        viewModelScope.launch {
            try {
                val user = authRepository.authUser(login, password)
                _data.value = user
            } catch (e: Exception) {
                _state.postValue(FeedModelState(loginError = true))
            }
        }
    }
}