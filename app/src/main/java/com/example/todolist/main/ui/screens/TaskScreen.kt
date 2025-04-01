package com.example.todolist.main.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.example.todolist.main.viewmodels.TaskUiState
import com.example.todolist.utils.getFormattedDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    task: TaskUiState,
    onSave: (title: String, description: String) -> Unit,
    onNavBack: () -> Unit,
) {
    var title by rememberSaveable { mutableStateOf(task.title) }
    var description by rememberSaveable { mutableStateOf(task.description) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = "Edit task",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,

                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                onSave(title, description)
                            },
                            modifier = Modifier.padding(10.dp),
                        ) {
                            Text("Save")
                        }
                    }

                },
                navigationIcon = {
                    IconButton(onClick = onNavBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                            contentDescription = "Back"
                        )
                    }
                },

                )
        },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            item {
                TaskTitleView(
                    title = title,
                    onTitleChange = { title = it },
                    date = task.date,
                    descriptionSize = description.trim().replace(" ", "").length
                )
            }

            item {
                TaskDescriptionView(
                    description = description,
                    onDescriptionChange = { description = it }
                )
            }

        }
    }

}

@Composable
fun TaskTitleView(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    descriptionSize: Int,
    date: String,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    BasicTextField(
                        value = title,
                        onValueChange = onTitleChange,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        singleLine = false,
                        textStyle = TextStyle(
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                            textDirection = TextDirection.ContentOrLtr,
                            textAlign = TextAlign.Center,
                        )
                    )
                }
            }
        }
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = if (date == getFormattedDate()) "Today" else date,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
                    modifier = Modifier.padding(10.dp)
                )
                VerticalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "$descriptionSize ${if (descriptionSize <= 1) "character" else "characters"}",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
                )
            }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), thickness = 1.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun TaskDescriptionView(
    modifier: Modifier = Modifier,
    description: String,
    onDescriptionChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            cursorBrush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.99f))),
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            value = description,
            onValueChange = onDescriptionChange,
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                textDirection = TextDirection.ContentOrLtr,
            )
        )
        Box(
            contentAlignment = Alignment.Center
        ) {
            Row {
                Icon(
                    imageVector = Icons.Filled.KeyboardDoubleArrowUp,
                    contentDescription = "Click up there"
                )
                Text(
                    "Click there"
                )
            }
        }
    }
}
