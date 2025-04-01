package com.example.todolist.backend.auth

data class SignInState (
    val isSignInSuccessful: Boolean = false,
    val signInErrorMessage: String? = null,
    )