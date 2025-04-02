package com.example.todolist.main.ui.screens

import StackedSnackbarDuration
import StackedSnakbarHostState
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.RemoveDone
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.compose.ToDoListTheme
import com.example.todolist.backend.auth.AuthenticatedUserData
import com.example.todolist.backend.model.CollabTask
import com.example.todolist.main.ui.components.primary.AddCollabTaskDialog
import com.example.todolist.main.ui.components.primary.CollabsBottomNavigation
import com.example.todolist.main.ui.components.secondry.FetchingDataScreen
import com.example.todolist.main.viewmodels.CollaborationViewModel
import com.example.todolist.main.viewmodels.LoadingControl
import com.example.todolist.main.viewmodels.UserAccountViewModel
import com.example.todolist.utils.getFormattedDate
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.util.Locale


@SuppressLint("ContextCastToActivity")
@Composable
fun CollaborationScreen(
    // params
    isConnected: Boolean,
    stackedSnackbarHostState: StackedSnakbarHostState,
    collaborationViewModel: CollaborationViewModel,
    userAccountViewModel: UserAccountViewModel,
    // lambdas
    backHandler: () -> Unit,
    // composable
    topAppBar: @Composable () -> Unit
) {
    val loadingState by collaborationViewModel.loading.collectAsStateWithLifecycle()
    BackHandler {
        backHandler()
    }

    when (loadingState) {
        is LoadingControl.FetchingData -> {
            FetchingDataScreen()
        }

        is LoadingControl.Success -> {
            SuccessCollaborationScreen(
                topAppBar = topAppBar,
                userAccountViewModel = userAccountViewModel,
                isConnected = isConnected,
                collaborationViewModel = collaborationViewModel,
                stackedSnackbarHostState = stackedSnackbarHostState,
            )
        }

        is LoadingControl.Error -> {}
    }
}


@OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
@Composable
fun SuccessCollaborationScreen(
    modifier: Modifier = Modifier,
    isConnected: Boolean,
    stackedSnackbarHostState: StackedSnakbarHostState,
    collaborationViewModel: CollaborationViewModel,
    userAccountViewModel: UserAccountViewModel,
    topAppBar: @Composable () -> Unit
) {
    // states
    val collabsState by collaborationViewModel.userCollab.collectAsState()
    val userAccount by userAccountViewModel.userData.collectAsState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0)

    // variables
    var showDialog by remember { mutableStateOf(false) }
    var isEdit by rememberSaveable { mutableStateOf(false) }
    var pageCountIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            CollabsBottomNavigation(
                pageIndex = pageCountIndex,
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(it)
                    }
                }
            )
        },
        topBar = {
            topAppBar()
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                AddTaskFAB(
                    onClick = {
                        if (isConnected) {
                            isEdit = false
                            showDialog = true
                        } else {
                            scope.launch {
                                stackedSnackbarHostState.showInfoSnackbar(
                                    title = "Failed",
                                    description = "No internet connection",
                                    duration = StackedSnackbarDuration.Short
                                )
                            }
                        }
                    },
                    showDialog = showDialog,
                    color = Color(0xFF1976D2)
                )
            }
        }
    ) { innerPadding ->

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                pageCountIndex = page
            }
        }
        HorizontalPager(
            count = 3,
            userScrollEnabled = false,
            itemSpacing = 20.dp,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalAlignment = Alignment.Top

        ) { pageIndex ->
            var title by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }
            val completedTasks = collabsState.currentCollab?.completedTasks ?: emptyList()
            val uncompletedTasks = collabsState.currentCollab?.tasks ?: emptyList()

            Box {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {

                    item {
                        when (pageIndex) {
                            0 -> {
                                CollaborationTasksList(
                                    collaborationViewModel = collaborationViewModel,
                                    userAccount = userAccount,
                                    isEmpty = uncompletedTasks.isEmpty(),
                                    label = "Uncompleted",
                                    tasksList = uncompletedTasks,
                                    onCheckedChange = {
                                        if (isConnected) {
                                            if (it in completedTasks) {
                                                collaborationViewModel.uncompleteTask(
                                                    task = it,
                                                    collabId = collabsState.currentCollab!!.id,
                                                    onSuccess = {},
                                                    onError = {

                                                        stackedSnackbarHostState.showErrorSnackbar(
                                                            title = "Something went wrong",
                                                            duration = StackedSnackbarDuration.Short
                                                        )

                                                    },
                                                    user = userAccount!!
                                                )
                                            } else if (it in uncompletedTasks) {
                                                collaborationViewModel.completeTask(
                                                    task = it,
                                                    collabId = collabsState.currentCollab!!.id,
                                                    onSuccess = {
                                                        stackedSnackbarHostState.showInfoSnackbar(
                                                            title = "\"${it.title}\" Completed",
                                                            duration = StackedSnackbarDuration.Short
                                                        )

                                                    },
                                                    onError = {

                                                        stackedSnackbarHostState.showErrorSnackbar(
                                                            title = "Something went wrong",
                                                            duration = StackedSnackbarDuration.Short
                                                        )

                                                    },
                                                    user = userAccount!!
                                                )
                                            }
                                        } else {
                                            stackedSnackbarHostState.showInfoSnackbar(
                                                title = "Failed",
                                                description = "No internet connection",
                                                duration = StackedSnackbarDuration.Short
                                            )
                                        }
                                    },
                                    onDeleteClick = {
                                        if (isConnected) {

                                            scope.launch {
                                                collaborationViewModel.deleteTask(
                                                    task = it,
                                                    collab = collabsState.currentCollab!!,
                                                    onSuccess = {
                                                        stackedSnackbarHostState.showSuccessSnackbar(
                                                            title = "\"${it.title}\" is deleted successfully",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    onError = {
                                                        stackedSnackbarHostState.showErrorSnackbar(
                                                            title = "Something went wrong",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    user = userAccount!!
                                                )
                                            }
                                        } else {
                                            stackedSnackbarHostState.showInfoSnackbar(
                                                title = "Failed",
                                                description = "No internet connection",
                                                duration = StackedSnackbarDuration.Short
                                            )
                                        }
                                    },
                                    isCompleted = false,
                                    onEditClick = {
                                        collaborationViewModel.updateTask(
                                            user = userAccount!!,
                                            collabId = collabsState.currentCollab!!.id,
                                            task = it,
                                            onSuccess = {
                                                stackedSnackbarHostState.showSuccessSnackbar(
                                                    title = "Editing Task Successfully",
                                                    duration = StackedSnackbarDuration.Short
                                                )
                                            },
                                            onError = {
                                                stackedSnackbarHostState.showErrorSnackbar(
                                                    title = "Something went wrong",
                                                    description = "Editing Task Failed",
                                                    duration = StackedSnackbarDuration.Short
                                                )
                                            }
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                CollaborationTasksList(
                                    collaborationViewModel = collaborationViewModel,
                                    userAccount = userAccount,
                                    isEmpty = completedTasks.isEmpty(),
                                    label = "Completed",
                                    tasksList = completedTasks,
                                    onCheckedChange = {
                                        if (isConnected) {
                                            if (it in completedTasks) {
                                                collaborationViewModel.uncompleteTask(
                                                    task = it,
                                                    collabId = collabsState.currentCollab!!.id,
                                                    onSuccess = {
                                                        stackedSnackbarHostState.showSuccessSnackbar(
                                                            title = "\"${it.title}\" uncompleted",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    onError = {
                                                        stackedSnackbarHostState.showErrorSnackbar(
                                                            title = "Something went wrong",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    user = userAccount!!
                                                )
                                            } else if (it in uncompletedTasks) {
                                                collaborationViewModel.completeTask(
                                                    task = it,
                                                    collabId = collabsState.currentCollab!!.id,
                                                    onSuccess = {
                                                        stackedSnackbarHostState.showSuccessSnackbar(
                                                            title = "\"${it.title}\" Completed",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    onError = {
                                                        stackedSnackbarHostState.showErrorSnackbar(
                                                            title = "Something went wrong",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    user = userAccount!!
                                                )
                                            }
                                        } else {
                                            stackedSnackbarHostState.showInfoSnackbar(
                                                title = "Failed",
                                                description = "No internet connection",
                                                duration = StackedSnackbarDuration.Short
                                            )
                                        }
                                    },
                                    onDeleteClick = {
                                        if (isConnected) {

                                            scope.launch {
                                                collaborationViewModel.deleteTask(
                                                    task = it,
                                                    collab = collabsState.currentCollab!!,
                                                    onSuccess = {
                                                        stackedSnackbarHostState.showSuccessSnackbar(
                                                            title = "\"${it.title}\" is deleted successfully",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    onError = {
                                                        stackedSnackbarHostState.showErrorSnackbar(
                                                            title = "Something went wrong",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    user = userAccount!!
                                                )
                                            }
                                        } else {
                                            stackedSnackbarHostState.showInfoSnackbar(
                                                title = "Failed",
                                                description = "No internet connection",
                                                duration = StackedSnackbarDuration.Short
                                            )
                                        }
                                    },
                                    isCompleted = true,
                                    onEditClick = {
                                        collaborationViewModel.updateTask(
                                            user = userAccount!!,
                                            collabId = collabsState.currentCollab!!.id,
                                            task = it,
                                            onSuccess = {
                                                stackedSnackbarHostState.showSuccessSnackbar(
                                                    title = "Editing Task Successfully",
                                                    duration = StackedSnackbarDuration.Short
                                                )
                                            },
                                            onError = {
                                                stackedSnackbarHostState.showErrorSnackbar(
                                                    title = "Something went wrong",
                                                    description = "Editing Task Failed",
                                                    duration = StackedSnackbarDuration.Short
                                                )
                                            }
                                        )
                                    }
                                )
                            }

                            else -> {
                                CollaborationTasksList(
                                    collaborationViewModel = collaborationViewModel,
                                    userAccount = userAccount,
                                    isEmpty = if (pageIndex == 1) completedTasks.isEmpty() else uncompletedTasks.isEmpty(),
                                    label = if (pageIndex == 1) "Completed" else "Uncompleted",
                                    tasksList = if (pageIndex == 1) completedTasks else uncompletedTasks,
                                    onCheckedChange = {
                                        if (isConnected) {
                                            if (it in completedTasks) {
                                                collaborationViewModel.uncompleteTask(
                                                    task = it,
                                                    collabId = collabsState.currentCollab!!.id,
                                                    onSuccess = {
                                                        stackedSnackbarHostState.showSuccessSnackbar(
                                                            title = "\"${it.title}\" uncompleted",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    onError = {
                                                        stackedSnackbarHostState.showErrorSnackbar(
                                                            title = "Something went wrong",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    user = userAccount!!
                                                )
                                            } else if (it in uncompletedTasks) {
                                                collaborationViewModel.completeTask(
                                                    task = it,
                                                    collabId = collabsState.currentCollab!!.id,
                                                    onSuccess = {
                                                        stackedSnackbarHostState.showSuccessSnackbar(
                                                            title = "\"${it.title}\" Completed",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    onError = {
                                                        stackedSnackbarHostState.showErrorSnackbar(
                                                            title = "Something went wrong",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    user = userAccount!!
                                                )
                                            }
                                        } else {
                                            stackedSnackbarHostState.showInfoSnackbar(
                                                title = "Failed",
                                                description = "No internet connection",
                                                duration = StackedSnackbarDuration.Short
                                            )
                                        }
                                    },
                                    onDeleteClick = {
                                        if (isConnected) {

                                            scope.launch {
                                                collaborationViewModel.deleteTask(
                                                    task = it,
                                                    collab = collabsState.currentCollab!!,
                                                    onSuccess = {
                                                        stackedSnackbarHostState.showSuccessSnackbar(
                                                            title = "\"${it.title}\" is deleted successfully",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    onError = {
                                                        stackedSnackbarHostState.showErrorSnackbar(
                                                            title = "Something went wrong",
                                                            duration = StackedSnackbarDuration.Short
                                                        )
                                                    },
                                                    user = userAccount!!
                                                )
                                            }
                                        } else {
                                            stackedSnackbarHostState.showInfoSnackbar(
                                                title = "Failed",
                                                description = "No internet connection",
                                                duration = StackedSnackbarDuration.Short
                                            )
                                        }
                                    },
                                    isCompleted = pageIndex == 1,
                                    onEditClick = {
                                        if (isConnected) {
                                            collaborationViewModel.updateTask(
                                                user = userAccount!!,
                                                collabId = collabsState.currentCollab!!.id,
                                                task = it,
                                                onSuccess = {
                                                    stackedSnackbarHostState.showSuccessSnackbar(
                                                        title = "Editing Task Successfully",
                                                        duration = StackedSnackbarDuration.Short
                                                    )
                                                },
                                                onError = {
                                                    stackedSnackbarHostState.showErrorSnackbar(
                                                        title = "Something went wrong",
                                                        description = "Editing Task Failed",
                                                        duration = StackedSnackbarDuration.Short
                                                    )
                                                }
                                            )
                                        } else {
                                            stackedSnackbarHostState.showInfoSnackbar(
                                                "No Internet Connection",
                                                duration = StackedSnackbarDuration.Short
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                }
            }
            if (showDialog) {
                AddCollabTaskDialog(
                    onDismiss = {
                        showDialog = false
                        title = ""
                        description = ""
                    },
                    onSave = {
                        collaborationViewModel.addTask(
                            it.copy(
                                assignedTo = mapOf(
                                    "id" to userAccount?.userId,
                                    "username" to userAccount?.username,
                                    "profilePic" to userAccount?.profilePictureUrl
                                )
                            ),
                            collab = collabsState.currentCollab!!,
                            onSuccess = {
                                stackedSnackbarHostState.showSuccessSnackbar(
                                    title = "\"${it.title}\" is Added successfully",
                                    duration = StackedSnackbarDuration.Short
                                )
                            },
                            onError = {
                                stackedSnackbarHostState.showErrorSnackbar(
                                    title = "Something went wrong",
                                    duration = StackedSnackbarDuration.Short
                                )
                            }
                        )
                        showDialog = false
                        title = ""
                        description = ""
                    },
                    title = title,
                    isEdit = false,
                    onTitleChange = { title = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    userAccount = userAccount!!
                )
            }
        }
    }
}


@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun CollaborationTasksList(
    modifier: Modifier = Modifier,
    isCompleted: Boolean,
    collaborationViewModel: CollaborationViewModel,
    label: String,
    isEmpty: Boolean = false,
    tasksList: List<CollabTask?>,
    onCheckedChange: (CollabTask) -> Unit,
    onEditClick: (CollabTask) -> Unit,
    onDeleteClick: (CollabTask) -> Unit,
    userAccount: AuthenticatedUserData?
) {
    val state by collaborationViewModel.userCollab.collectAsStateWithLifecycle()
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
                .background(if (!isCompleted) CollabColors.UncompletedTask else CollabColors.CompletedTask),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
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
                    CollaborationTodoCard(
                        onCheckedChange = onCheckedChange,
                        onDeleteClick = onDeleteClick,
                        task = task,
                        userAccount = userAccount,
                        creator = task.assignedTo,
                        admins = state.currentCollab!!.admins.map { it?.get("id") as String },
                        isCompleted = isCompleted,
                        onEditClick = { title, description ->
                            onEditClick(task.copy(title = title, description = description))
                        }
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
                            tint = Color.White,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(15.dp)
                                .clip(CircleShape)
                                .background(if (!isCompleted) CollabColors.UncompletedTask else CollabColors.CompletedTask)
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


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CollaborationTodoCard(
    modifier: Modifier = Modifier,
    isCompleted: Boolean,
    creator: Map<String, String?>,
    admins: List<String?>,
    userAccount: AuthenticatedUserData?,
    onEditClick: (title: String, description: String) -> Unit,
    task: CollabTask,
    onCheckedChange: (CollabTask) -> Unit,
    onDeleteClick: (CollabTask) -> Unit,

) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    val hasAccess = (userAccount?.userId in admins || creator["id"] == userAccount?.userId)
    val currentUser = userAccount?.userId == creator["id"]
    val isAdmin = creator["id"] in admins


    AnimatedVisibility(
        visible = !isEditing,
        enter = expandVertically() + fadeIn(),
        exit = shrinkOut() + fadeOut()
    ) {
        DefaultTaskCard(
            modifier = modifier,
            userAccount = userAccount,
            isCompleted = isCompleted,
            isAdmin = isAdmin,
            task = task,
            onClick = { expanded = !expanded },
            expanded = expanded,
            onCheckedChange = onCheckedChange,
            onDeleteClick = onDeleteClick,
            context = context,
            hasAccess = hasAccess,
            creator = creator,
            currentUser = currentUser,
            onEditClick = { isEditing = true }
        )
    }
    if (isEditing) {
        TaskEditDialog(
            onDismiss = {
                isEditing = false
                expanded = false
                        },
            onEditClick = { title, description ->
                onEditClick(title, description)
                isEditing = false
                expanded = false
            },
            task = task,
            roleColor = if (currentUser) {
                CollabColors.CurrentUserTask
            } else if (!isCompleted) {
                CollabColors.BottomBar
            } else {
                CollabColors.CompletedTask
            }
        )
    }
}

@Composable
fun TaskEditDialog(
    roleColor: Color,
    onDismiss: () -> Unit,
    onEditClick: (title: String, description: String) -> Unit,
    task: CollabTask,
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
        {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = roleColor,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(350.dp)
                    .height(500.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    item {
                        Text(
                            text = "Edit",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedTextColor = CollabColors.SecondaryText,
                                unfocusedBorderColor = CollabColors.SecondaryText,
                                unfocusedLabelColor = CollabColors.SecondaryText,
                                focusedLabelColor = CollabColors.PrimaryText,
                                focusedTextColor = CollabColors.PrimaryText,
                                focusedBorderColor = CollabColors.PrimaryText,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(textDirection = TextDirection.ContentOrLtr),
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Task Title") })
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedTextColor = CollabColors.SecondaryText,
                                unfocusedBorderColor = CollabColors.SecondaryText,
                                unfocusedLabelColor = CollabColors.SecondaryText,
                                focusedLabelColor = CollabColors.PrimaryText,
                                focusedTextColor = CollabColors.PrimaryText,
                                focusedBorderColor = CollabColors.PrimaryText,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            value = description,
                            onValueChange = { description = it },
                            textStyle = TextStyle(textDirection = TextDirection.ContentOrLtr),
                            label = { Text("Task Description") },
                            placeholder = {
                                if (description.isBlank()) {
                                    Text("No description")
                                }
                            }
                        )

                        Spacer(Modifier.weight(1f))
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            onClick = {
                                onEditClick(title, description)
                            },
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = roleColor.copy(
                                    red = ((roleColor.red * 0.7).toFloat()),
                                    green = ((roleColor.green * 0.7).toFloat()),
                                    blue = ((roleColor.blue * 0.7).toFloat())
                                ),
                                containerColor = roleColor.copy(
                                    red = ((roleColor.red * 1.5).toFloat()),
                                    green = ((roleColor.green * 1.5).toFloat()),
                                    blue = ((roleColor.blue * 1.5).toFloat())
                                ),
                                contentColor = CollabColors.PrimaryText,
                            ),
                            enabled = description.trim() != task.description.trim() || title.trim() != task.title.trim()
                        ) {
                            Text("Submit")
                        }
                    }
                    item {
                    }
                }
            }
        },
    )
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DefaultTaskCard(
    modifier: Modifier = Modifier,
    currentUser: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit,
    context: Context,
    creator: Map<String, String?>,
    userAccount: AuthenticatedUserData?,
    isAdmin: Boolean,
    expanded: Boolean,
    hasAccess: Boolean,
    task: CollabTask,
    onCheckedChange: (CollabTask) -> Unit,
    onDeleteClick: (CollabTask) -> Unit,
    onEditClick: () -> Unit,
) {

    Card(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick.invoke() },
        colors = CardDefaults.cardColors(
            containerColor = if (currentUser) {
                CollabColors.CurrentUserTask
            } else {
                if (isCompleted) {
                    CollabColors.CompletedTask
                } else {
                    CollabColors.UncompletedTask
                }
            }
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .animateContentSize()
        ) {
            // Top Section (Same as Collapsed State)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(!currentUser) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context = context)
                                .data(creator["profilePic"])
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Creator Profile Picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))


                Column {
                    Text(text = task.title, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        text = if (task.date == getFormattedDate()) "Today" else task.date,
                        fontSize = 12.sp,
                        color = CollabColors.SecondaryText
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Text(
                        text = if (userAccount?.userId == (creator["id"] as String)) "You" else (creator["username"] as String),
                        fontWeight = FontWeight.Bold,
                        color = CollabColors.PrimaryText
                    )
                    Text(
                        text = if (isAdmin) "Admin" else "Member",
                        fontSize = 12.sp,
                        color = CollabColors.SecondaryText
                    )
                }

            }
            AnimatedVisibility(expanded) {
                HorizontalDivider(color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    if (task.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Description:\n${task.description}",
                            style = TextStyle(textDirection = TextDirection.ContentOrLtr),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CollabColors.PrimaryText.copy(alpha =0.8f)
                        )
                    } else {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Description: No description!",
                            fontSize = 16.sp,
                            color = CollabColors.PrimaryText
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Bottom Section (Access Controls)
                    if (hasAccess) {
                        Row {
                            Button(
                                onClick = { onCheckedChange(task) },
                                colors = ButtonDefaults.buttonColors(containerColor = CollabColors.Background)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isCompleted) Icons.Filled.RemoveDone else Icons.Filled.TaskAlt,
                                        contentDescription = "Check",
                                        tint = Color.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onDeleteClick(task) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CollabColors.Background,
                                )
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onEditClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CollabColors.Background,
                                )
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

            }

        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, name = "No Tasks", group = "")
@Composable
fun PreviewCollaborationScreen() {
    ToDoListTheme {
        Surface {
            CollaborationTodoCard(
                onCheckedChange = {},
                onDeleteClick = { CollabTask() },
                task = CollabTask(),
                userAccount = AuthenticatedUserData("", "", "", "", ""),
                creator = mapOf(),
                admins = emptyList(),
                isCompleted = false,
                onEditClick = { t: String, x: String -> }
            )
        }
    }
}


