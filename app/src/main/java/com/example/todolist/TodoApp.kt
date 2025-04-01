package com.example.todolist

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.todolist.backend.model.AppContainer
import com.example.todolist.backend.model.DefaultAppContainer



class TodoApp: Application() {

    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        val notificationChannel = NotificationChannel(
            "Tasks_notifications",
            "Tasks",
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationChannel.description = "Tasks reminder"

        val notificationsManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationsManager.createNotificationChannel(notificationChannel)
    }
}
