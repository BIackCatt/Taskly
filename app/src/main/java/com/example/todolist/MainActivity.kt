package com.example.todolist

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.glance.appwidget.updateAll
import com.example.compose.ToDoListTheme
import com.example.todolist.experiments.notifications.TasksNotificationsManager
import com.example.todolist.main.TodoAppEntry
import com.example.todolist.experiments.widget.TasksWidget
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            ToDoListTheme(dynamicColor = false) {

                val accessPermission =
                    rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
                val readAccessper =
                    rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

                val tasksNotificationsManager = TasksNotificationsManager(this)

                LaunchedEffect(key1 = true) {
                    if (!accessPermission.status.isGranted) {
                        accessPermission.launchPermissionRequest()
                    }
                    if (!readAccessper.status.isGranted) {
                        readAccessper.launchPermissionRequest()
                    }
                }
                Surface {
                    TodoAppEntry(
                        notificationsManager = tasksNotificationsManager,
                    )
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            TasksWidget().updateAll(applicationContext)
        }
    }
}

