package com.example.todolist.main


import StackedSnackbarHost
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todolist.backend.firebase.toMemberData
import com.example.todolist.experiments.notifications.TasksNotificationsManager
import com.example.todolist.main.navigation.NavHandler
import com.example.todolist.main.navigation.NavRoutes
import com.example.todolist.main.navigation.TodoApp
import com.example.todolist.main.ui.components.primary.MembersDialog
import com.example.todolist.main.ui.components.primary.ModernProfileDialog
import com.example.todolist.main.ui.components.primary.ModernSignInDialog
import com.example.todolist.main.ui.components.primary.OperationsDialog
import com.example.todolist.main.ui.components.secondry.FloatingNotificationBar
import com.example.todolist.main.viewmodels.AppViewModelsProvider
import com.example.todolist.main.viewmodels.CollaborationViewModel
import com.example.todolist.main.viewmodels.HomeScreenViewModel
import com.example.todolist.main.viewmodels.SnackBarViewModel
import com.example.todolist.main.viewmodels.UserAccountViewModel
import kotlinx.coroutines.launch
import rememberStackedSnackbarHostState

const val DATABASE = "Tasks_database"

@Composable
fun TodoAppEntry(
    notificationsManager: TasksNotificationsManager,
) {
    val navController = rememberNavController()
    NavigationGraph(
        navHostController = navController,
    )
}

@SuppressLint(
    "StateFlowValueCalledInComposition",
    "MutableCollectionMutableState",
    "ContextCastToActivity"
)
@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    userAccountViewModel: UserAccountViewModel = viewModel<UserAccountViewModel>(
        factory = AppViewModelsProvider.Factory
    ),
    homeViewModel: HomeScreenViewModel = viewModel(factory = AppViewModelsProvider.Factory),
    collabsViewModel: CollaborationViewModel = viewModel(factory = AppViewModelsProvider.Factory),
    snackBarViewModel: SnackBarViewModel = viewModel(),
) {
    val dataImported by homeViewModel.dataImported.collectAsStateWithLifecycle()
    val signInState by userAccountViewModel.state.collectAsStateWithLifecycle()
    val userAccount by userAccountViewModel.userData.collectAsStateWithLifecycle()
    val isConnected by userAccountViewModel.isConnected.collectAsStateWithLifecycle()
    val collabs by collabsViewModel.userCollab.collectAsStateWithLifecycle()
    var start by rememberSaveable { mutableStateOf(true) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val snackBarScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val stackedSnackBarHostState = rememberStackedSnackbarHostState(
        maxStack = 5,
        animation = StackedSnackbarAnimation.Bounce,
    )

    signInState.signInErrorMessage?.let { error ->
        stackedSnackBarHostState.showErrorSnackbar(
            title = "Sign in failed",
            description = "Something went wrong try again later",
            duration = StackedSnackbarDuration.Short
        )
        userAccountViewModel.resetState()
    }

    // Handle successful sign-in and data import
    if (signInState.isSignInSuccessful && !start && !dataImported) {
        homeViewModel.updateDataSource(true)
        // Ensures it runs once when sign-in is successful
        homeViewModel.importData(
            userId = userAccount?.userId,
            onSuccessAction = {
                stackedSnackBarHostState.showSuccessSnackbar(
                    title = "Succeeded",
                    description = "Fetching tasks succeeded",
                    duration = StackedSnackbarDuration.Short
                )
            },
            onErrorAction = { error ->
                stackedSnackBarHostState.showErrorSnackbar(
                    title = "Failed to fetch your tasks",
                    description = "Something went wrong",
                    duration = StackedSnackbarDuration.Short
                )
            },
            isConnected = isConnected
        )
        collabsViewModel.getUserCollabs(
            userAccount?.userId,
            onError = {
                stackedSnackBarHostState.showErrorSnackbar(
                    title = "Failed to fetch your Collabs",
                    description = "Something went wrong",
                    duration = StackedSnackbarDuration.Short
                )
            },
            onSuccess = {
                userAccountViewModel.updateUserCollabs(it)
                userAccountViewModel.updateFirestoreUserData { }
            },
            onTaskChange = {}
        )
        homeViewModel.updateDataImported(true)
    }


    var showProfileDialog by rememberSaveable { mutableStateOf(false) }
    var showNotSignedInDialog by rememberSaveable { mutableStateOf(false) }
    var showMembersDialog by rememberSaveable { mutableStateOf(false) }
    var showOperationsDialog by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                snackBarScope.launch {
                    userAccountViewModel.onSignInResult(result.data ?: return@launch)
                }
            }
        }
    )

    TodoApp(
        stackedSnackbarState = stackedSnackBarHostState,
        collabs = collabs,
        isConnected = isConnected,
        isSignedIn = userAccount != null,
        screen = navBackStackEntry,
        onCollaborationSave = {
            snackBarScope.launch {
                drawerState.close()
            }
            if (isConnected) {
                collabsViewModel.addCollaboration(
                    userData = userAccount!!,
                    onIsExist = {
                        stackedSnackBarHostState.showInfoSnackbar(
                            title = "Failed",
                            description = "This collab already exists",
                            duration = StackedSnackbarDuration.Short
                        )
                    },
                    onError = {
                        stackedSnackBarHostState.showErrorSnackbar(
                            title = "Failed",
                            description = "Something went wrong try again later",
                            duration = StackedSnackbarDuration.Short
                        )
                    },
                    collab = it,
                    onSuccess = {
                        stackedSnackBarHostState.showSuccessSnackbar(
                            title = "Succeeded",
                            description = "Collab Created Successfully",
                            duration = StackedSnackbarDuration.Short,
                            actionTitle = "Open",
                            action = {
                                collabsViewModel.updateCurrentCollab(it)
                                navHostController.navigate(NavRoutes.Collaboration.route)
                            }
                        )
                    }
                )
            } else {
                stackedSnackBarHostState.showInfoSnackbar(
                    title = "Failed",
                    description = "No internet connection",
                    duration = StackedSnackbarDuration.Short
                )
            }
        },
        onHomeClick = {
            snackBarScope.launch {
                drawerState.close()
            }
            navHostController.navigate(NavRoutes.Home.route)
        },
        drawerState = drawerState,
        onCollaborationClick = {
            snackBarScope.launch {
                drawerState.close()
                collabsViewModel.updateCurrentCollab(it)
                navHostController.navigate(NavRoutes.Collaboration.route)
            }
        },
        onAboutClick = {
            snackBarScope.launch {
                drawerState.close()
            }
            navHostController.navigate(NavRoutes.About.route)
        },
        onJoin = {
            snackBarScope.launch {
                drawerState.close()
            }
            if (isConnected) {
                collabsViewModel.joinCollab(
                    userId = userAccount?.userId,
                    username = it[0],
                    password = it[1],
                    onError = {
                        stackedSnackBarHostState.showErrorSnackbar(
                            title = "Failed",
                            description = "Something went wrong try again later",
                            duration = StackedSnackbarDuration.Short
                        )
                    },
                    onSuccess = {
                        snackBarScope.launch {
                            stackedSnackBarHostState.showSuccessSnackbar(
                                title = "Succeeded",
                                description = "Joined collab successfully",
                                duration = StackedSnackbarDuration.Short
                            )
                        }
                    },
                    userData = userAccount
                )
            } else {
                stackedSnackBarHostState.showInfoSnackbar(
                    title = "Failed",
                    description = "No internet connection",
                    duration = StackedSnackbarDuration.Short
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = {
                StackedSnackbarHost(stackedSnackBarHostState)
            },
            floatingActionButtonPosition = FabPosition.Center,
        ) { innerPadding ->
            println(innerPadding)
            NavHandler(
                navHostController = navHostController,
                userAccount = userAccount,
                userAccountViewModel = userAccountViewModel,
                homeScreenViewModel = homeViewModel,
                collaborationViewModel = collabsViewModel,
                stackedSnackbarState = stackedSnackBarHostState,
                snackBarScope = snackBarScope,

                collabs = collabs,

                isConnected = isConnected,
                drawerState = drawerState,

                isSignIn = signInState.isSignInSuccessful,
                onShowMembersDialog = { showMembersDialog = true },
                onGFCclicked = { showNotSignedInDialog = true },
                onNotSignedClick = { showNotSignedInDialog = true },
                onShowProfileDialog = { showProfileDialog = true },
                onShowOperationsDialog = { showOperationsDialog = true },
                onSplashScreenFinish = { start = false },
            )


            if (showProfileDialog) {
                ModernProfileDialog(
                    fullName = userAccount?.username ?: "",
                    email = userAccount?.email ?: "",
                    onSignOut = {
                        snackBarScope.launch {
                            navHostController.navigate(NavRoutes.Home.route)
                            userAccountViewModel.signOut {
                                snackBarScope.launch {
                                    homeViewModel.clearTasksList()
                                }
                            }
                            collabsViewModel.signOut()
                            homeViewModel.updateDataImported(false)
                            showProfileDialog = false
                            stackedSnackBarHostState.showSuccessSnackbar(
                                title = "Succeeded",
                                description = "Signed out successfully!",
                                duration = StackedSnackbarDuration.Short
                            )
                        }
                    },
                    onCancel = {
                        showProfileDialog = false
                    }
                )
            }
            if (showNotSignedInDialog) {
                ModernSignInDialog(
                    onDismiss = {
                        showNotSignedInDialog = false
                    },
                    onSignIn = {
                        if (isConnected) {
                            snackBarScope.launch {
                                val signInIntent = userAccountViewModel.signinIntent()
                                launcher.launch(
                                    signInIntent
                                )
                                showNotSignedInDialog = false
                            }
                        } else {
                            stackedSnackBarHostState.showInfoSnackbar(
                                title = "Failed",
                                description = "No internet connection",
                                duration = StackedSnackbarDuration.Short
                            )
                        }
                    }
                )
            }
            if (showMembersDialog) {
                MembersDialog(
                    members = collabs.currentCollab!!.members,
                    currentUser = userAccount!!.toMemberData(),
                    admins = collabs.currentCollab!!.admins,
                    onPromoteMember = { id, username ->
                        if (isConnected) {
                            collabsViewModel.promoteUser(
                                user = userAccount,
                                memberUsername = username,
                                memberId = id,
                                collab = collabs.currentCollab!!,
                                onError = {
                                    stackedSnackBarHostState.showErrorSnackbar(
                                        title = "Promoting failed!",
                                        description = "Something went wrong try again later",
                                        duration = StackedSnackbarDuration.Short
                                    )
                                },
                                onSuccess = {
                                    stackedSnackBarHostState.showSuccessSnackbar(
                                        title = "Promoting succeeded",
                                        description = "Member is promoted successfully",
                                        duration = StackedSnackbarDuration.Short
                                    )
                                }
                            )
                        } else {
                            stackedSnackBarHostState.showInfoSnackbar(
                                title = "Failed",
                                description = "No internet connection",
                                duration = StackedSnackbarDuration.Short
                            )

                        }
                        showMembersDialog = false
                    },
                    onRemoveMember = { id, username ->
                        if (isConnected) {
                            collabsViewModel.removeMember(
                                user = userAccount,
                                collab = collabs.currentCollab!!,
                                memberId = id,
                                memberUsername = username,
                                onSuccess = {
                                    stackedSnackBarHostState.showSuccessSnackbar(
                                        title = "Removing succeeded",
                                        description = "Member removed successfully",
                                        duration = StackedSnackbarDuration.Short
                                    )
                                },
                                onError = {
                                    stackedSnackBarHostState.showErrorSnackbar(
                                        title = "Removing Member failed",
                                        description = "Something went error try again later",
                                        duration = StackedSnackbarDuration.Short
                                    )
                                }
                            )
                        } else {
                            stackedSnackBarHostState.showInfoSnackbar(
                                title = "Failed",
                                description = "No internet connection",
                                duration = StackedSnackbarDuration.Short
                            )
                        }
                    },
                    onExit = {
                        if (isConnected) {
                            collabsViewModel.exitCollab(
                                user = userAccount,
                                collab = collabs.currentCollab!!,
                                onError = {
                                    stackedSnackBarHostState.showErrorSnackbar(
                                        title = "Leaving collab failed",
                                        description = "Something went wrong try again later",
                                        duration = StackedSnackbarDuration.Short
                                    )
                                },
                                onSuccess = {
                                    navHostController.navigate(NavRoutes.Home.route)
                                    stackedSnackBarHostState.showSuccessSnackbar(
                                        title = "Leaving collab succeeded",
                                        description = "You left the collab successfully",
                                        duration = StackedSnackbarDuration.Short
                                    )
                                }
                            )
                        } else {
                            stackedSnackBarHostState.showInfoSnackbar(
                                title = "Failed",
                                description = "No internet connection",
                                duration = StackedSnackbarDuration.Short
                            )
                        }
                        showMembersDialog = false

                    },
                    onDismiss = { showMembersDialog = false }
                )
            }
            if (showOperationsDialog) {
                OperationsDialog(
                    isAdmin = collabs.currentCollab?.admins?.contains(userAccount?.toMemberData()) == true,
                    operations = collabs.currentCollab!!.operations,
                    onDismiss = {
                        showOperationsDialog = false
                    },
                    onClearClick = {
                        if (isConnected) {
                            collabsViewModel.clearOperationsHistory(
                                collabId = collabs.currentCollab!!.id,
                                onSuccess = {}, onError = {
                                    stackedSnackBarHostState.showErrorSnackbar(
                                        title = "Clearing operations failed",
                                        duration = StackedSnackbarDuration.Short
                                    )
                                }
                            )
                        } else {
                            stackedSnackBarHostState.showInfoSnackbar(
                                title = "Failed",
                                description = "No internet connection",
                                duration = StackedSnackbarDuration.Short
                            )
                        }
                    },
                    onDeleteOperationClick = {
                        collabsViewModel.deleteOperation(
                            collabId = collabs.currentCollab!!.id,
                            onSuccess = {},
                            operationId = it,
                            onError = {
                                stackedSnackBarHostState.showErrorSnackbar(
                                    "Deleting failed",
                                    "Something went wrong try again later",
                                    duration = StackedSnackbarDuration.Short
                                )
                            }
                        )
                    }
                )
            }
        }
    }
}





