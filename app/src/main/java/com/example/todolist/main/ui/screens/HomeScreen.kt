package com.example.todolist.main.ui.screens

import StackedSnakbarHostState
import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.ToDoListTheme
import com.example.todolist.backend.auth.AuthenticatedUserData
import com.example.todolist.main.viewmodels.HomeScreenViewModel
import com.example.todolist.main.viewmodels.LoadingControl
import com.example.todolist.main.viewmodels.TaskUiState
import com.example.todolist.main.viewmodels.UserAccountViewModel
import com.example.todolist.main.ui.components.primary.AddTaskDialog
import com.example.todolist.main.ui.components.primary.BottomNavigation
import com.example.todolist.main.ui.components.secondry.FetchingDataScreen
import com.example.todolist.main.ui.components.primary.GoogleButtonIcon
import com.example.todolist.utils.getFormattedDate
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import java.util.Locale


@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen(
    // params
    isConnected: Boolean,
    homeScreenViewModel: HomeScreenViewModel,
    userAccountViewModel: UserAccountViewModel,
    stackedSnackbarState: StackedSnakbarHostState,
    // lambdas
    onClick: (TaskUiState) -> Unit,
    onGFCClicked: () -> Unit,
    backHandler: () -> Unit,
    // composable
    topBar: @Composable () -> Unit
) {
    BackHandler {
        backHandler()
    }
    val loadingState by homeScreenViewModel.isLoading.collectAsStateWithLifecycle()
    when (loadingState) {
        is LoadingControl.FetchingData -> {
            FetchingDataScreen()
        }

        is LoadingControl.Success -> {
            SuccessHomeScreen(
                homeScreenViewModel = homeScreenViewModel,
                onClick = onClick,
                onGoogleFapClicked = onGFCClicked,
                userAccountViewModel = userAccountViewModel,
                isConnected = isConnected,
                topBar = topBar,
                stackedSnackbarState = stackedSnackbarState
            )
        }

        is LoadingControl.Error -> {}
    }
}


@OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
@Composable
fun SuccessHomeScreen(
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel,
    userAccountViewModel: UserAccountViewModel,
    stackedSnackbarState: StackedSnakbarHostState,
    onClick: (TaskUiState) -> Unit,
    onGoogleFapClicked: () -> Unit,
    isConnected: Boolean,
    topBar: @Composable () -> Unit,
) {
    // states
    val userAccount by userAccountViewModel.userData.collectAsState()
    val homeUiState by homeScreenViewModel.homeUiState.collectAsState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0)

    // variables
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var isEdit by rememberSaveable { mutableStateOf(false) }
    var task by remember { mutableStateOf<TaskUiState?>(null) }
    var pageCountIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            topBar()
        },
        bottomBar = {
            BottomNavigation(
                pageIndex = pageCountIndex,
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(it)
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                if (userAccount == null) {
                    GoogleButtonIcon(
                        size = 50,
                        onClick = {
                            onGoogleFapClicked()
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(bottom = 10.dp)
                    )
                }
                AddTaskFAB(
                    onClick = {
                        isEdit = false
                        task = null
                        showDialog = true
                    },
                    showDialog = showDialog,
                    color = Color(0xFF6A0DAD)
                )
            }
        }
    ) { innerPadding ->
        println(innerPadding)

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                pageCountIndex = page
            }
        }
        HorizontalPager(
            count = 3,
            state = pagerState,
            itemSpacing = 20.dp,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalAlignment = Alignment.Top

        ) { pageIndex ->
            var title by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }
            Log.d("Tasks", homeUiState.tasks.toString())
            val completedTasks = homeUiState.tasks.filter { taskUiState ->
                taskUiState?.isCompleted == true
            }
            val uncompletedTasks = homeUiState.tasks.filter { taskUiState ->
                taskUiState?.isCompleted == false
            }

            Box {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    item {
                        when (pageIndex) {
                            0 -> {
                                TasksList(
                                    userAccount = userAccount,
                                    isEmpty = uncompletedTasks.isEmpty(),
                                    isCompleted = false,
                                    tasksList = uncompletedTasks,
                                    onCheckedChange = {
                                        scope.launch {
                                            if (userAccount != null) {
                                                homeScreenViewModel.updateTask(
                                                    isConnected = isConnected,
                                                    task = it.copy(isCompleted = !it.isCompleted),
                                                    userId = userAccount!!.userId,
                                                    onError = { error ->
                                                        stackedSnackbarState.showErrorSnackbar(
                                                            title = "Something went wrong!",
                                                            duration = StackedSnackbarDuration.Short
                                                        )

                                                    },
                                                    onSuccessAction = {
                                                        stackedSnackbarState.showSuccessSnackbar(
                                                            title = "\"${it.title}\" ${if (it.isCompleted) "Uncompleted" else "Completed"}",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    }
                                                )
                                            } else {
                                                homeScreenViewModel.updateOfflineTask(
                                                    it.copy(
                                                        isCompleted = !it.isCompleted
                                                    )
                                                )
                                                stackedSnackbarState.showSuccessSnackbar(
                                                    title = "\"${it.title}\" ${if (it.isCompleted) "Uncompleted" else "Completed"}",
                                                    duration = StackedSnackbarDuration.Short
                                                )
                                            }
                                        }
                                    },
                                    onClick = onClick,
                                    onEditClick = {
                                        isEdit = true
                                        task = it
                                        showDialog = true
                                        title = it.title
                                        description = it.description
                                    },
                                    onDeleteClick = {
                                        scope.launch {
                                            if (userAccount != null) {
                                                homeScreenViewModel.deleteTask(
                                                    isConnected = isConnected,
                                                    task = it,
                                                    userId = userAccount!!.userId,
                                                    onError = { error ->
                                                        stackedSnackbarState.showErrorSnackbar(
                                                            title = "Something went wrong!",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    onSuccessAction = {
                                                        stackedSnackbarState.showSuccessSnackbar(
                                                            title = "Deleting \"${it.title}\" successfully!"
                                                        )
                                                    }
                                                )
                                            } else {
                                                homeScreenViewModel.deleteOfflineTask(it)
                                                stackedSnackbarState.showSuccessSnackbar(
                                                    title = "Deleting \"${it.title}\" successfully!"
                                                )
                                            }


                                        }
                                    },
                                    onSyncClick = { task ->

                                        homeScreenViewModel.updateTask(
                                            task = task,
                                            userId = userAccount!!.userId,
                                            onError =  { error ->
                                                stackedSnackbarState.showErrorSnackbar(
                                                    title = "Something went wrong!",
                                                    duration = StackedSnackbarDuration.Short
                                                )
                                            },
                                            isConnected = isConnected,
                                            onSuccessAction = {
                                                stackedSnackbarState.showSuccessSnackbar(
                                                    title = "\"${task.title}\" synced successfully!"
                                                )
                                            }

                                        )

                                    }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                TasksList(
                                    userAccount = userAccount,
                                    isEmpty = completedTasks.isEmpty(),
                                    isCompleted = true,
                                    tasksList = completedTasks,
                                    onCheckedChange = {
                                        scope.launch {
                                            if (userAccount != null) {
                                                homeScreenViewModel.updateTask(
                                                    isConnected = isConnected,
                                                    task = it.copy(isCompleted = !it.isCompleted),
                                                    userId = userAccount!!.userId,
                                                    onError = { error ->
                                                        stackedSnackbarState.showErrorSnackbar(
                                                            title = "Something went wrong!",
                                                            duration = StackedSnackbarDuration.Short
                                                        )

                                                    },
                                                    onSuccessAction = {
                                                        stackedSnackbarState.showSuccessSnackbar(
                                                            title = "\"${it.title}\" ${if (it.isCompleted) "Uncompleted" else "Completed"}",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    }
                                                )
                                            } else {
                                                homeScreenViewModel.updateOfflineTask(
                                                    it.copy(
                                                        isCompleted = !it.isCompleted
                                                    )
                                                )
                                                stackedSnackbarState.showSuccessSnackbar(
                                                    title = "\"${it.title}\" ${if (it.isCompleted) "Uncompleted" else "Completed"}",
                                                    duration = StackedSnackbarDuration.Short
                                                )
                                            }
                                        }
                                    },
                                    onClick = onClick,
                                    onEditClick = {
                                        isEdit = true
                                        task = it
                                        showDialog = true
                                        title = it.title
                                        description = it.description
                                    },
                                    onDeleteClick = {
                                        scope.launch {
                                            if (userAccount != null) {
                                                homeScreenViewModel.deleteTask(
                                                    isConnected = isConnected,
                                                    task = it,
                                                    userId = userAccount!!.userId,
                                                    onError = { error ->
                                                        stackedSnackbarState.showErrorSnackbar(
                                                            title = "Something went wrong!",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    onSuccessAction = {
                                                        stackedSnackbarState.showSuccessSnackbar(
                                                            title = "Deleting \"${it.title}\" successfully!"
                                                        )
                                                    }
                                                )
                                            } else {
                                                homeScreenViewModel.deleteOfflineTask(it)
                                                stackedSnackbarState.showSuccessSnackbar(
                                                    title = "Deleting \"${it.title}\" successfully!"
                                                )
                                            }


                                        }
                                    },
                                    onSyncClick = { task ->

                                        homeScreenViewModel.updateTask(
                                            task = task,
                                            userId = userAccount!!.userId,
                                            onError =  { error ->
                                                stackedSnackbarState.showErrorSnackbar(
                                                    title = "Something went wrong!",
                                                    duration = StackedSnackbarDuration.Short
                                                )
                                            },
                                            isConnected = isConnected,
                                            onSuccessAction = {
                                                stackedSnackbarState.showSuccessSnackbar(
                                                    title = "\"${task.title}\" synced successfully!"
                                                )
                                            }

                                        )

                                    }
                                )
                            }

                            else -> {
                                TasksList(
                                    userAccount = userAccount,
                                    isEmpty = if (pageIndex == 1) completedTasks.isEmpty() else uncompletedTasks.isEmpty(),
                                    isCompleted = pageIndex == 1,
                                    tasksList = if (pageIndex == 1) completedTasks else uncompletedTasks,
                                    onCheckedChange = {
                                        scope.launch {
                                            if (userAccount != null) {
                                                homeScreenViewModel.updateTask(
                                                    isConnected = isConnected,
                                                    task = it.copy(isCompleted = !it.isCompleted),
                                                    userId = userAccount!!.userId,
                                                    onError = { error ->
                                                        stackedSnackbarState.showErrorSnackbar(
                                                            title = "Something went wrong!",
                                                            duration = StackedSnackbarDuration.Short
                                                        )

                                                    },
                                                    onSuccessAction = {
                                                        stackedSnackbarState.showSuccessSnackbar(
                                                            title = "\"${it.title}\" ${if (it.isCompleted) "Uncompleted" else "Completed"}",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    }
                                                )
                                            } else {
                                                homeScreenViewModel.updateOfflineTask(
                                                    it.copy(
                                                        isCompleted = !it.isCompleted
                                                    )
                                                )
                                                stackedSnackbarState.showSuccessSnackbar(
                                                    title = "\"${it.title}\" ${if (it.isCompleted) "Uncompleted" else "Completed"}",
                                                    duration = StackedSnackbarDuration.Short
                                                )
                                            }
                                        }
                                    },
                                    onClick = onClick,
                                    onEditClick = {
                                        isEdit = true
                                        task = it
                                        showDialog = true
                                        title = it.title
                                        description = it.description
                                    },
                                    onDeleteClick = {
                                        scope.launch {
                                            if (userAccount != null) {
                                                homeScreenViewModel.deleteTask(
                                                    isConnected = isConnected,
                                                    task = it,
                                                    userId = userAccount!!.userId,
                                                    onError = { error ->
                                                        stackedSnackbarState.showErrorSnackbar(
                                                            title = "Something went wrong!",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    onSuccessAction = {
                                                        stackedSnackbarState.showSuccessSnackbar(
                                                            title = "Deleting \"${it.title}\" successfully!"
                                                        )
                                                    }
                                                )
                                            } else {
                                                homeScreenViewModel.deleteOfflineTask(it)
                                                stackedSnackbarState.showSuccessSnackbar(
                                                    title = "Deleting \"${it.title}\" successfully!"
                                                )
                                            }


                                        }
                                    },
                                    onSyncClick = { task ->

                                        homeScreenViewModel.updateTask(
                                            task = task,
                                            userId = userAccount!!.userId,
                                            onError =  { error ->
                                                stackedSnackbarState.showErrorSnackbar(
                                                    title = "Something went wrong!",
                                                    duration = StackedSnackbarDuration.Short
                                                )
                                            },
                                            isConnected = isConnected,
                                            onSuccessAction = {
                                                stackedSnackbarState.showSuccessSnackbar(
                                                    title = "\"${task.title}\" synced successfully!"
                                                )
                                            }

                                        )

                                    }
                                )
                            }
                        }
                    }
                }
            }


            if (showDialog) {
                AddTaskDialog(
                    title = title,
                    onTitleChange = { title = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    onDismiss = {
                        showDialog = false
                        title = ""
                        description = ""
                    },
                    onSave = {
                        showDialog = false
                        scope.launch {
                            if (userAccount != null) {
                                Log.d("Saveing", "Saving online")
                                homeScreenViewModel.addTask(
                                    isConnected = isConnected,
                                    userId = userAccount!!.userId,
                                    task = it,
                                    onError = { error ->
                                        stackedSnackbarState.showErrorSnackbar(
                                            title = "Something went wrong!",
                                            duration = StackedSnackbarDuration.Short
                                        )
                                    },
                                    onSuccessAction = {
                                        stackedSnackbarState.showSuccessSnackbar(
                                            title = "\"${it.title}\" Added Successfully!",
                                            duration = StackedSnackbarDuration.Short
                                        )
                                    }
                                )
                            } else {
                                Log.d("Saveing", "Saving offline Successfully")
                                homeScreenViewModel.addOfflineTask(it)
                                stackedSnackbarState.showSuccessSnackbar(
                                    title = "\"${it.title}\" Added Successfully!",
                                    duration = StackedSnackbarDuration.Short
                                )
                            }
                        }

                        title = ""
                        description = ""
                    }

                )
            }
        }
    }
}


@Composable
fun AddTaskFAB(onClick: () -> Unit, showDialog: Boolean, color: Color) {
    val fabScale by animateFloatAsState(
        targetValue = if (showDialog) 0f else 1f,
        animationSpec = tween(durationMillis = 300)
    )
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.scale(fabScale),
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        containerColor = color,
        contentColor = Color.White
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
    }
}


@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun TasksList(
    modifier: Modifier = Modifier,
    isCompleted: Boolean,
    onSyncClick: (TaskUiState) -> Unit,
    isEmpty: Boolean = false,
    onClick: (TaskUiState) -> Unit,
    tasksList: List<TaskUiState?>,
    onCheckedChange: (TaskUiState) -> Unit,
    onEditClick: (TaskUiState) -> Unit,
    onDeleteClick: (TaskUiState) -> Unit,
    userAccount: AuthenticatedUserData?
) {
    val label = if (isCompleted) "Completed" else "Uncompleted"
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy, // Adjust bounciness
                    stiffness = Spring.StiffnessMediumLow // Adjust stiffness
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 100.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(100))
                .background(if (!isCompleted) Color(0xFF4B00B3) else Color(0xFF006D30)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = Color.White,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .padding(5.dp),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        if (tasksList.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(5.dp)
            ) {
                tasksList.filterNotNull().forEach { task ->
                    TodoCard(
                        onClick = { onClick(task) },
                        onCheckedChange = { onCheckedChange(task) },
                        onDeleteClick = onDeleteClick,
                        onEditClick = onEditClick,
                        task = task,
                        onSyncClick = onSyncClick,
                        userAccount = userAccount
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        } else {
            if (isEmpty) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(15.dp)
                                .clip(CircleShape)
                                .background(
                                    if (!isCompleted) Color(0xFF4B00B3) else Color(0xFF006D30)
                                )
                                .size(150.dp)

                        )
                        Text(
                            text = "No ${
                                label.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.getDefault()
                                    ) else it.toString()
                                }
                            } Tasks",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun TodoCard(
    onClick: () -> Unit,
    userAccount: AuthenticatedUserData?,
    task: TaskUiState,
    onSyncClick: (TaskUiState) -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: (TaskUiState) -> Unit,
    onDeleteClick: (TaskUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    val completed = SwipeAction(
        onSwipe = { onCheckedChange(!task.isCompleted) },
        icon = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.White,

                    )
                Spacer(Modifier.padding(3.dp))
                Text(
                    text = if (task.isCompleted) "Undo" else "Complete",
                    color = Color.White
                )
            }
        },
        background = if (!task.isCompleted) Color(0xFF4CD453) else Color(0xFFD51A1A)
    )

    val delete = SwipeAction(
        onSwipe = { onDeleteClick(task) },
        icon = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.White,

                    )
                Spacer(Modifier.padding(3.dp))
                Text(
                    text = "Delete",
                    color = Color.White
                )
            }
        },
        background = Color(0xFFD51A1A)
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(30))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (!task.isCompleted) {
                        listOf(
                            Color(0xFF7200A3),
                            Color(0xFF4B00B3),
                            Color(0xFF1200E3)
                        )
                    } else {
                        listOf(
                            Color(0xFF01AC4C),
                            Color(0xFF00A049),
                            Color(0xFF006D30)
                        )
                    }
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for marking as complete
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    uncheckedColor = Color.White,
                    checkmarkColor = Color(0xFF7200A3),
                    checkedColor = Color.White
                ),
                modifier = Modifier.clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))


            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (task.isCompleted) FontWeight.Light else FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                modifier = modifier.weight(1f)
            )

            val date = if (task.date == getFormattedDate()) "Today" else task.date
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.Edit,

                    contentDescription = "Edit Task",
                    tint = Color.White
                )
            }
            IconButton(onClick = { onDeleteClick(task) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = Color.White

                )
            }
            AnimatedVisibility((!task.isSynced && userAccount != null)) {
                IconButton(onClick = { onSyncClick(task) }) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Delete Task",
                        tint = Color.Red,

                        )

                }
            }
        }
    }

}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "No Tasks", group = "")
@Composable
fun PreviewNoTasks() {
    ToDoListTheme {
        Surface {
            TodoCard(
                onCheckedChange = {},
                onEditClick = {},
                onDeleteClick = { TaskUiState() },
                task = TaskUiState(),
                onClick = {},
                onSyncClick = {},
                userAccount = AuthenticatedUserData("", "", "", "", "")
            )
        }
    }
}


