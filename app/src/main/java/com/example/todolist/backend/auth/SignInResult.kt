package com.example.todolist.backend.auth


data class SignInResult (
    val data: AuthenticatedUserData?,
    val errorMessage: String? = null
)

data class AuthenticatedUserData (
    val userId: String? = null,
    val username: String? = null,
    val profilePictureUrl: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val collaborations: MutableList<String?> = mutableListOf()
)