package com.example.todolist.main.navigation

import StackedSnakbarHostState
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.todolist.backend.auth.AuthenticatedUserData
import com.example.todolist.main.ui.components.primary.CollaborationsTopAppBar
import com.example.todolist.main.ui.components.primary.MainScreenTopBar
import com.example.todolist.main.ui.components.secondry.CoilImageFormatter
import com.example.todolist.main.ui.screens.AboutScreen
import com.example.todolist.main.ui.screens.CollaborationScreen
import com.example.todolist.main.ui.screens.HomeScreen
import com.example.todolist.main.ui.screens.SplashScreen
import com.example.todolist.main.ui.screens.TaskScreen
import com.example.todolist.main.viewmodels.CollaborationViewModel
import com.example.todolist.main.viewmodels.CollabsState
import com.example.todolist.main.viewmodels.DetailsScreenViewModel
import com.example.todolist.main.viewmodels.HomeScreenViewModel
import com.example.todolist.main.viewmodels.UserAccountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavHandler(
    modifier: Modifier = Modifier,
    snackBarScope: CoroutineScope,
    navHostController: NavHostController,

    collaborationViewModel: CollaborationViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    userAccountViewModel: UserAccountViewModel,
    stackedSnackbarState: StackedSnakbarHostState,
    detailsScreenViewModel: DetailsScreenViewModel = viewModel(),

    collabs: CollabsState,
    drawerState: DrawerState,
    userAccount: AuthenticatedUserData?,


    isConnected: Boolean,
    isSignIn: Boolean,

    onGFCclicked: () -> Unit,
    onNotSignedClick: () -> Unit,
    onShowProfileDialog: () -> Unit,
    onShowOperationsDialog: () -> Unit,
    onSplashScreenFinish: () -> Unit,

    onShowMembersDialog: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val appSyncState by homeScreenViewModel.appSyncState.collectAsStateWithLifecycle()
    val currentTaskState by detailsScreenViewModel.currentTask.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = NavRoutes.Start.route,
    ) {

        composable(NavRoutes.About.route) {
            AboutScreen {
                navHostController.popBackStack()
            }
        }

        composable(NavRoutes.Home.route) {
            HomeScreen(
                stackedSnackbarState = stackedSnackbarState,
                isConnected = isConnected,
                userAccountViewModel = userAccountViewModel,
                homeScreenViewModel = homeScreenViewModel,
                onGFCClicked = onGFCclicked,
                onClick = {
                    detailsScreenViewModel.updateCurrentTask(it)
                    navHostController.navigate(NavRoutes.Task.route)
                },
                backHandler = {
                    activity?.finish()
                },
                topBar = {
                    MainScreenTopBar(
                        showTopBarButtons = isSignIn,
                        title = userAccount?.username ?: "Taskly",
                        icon = {
                            CoilImageFormatter.ProfileImage(
                                onNotSignedClick = onNotSignedClick,
                                image = userAccount?.profilePictureUrl ?: "",
                                modifier = it,
                                onClick = onShowProfileDialog
                            )
                        },
                        onImportClick = {
                            snackBarScope.launch {
                                homeScreenViewModel.importData(
                                    userId = userAccount?.userId,
                                    onErrorAction = {
                                        stackedSnackbarState.showErrorSnackbar(
                                            title = "Updating tasks failed",
                                            description = "Something went wrong"
                                        )
                                    },
                                    onSuccessAction = {
                                        stackedSnackbarState.showSuccessSnackbar(
                                            title = "Updating Succeeded",
                                            description = "Updating your tasks succeeded"
                                        )
                                    },
                                    isConnected = isConnected
                                )
                            }
                        },
                        isSync = appSyncState.isSynced,
                        onIconClick = onShowProfileDialog,
                        context = context,
                        isConnected = isConnected,
                        drawerState = drawerState,
                        onModelDrawerClicked = {
                            snackBarScope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }
            )
        }

        composable(route = NavRoutes.Task.route) {
            TaskScreen(
                task = currentTaskState!!,
                onNavBack = {
                    navHostController.popBackStack()
                },
                onSave = { title, description ->
                    scope.launch {
                        if (userAccount != null) {
                            homeScreenViewModel.updateTask(
                                isConnected = isConnected,
                                task = currentTaskState!!.copy(
                                    title = title,
                                    description = description
                                ),
                                userId = userAccount.userId,
                                onError = { error ->
                                    stackedSnackbarState.showErrorSnackbar(
                                        title = "Something went wrong!",
                                        duration = StackedSnackbarDuration.Short
                                    )
                                },
                                onSuccessAction = {
                                    stackedSnackbarState.showSuccessSnackbar(
                                        title = "\"$title\" updated successfully!",
                                        duration = StackedSnackbarDuration.Short
                                    )
                                }
                            )
                        } else {
                            homeScreenViewModel.updateOfflineTask(
                                currentTaskState!!.copy(title = title, description = description)
                            )
                        }
                        navHostController.navigate(NavRoutes.Home.route)
                    }
                }
            )
        }

        composable(route = NavRoutes.Collaboration.route) {
            CollaborationScreen(
                collaborationViewModel = collaborationViewModel,
                userAccountViewModel = userAccountViewModel,
                stackedSnackbarHostState = stackedSnackbarState,
                isConnected = isConnected,
                backHandler = {
                    if (navHostController.previousBackStackEntry?.destination?.route == NavRoutes.Start.route) {
                        activity?.finish()
                    } else {
                        navHostController.navigate(NavRoutes.Home.route)
                    }
                },
                topAppBar = {
                    CollaborationsTopAppBar(
                        showTopBars = isSignIn,
                        icon = {
                            IconButton(
                                onClick = onShowMembersDialog,
                                modifier = Modifier
                                    .padding(horizontal = 5.dp, vertical = 10.dp)
                                    .clip(
                                        CircleShape
                                    )
                                    .background(Color.White)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Group,
                                    contentDescription = "Group",
                                    tint = Color.Black,
                                    modifier = it
                                )
                            }
                        },
                        context = context,
                        onIconClick = {},
                        title = collabs.currentCollab?.username ?: "Collab",
                        onModelDrawerClicked = {
                            snackBarScope.launch {
                                drawerState.open()
                            }
                        },
                        drawerState = drawerState,
                        onImportClick = onShowOperationsDialog,
                        isConnected = isConnected,
                    )
                }
            )
        }

        composable(
            route = NavRoutes.Start.route
        ) {
            SplashScreen(
                onLoadingComplete = {
                    onSplashScreenFinish.invoke()
                    navHostController.navigate(route = NavRoutes.Home.route)
                },
                homeViewModel = homeScreenViewModel,
                userAccountViewModel = userAccountViewModel,
                isConnected = isConnected
            )
        }
    }
}