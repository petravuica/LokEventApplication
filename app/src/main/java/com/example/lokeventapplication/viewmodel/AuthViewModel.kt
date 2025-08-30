package com.example.lokeventapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokeventapplication.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        _authState.value = if (authRepository.isAuthenticated()) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email i lozinka ne smiju biti prazni")
            return
        }
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.Authenticated
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Greška pri prijavi")
            }
        }
    }

    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email i lozinka ne smiju biti prazni")
            return
        }
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.signup(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.Authenticated
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Greška pri registraciji")
            }
        }
    }

    fun signout() {
        authRepository.logout()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
