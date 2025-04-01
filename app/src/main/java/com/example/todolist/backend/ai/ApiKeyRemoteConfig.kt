package com.example.todolist.backend.ai

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig

class ApiKeyRemoteConfig {
    private val remoteConfig = Firebase.remoteConfig

    init {
        remoteConfig.fetchAndActivate()
    }

    fun getAIApiKey(): String {
        return remoteConfig.getString("ai_api_key")
    }
}