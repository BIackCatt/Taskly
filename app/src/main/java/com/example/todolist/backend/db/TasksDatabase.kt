package com.example.todolist.backend.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todolist.backend.model.CollaborationDb
import com.example.todolist.backend.model.OfflineTask
import com.example.todolist.backend.model.Task

@Database(entities = [Task::class, OfflineTask::class, CollaborationDb::class], version = 2, exportSchema = false)
abstract class TasksDatabase : RoomDatabase() {
    abstract fun taskDAO(): TaskDao
    abstract fun offlineTasksDao(): OfflineTasksDao
    abstract fun collaborationDao(): CollaborationDao

    companion object {
        @Volatile
        var Instance: TasksDatabase? = null

        fun getDatabase(context: Context): TasksDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TasksDatabase::class.java,
                    "Tasks_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}