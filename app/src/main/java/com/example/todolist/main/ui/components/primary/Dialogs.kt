package com.example.todolist.main.ui.components.primary

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ReplyAll
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoDisturbOn
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.util.DebugLogger
import com.example.todolist.backend.model.CollabTask
import com.example.todolist.backend.model.Collaboration
import com.example.todolist.backend.model.Operation
import com.example.todolist.backend.auth.AuthenticatedUserData
import com.example.todolist.main.ui.screens.CollabColors
import com.example.todolist.main.ui.screens.CollabDialogColors
import com.example.todolist.main.viewmodels.TaskUiState
import java.text.SimpleDateFormat

@Composable
fun AddTaskDialog(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (TaskUiState) -> Unit
) {
    val textFieldUnFocusedColor = Color.LightGray
    val textFieldFocusedColor = Color(0xB9EED8FF)
    AlertDialog(
        containerColor = Color.Transparent,
        titleContentColor = CollabColors.PrimaryText,
        textContentColor = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(20, 0, 20, 0))
            .background(Color(0xFF1B1464)),
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
        ),
        onDismissRequest = onDismiss,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Add new task",
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title input field
                OutlinedTextField(
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = textFieldFocusedColor,
                        focusedTextColor = textFieldFocusedColor,
                        focusedLeadingIconColor = textFieldFocusedColor,
                        focusedSupportingTextColor = textFieldFocusedColor,

                        unfocusedLabelColor  = textFieldUnFocusedColor,
                        unfocusedTextColor = textFieldUnFocusedColor,
                        unfocusedLeadingIconColor = textFieldUnFocusedColor,
                        unfocusedSupportingTextColor = textFieldUnFocusedColor

                    ),
                    label = { Text("Task Title") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Title,
                            contentDescription = "title"
                        )
                    },
                    supportingText = {
                        Text(
                            "Required",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    textStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        textDirection = TextDirection.ContentOrLtr,
                        color = Color.White,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    ),
                    value = title,
                    onValueChange = onTitleChange,
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true
                )
                // Description input field
                OutlinedTextField(
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = textFieldFocusedColor,
                        focusedTextColor = textFieldFocusedColor,
                        focusedLeadingIconColor = textFieldFocusedColor,
                        focusedSupportingTextColor = textFieldFocusedColor,

                        unfocusedLabelColor  = textFieldUnFocusedColor,
                        unfocusedTextColor = textFieldUnFocusedColor,
                        unfocusedLeadingIconColor = textFieldUnFocusedColor,
                        unfocusedSupportingTextColor = textFieldUnFocusedColor

                    ),
                    textStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        color = Color.White,
                        textDirection = TextDirection.ContentOrLtr,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    ),
                    label = { Text("Description") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Description,
                            contentDescription = "Description"
                        )
                    },
                    supportingText = {
                        Text(
                            "Optional",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = description,
                    onValueChange = onDescriptionChange,
                    maxLines = 10
                )
            }
        },
        confirmButton = {
            var validTitle by remember { mutableStateOf(title.isNotBlank()) }
            LaunchedEffect(title) {
                validTitle = title.isNotBlank()
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    disabledContentColor = Color.Black,
                    disabledContainerColor = Color.Transparent
                ),
                onClick = {

                    onSave(TaskUiState(title = title, description = description))

                },
                enabled = validTitle,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (validTitle) listOf(
                                Color(0xFF8A2BE2),
                                Color(0xFF6A0DAD),
                                Color(0xFF1B1464)
                            ) else listOf(Color.LightGray, Color.Gray, Color.DarkGray),
                        )
                    )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Cancel", style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernProfileDialog(
    fullName: String,
    email: String,
    onCancel: () -> Unit,
    onSignOut: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Title
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                // Full Name Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Full Name",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Full Name",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                        Text(
                            text = fullName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                // Divider
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                )

                // Email Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                // Buttons Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    // Cancel Button
                    TextButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    ) {
                        Text(text = "Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Sign Out Button
                    Button(
                        onClick = {
                            onSignOut()
                            onCancel()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign Out",
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sign Out")
                    }
                }
            }
        }
    }
}


@Composable
fun ModernSignInDialog(
    onSignIn: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Title
                Text(
                    text = "Sign In Required",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                // Message
                Text(
                    text = "Sign in with Google to save your taskz online",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                GoogleButtonFab(
                    onClick = {
                        onSignIn()
                        onDismiss()
                    },
                    label = "Sign In With Google",
                    padding = 10.dp
                )

                // Cancel Button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(top = 5.dp),
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
fun AddCollabDialog(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (Collaboration) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var showPassword by remember { mutableStateOf(false) }
    val passwordCriteria = listOf(
        "At least 6 characters" to (password.length >= 6),
        "Contains a lowercase letter" to password.any { it.isLowerCase() },
        "Contains a number" to password.any { it.isDigit() },
    )
    val progress by animateFloatAsState(
        targetValue = passwordCriteria.count { it.second } / passwordCriteria.size.toFloat(),
        animationSpec = tween(durationMillis = 1000)
    )

    AlertDialog(
        containerColor = Color.Transparent,
        titleContentColor = CollabColors.PrimaryText,
        textContentColor = Color.White,
        iconContentColor = CollabColors.PrimaryText.copy(alpha = 0.8f),
        modifier = Modifier
            .clip(RoundedCornerShape(20, 0, 20, 0))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        CollabDialogColors.DialogStart,
                        CollabDialogColors.DialogEnd
                    )
                )
            ),
        icon = {
            Icon(
                imageVector = Icons.Default.GroupAdd,
                contentDescription = "Add New Task",
            )
        },
        onDismissRequest = onDismiss,
        title = { Text("Create collab") },
        text = {
            Column {
                OutlinedTextField(
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CollabDialogColors.InputText,
                        focusedPlaceholderColor = CollabDialogColors.InputPlaceholder,
                        focusedLabelColor = CollabDialogColors.InputLabel,
                        focusedBorderColor = CollabDialogColors.InputBorder,
                        unfocusedTextColor = CollabDialogColors.InputText.copy(0.6f),
                        unfocusedPlaceholderColor = CollabDialogColors.InputPlaceholder.copy(0.5f),
                        unfocusedLabelColor = CollabDialogColors.InputLabel.copy(0.6f),
                        unfocusedBorderColor = CollabDialogColors.InputBorder.copy(0.6f),
                        cursorColor = CollabDialogColors.CursorColor
                    ),
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("Collab name *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CollabDialogColors.InputText,
                        focusedPlaceholderColor = CollabDialogColors.InputPlaceholder,
                        focusedLabelColor = CollabDialogColors.InputLabel,
                        focusedBorderColor = CollabDialogColors.InputBorder,
                        unfocusedTextColor = CollabDialogColors.InputText.copy(0.6f),
                        unfocusedPlaceholderColor = CollabDialogColors.InputPlaceholder.copy(0.5f),
                        unfocusedLabelColor = CollabDialogColors.InputLabel.copy(0.6f),
                        unfocusedBorderColor = CollabDialogColors.InputBorder.copy(0.6f),
                        cursorColor = CollabDialogColors.CursorColor
                    ),
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility",
                                tint = CollabDialogColors.IconColor
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    color = Color.Transparent,
                    trackColor = Color.LightGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFF4A05AA),
                                    Color(0xFF2A0E8D),
                                    Color(0xFF1500F3)
                                )
                            )
                        ),
                )
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    passwordCriteria.forEach { (rule, met) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (met) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (met) Color.Green else Color.Red
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(rule, color = CollabDialogColors.PrimaryText)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        Collaboration(
                            username = username.trim(),
                            password = password
                        )
                    )
                },
                enabled = passwordCriteria.all { it.second } && username.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CollabDialogColors.PrimaryButton,
                    contentColor = CollabDialogColors.PrimaryButtonText,
                    disabledContainerColor = CollabDialogColors.PrimaryButtonDisabled,
                    disabledContentColor = CollabDialogColors.PrimaryButtonText.copy(alpha = 0.38f)
                )
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun JoinCollabDialog(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var showPassword by remember { mutableStateOf(false) }
    AlertDialog(
        containerColor = Color.Transparent,
        titleContentColor = CollabColors.PrimaryText,
        textContentColor = Color.White,
        iconContentColor = CollabColors.PrimaryText.copy(alpha = 0.8f),
        modifier = Modifier
            .clip(RoundedCornerShape(20, 0, 20, 0))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        CollabDialogColors.DialogStart,
                        CollabDialogColors.DialogEnd
                    )
                )
            ),
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ReplyAll,
                contentDescription = "Join collab",
            )
        },
        onDismissRequest = onDismiss,
        title = { Text("Join collab", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column {
                // Title input field
                OutlinedTextField(
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CollabDialogColors.InputText,
                        focusedPlaceholderColor = CollabDialogColors.InputPlaceholder,
                        focusedLabelColor = CollabDialogColors.InputLabel,
                        focusedBorderColor = CollabDialogColors.InputBorder,
                        unfocusedTextColor = CollabDialogColors.InputText.copy(0.6f),
                        unfocusedPlaceholderColor = CollabDialogColors.InputPlaceholder.copy(0.5f),
                        unfocusedLabelColor = CollabDialogColors.InputLabel.copy(0.6f),
                        unfocusedBorderColor = CollabDialogColors.InputBorder.copy(0.6f),
                        cursorColor = CollabDialogColors.CursorColor
                    ),
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("Collab name *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description input field
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CollabDialogColors.InputText,
                        focusedPlaceholderColor = CollabDialogColors.InputPlaceholder,
                        focusedLabelColor = CollabDialogColors.InputLabel,
                        focusedBorderColor = CollabDialogColors.InputBorder,
                        unfocusedTextColor = CollabDialogColors.InputText.copy(0.6f),
                        unfocusedPlaceholderColor = CollabDialogColors.InputPlaceholder.copy(0.5f),
                        unfocusedLabelColor = CollabDialogColors.InputLabel.copy(0.6f),
                        unfocusedBorderColor = CollabDialogColors.InputBorder.copy(0.6f),
                        cursorColor = CollabDialogColors.CursorColor
                    ),
                    label = { Text("password *") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility",
                                tint = CollabDialogColors.IconColor
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(listOf(username.trim(), password))
                },
                enabled = username.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CollabDialogColors.PrimaryButton,
                    contentColor = CollabDialogColors.PrimaryButtonText,
                    disabledContainerColor = CollabDialogColors.PrimaryButtonDisabled,
                    disabledContentColor = CollabDialogColors.PrimaryButtonText.copy(alpha = 0.38f)
                ) // Enable the button only if the title is not empty
            ) {
                Text("Join")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss, colors = ButtonDefaults.textButtonColors(
                    contentColor = CollabDialogColors.DismissButtonText
                )
            ) {
                Text("Cancel")
            }
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}


@Composable
fun AddCollabTaskDialog(
    title: String,
    isEdit: Boolean,
    task: CollabTask? = null,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (CollabTask) -> Unit,
    userAccount: AuthenticatedUserData
) {
    val focusRequester = remember { FocusRequester() }
    AlertDialog(
        containerColor = Color.Transparent,
        titleContentColor = CollabColors.PrimaryText,
        textContentColor = Color.White,
        iconContentColor = CollabColors.PrimaryText.copy(alpha = 0.8f),
        modifier = Modifier
            .clip(RoundedCornerShape(20, 0, 20, 0))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        CollabDialogColors.DialogStart,
                        CollabDialogColors.DialogEnd
                    )
                )
            ),
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column {
                // Title input field
                OutlinedTextField(
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CollabDialogColors.InputText,
                        focusedPlaceholderColor = CollabDialogColors.InputPlaceholder,
                        focusedLabelColor = CollabDialogColors.InputLabel,
                        focusedBorderColor = CollabDialogColors.InputBorder,
                        unfocusedTextColor = CollabDialogColors.InputText.copy(0.6f),
                        unfocusedPlaceholderColor = CollabDialogColors.InputPlaceholder.copy(0.5f),
                        unfocusedLabelColor = CollabDialogColors.InputLabel.copy(0.6f),
                        unfocusedBorderColor = CollabDialogColors.InputBorder.copy(0.6f),
                        cursorColor = CollabDialogColors.CursorColor
                    ),
                    value = title,
                    textStyle = TextStyle(textDirection = TextDirection.ContentOrLtr),
                    onValueChange = onTitleChange,
                    label = { Text("Title *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description input field
                OutlinedTextField(
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CollabDialogColors.InputText,
                        focusedPlaceholderColor = CollabDialogColors.InputPlaceholder,
                        focusedLabelColor = CollabDialogColors.InputLabel,
                        focusedBorderColor = CollabDialogColors.InputBorder,
                        unfocusedTextColor = CollabDialogColors.InputText.copy(0.6f),
                        unfocusedPlaceholderColor = CollabDialogColors.InputPlaceholder.copy(0.5f),
                        unfocusedLabelColor = CollabDialogColors.InputLabel.copy(0.6f),
                        unfocusedBorderColor = CollabDialogColors.InputBorder.copy(0.6f),
                        cursorColor = CollabDialogColors.CursorColor
                    ),
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    textStyle = TextStyle(textDirection = TextDirection.ContentOrLtr),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = CollabDialogColors.PrimaryButton,
                    contentColor = CollabDialogColors.PrimaryButtonText,
                    disabledContainerColor = CollabDialogColors.PrimaryButtonDisabled,
                    disabledContentColor = CollabDialogColors.PrimaryButtonText.copy(alpha = 0.38f)
                ),
                onClick = {
                    if (isEdit) {
                        onSave(task!!.copy(title = title, description = description))
                    } else {
                        onSave(
                            CollabTask(
                                title = title,
                                description = description,
                                assignedTo = mapOf(
                                    "id" to userAccount.userId,
                                    "username" to userAccount.username,
                                    "profilePic" to userAccount.profilePictureUrl
                                )
                            )
                        )
                    }
                },
                enabled = title.isNotBlank(),
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss, colors = ButtonDefaults.textButtonColors(
                    contentColor = CollabDialogColors.DismissButtonText
                )
            ) {
                Text("Cancel")
            }
        }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun MemberItem(
    modifier: Modifier = Modifier,
    onExit: (String) -> Unit,
    member: Map<String, String?>,
    isCurrentUser: Boolean, // Whether the current user is the member
    isAdmin: Boolean, // Whether the current user is an admin
    isMemberAdmin: Boolean,
    onPromote: (id: String, username: String?) -> Unit, // Callback for promoting a member
    onRemove: (id: String, username: String?) -> Unit // Callback for removing a member
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Picture
        SubcomposeAsyncImage(
            modifier = modifier.size(50.dp).clip(CircleShape),
            model = ImageRequest.Builder(context)
                .data(member["profilePic"])
                .crossfade(true)
                .crossfade(300)
                .build(),
            contentDescription = "Profile Image",
            imageLoader = ImageLoader.Builder(context)
                .diskCachePolicy(CachePolicy.ENABLED)
                .logger(DebugLogger())
                .build(),
            success = { state ->
                Image(
                    painter = state.painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            },
            loading = {
                CircularProgressIndicator(modifier = modifier)
            },
            error = { error ->
                Log.e("ImageError", error.result.throwable.toString())

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    tint = CollabColors.PrimaryText,
                    contentDescription = null,
                )
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Name and Rank
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isCurrentUser) "You" else member["username"] as String,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = if (isMemberAdmin) "Admin" else "Member",
                fontSize = 12.sp,
                color = CollabDialogColors.SecondaryText
            )
        }

        // Actions (Only show for admins and non-current users)
        if (isAdmin) {
            // Current user is an admin
            if (isCurrentUser) {
                // Current user is an admin and the member is themselves
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onExit(member["id"] as String) }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "exit",
                        tint = CollabDialogColors.SecondaryText
                    )
                }
            } else if (!isMemberAdmin) {
                // Current user is an admin and the member is not an admin
                Row {
                    IconButton(onClick = {
                        onPromote(
                            (member["id"] as String),
                            (member["username"] as String)
                        )
                    }) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Promote")
                    }
                    IconButton(onClick = {
                        onRemove(
                            (member["id"] as String),
                            (member["username"] as String)
                        )
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove")
                    }
                }
            }
            // If the member is an admin and not the current user, do nothing (no UI changes)
        } else {
            // Current user is not an admin
            if (isCurrentUser) {
                // Current user is not an admin and the member is themselves
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onExit(member["id"] as String) }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "exit",
                        tint = CollabDialogColors.SecondaryText
                    )
                }
            }
            // If the current user is not an admin and the member is not themselves, do nothing (no UI changes)
        }
    }
}


@Composable
fun MembersDialog(
    members: List<Map<String, String?>?>,
    currentUser: Map<String, String?>,
    admins: List<Map<String, String?>?>, // Whether the current user is an admin
    onPromoteMember: (id: String, username: String?) -> Unit, // Callback for promoting a member
    onRemoveMember: (id: String, username: String?) -> Unit, // Callback for removing a member
    onDismiss: () -> Unit, // Callback for dismissing the dialog
    onExit: (String) -> Unit,
) {
    AlertDialog(
        containerColor = Color.Transparent,
        titleContentColor = CollabColors.PrimaryText,
        textContentColor = Color.White,
        iconContentColor = CollabColors.PrimaryText.copy(alpha = 0.8f),
        modifier = Modifier
            .clip(RoundedCornerShape(20, 0, 20, 0))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        CollabDialogColors.DialogStart,
                        CollabDialogColors.DialogEnd
                    )
                )
            ),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Members",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (members.isEmpty()) {
                    Text(text = "No mates yet")
                } else {
                    members.forEach { member ->
                        MemberItem(
                            member = member!!,
                            isAdmin = currentUser in admins,
                            onPromote = onPromoteMember,
                            onRemove = onRemoveMember,
                            isCurrentUser = member["id"] == currentUser["id"],
                            isMemberAdmin = member in admins,
                            onExit = onExit
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CollabDialogColors.PrimaryButtonText,
                    contentColor = CollabDialogColors.PrimaryButton,
                    disabledContainerColor = CollabDialogColors.PrimaryButtonDisabled,
                    disabledContentColor = CollabDialogColors.PrimaryButtonText.copy(alpha = 0.38f)
                )
            ) {
                Text("Close")
            }
        }
    )
}


@Composable
fun OperationsDialog(
    onDeleteOperationClick: (String) -> Unit,
    isAdmin: Boolean,
    operations: List<Operation?>,
    onClearClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        containerColor = Color.Transparent,
        titleContentColor = CollabColors.PrimaryText,
        textContentColor = Color.White,
        iconContentColor = CollabColors.PrimaryText.copy(alpha = 0.8f),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
        modifier = Modifier
            .background(
                Brush.horizontalGradient(
                    listOf(
                        CollabDialogColors.DialogStart,
                        CollabDialogColors.DialogEnd
                    )
                )
            ),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Logs",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row {
                    // Dialog title
                    Text(
                        text = "Collab History",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.White
                    )
                    Spacer(Modifier.weight(1f))
                    if (isAdmin) {
                        IconButton(
                            onClick = onClearClick,
                            enabled = operations.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DoDisturbOn,
                                contentDescription = null
                            )
                        }
                    }
                }
                if (operations.isNotEmpty()) {
                    LazyColumn {
                        items(
                            items = operations,
                            key = { operation -> operation?.id ?: "" }) { operation ->
                            OperationItem(
                                operation!!, isAdmin, onDeleteOperationClick
                            )
                            HorizontalDivider(
                                Modifier.padding(vertical = 5.dp),
                                color = Color.White
                            )
                        }
                    }
                } else {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EditNote,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 0.dp),
                                tint = Color.White
                            )
                            Text(
                                text = "No operations yet", textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CollabDialogColors.PrimaryButtonText,
                    contentColor = CollabDialogColors.PrimaryButton,
                    disabledContainerColor = CollabDialogColors.PrimaryButtonDisabled,
                    disabledContentColor = CollabDialogColors.PrimaryButtonText.copy(alpha = 0.38f)
                )
            ) {
                Text("Close")
            }
        }
    )
}

@SuppressLint("SimpleDateFormat")
@Composable
fun OperationItem(operation: Operation, isAdmin: Boolean, onDelete: (String) -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User profile picture
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context = context)
                    .data(operation.userPic)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Operation details
        Column(modifier = Modifier.weight(0.80f)) {
            Column {
                Text(
                    text = (operation.username ?: "Unknown member") + " :",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = operation.message ?: "Something went wrong",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(Modifier.padding(vertical = 5.dp))
            Text(
                text = SimpleDateFormat("hh:mm a, dd MMM yyyy").format(operation.timestamp!!), // Format the Date
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        if (isAdmin) {
            IconButton(
                onClick = {
                    onDelete.invoke(operation.id)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    modifier = Modifier.weight(0.10f)
                )
            }
        }
    }
}
