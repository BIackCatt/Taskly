package com.example.todolist.experiments.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import com.example.todolist.R
import com.example.todolist.backend.db.TasksDatabase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TasksWidget : GlanceAppWidget() {
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val tasksDao = TasksDatabase.getDatabase(context).offlineTasksDao()
        val tasks = tasksDao.getAllTasks().stateIn(scope)
        provideContent {
            GlanceTheme {
                Scaffold(
                    titleBar = {
                        TitleBar(
                            startIcon = ImageProvider(R.drawable.google_logo),
                            title = "Welcome glance"
                        )
                    },
                    backgroundColor = GlanceTheme.colors.widgetBackground
                ) {
                    val tasksState by tasks.collectAsState()
                    LazyColumn {
                        items(tasksState) { task ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = GlanceModifier.padding(8.dp)
                            ) {
                                CheckBox(
                                    checked = task?.isCompleted?: false,
                                    onCheckedChange = { task?.let {
                                        val gsonTask = Gson().toJson(task)
                                        scope.launch {
                                            tasksDao.update(task.copy(isCompleted = !task.isCompleted))
                                        }
                                    }?: Log.e("TaskDaoWidget", "Task is null")
                                })
                                androidx.glance.layout.Spacer(GlanceModifier.padding(10.dp))
                                Text(
                                    task?.title ?: "No title",
                                    style = TextStyle(
                                        color = GlanceTheme.colors.onSurface,
                                        textDecoration = if (task!!.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

    }

}

