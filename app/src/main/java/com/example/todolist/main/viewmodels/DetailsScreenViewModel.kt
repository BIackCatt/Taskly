package com.example.todolist.main.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.todolist.backend.db.TasksRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DetailsScreenViewModel: ViewModel() {
    private val _currentTask = MutableStateFlow<TaskUiState?>(null)
    val currentTask = _currentTask.asStateFlow()

    fun updateCurrentTask(task: TaskUiState) {
        _currentTask.update { task }
    }

    fun resetState() { _currentTask.update { null } }
}