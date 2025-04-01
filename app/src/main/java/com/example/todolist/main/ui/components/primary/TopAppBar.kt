package com.example.todolist.main.ui.components.primary

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.main.ui.screens.CollabColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenTopBar(
    context: Context,
    onIconClick: () -> Unit,
    showTopBarButtons: Boolean,
    title: String,
    onModelDrawerClicked: () -> Unit,
    drawerState: DrawerState,
    icon: @Composable (Modifier) -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    onImportClick: () -> Unit,
    isSync: Boolean,
    isConnected: Boolean,
) {
    val isDrawerOpened = animateFloatAsState(
        targetValue = if (drawerState.isOpen) -90f else 1f,
    )
    var isImportEnabled by rememberSaveable { mutableStateOf(true) }
    CenterAlignedTopAppBar(
        windowInsets = WindowInsets(top = 20.dp),
        colors = topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(0, 0, 20, 20))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF8A2BE2), Color(0xFF6A0DAD), Color(0xFF1B1464)),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 500f) // Diagonal effect
                ),
                shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp) // Soft curves
            ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onModelDrawerClicked
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.rotate(isDrawerOpened.value)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(1f)

                        .clickable {
                            if (showTopBarButtons) {
                                onIconClick()
                            }
                        },
                    textAlign = TextAlign.Center
                )
                if (showTopBarButtons) {
                    IconButton(
                        onClick = {
                            if (!isConnected) {
                                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                                context.startActivity(intent)
                            }
                        }) {
                        Icon(
                            imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = if (isConnected) Color.Green else Color.Gray,
                        )
                    }
                    IconButton(
                        enabled = isImportEnabled,
                        onClick = {
                            if (isConnected) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    isImportEnabled = false
                                    onImportClick()
                                    delay(2500)
                                    isImportEnabled = true
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "No internet connection",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (!isSync) Icons.Default.Sync else Icons.Default.PublishedWithChanges,
                            contentDescription = "Import",
                            tint = if (!isSync) Color.Gray else Color.Green
                        )
                    }

                }

                icon(
                    Modifier
                        .padding(5.dp)
                        .size(50.dp)
                        .clip(CircleShape)
                )
            }


        },

        actions = actions,
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollaborationsTopAppBar(
    context: Context,
    onIconClick: () -> Unit,
    showTopBars: Boolean,
    title: String,
    onModelDrawerClicked: () -> Unit,
    drawerState: DrawerState,
    icon: @Composable (Modifier) -> Unit,
    onImportClick: () -> Unit,
    isConnected: Boolean,
) {
    val isDrawerOpened = animateFloatAsState(
        targetValue = if (drawerState.isOpen) -90f else 1f,
    )
    CenterAlignedTopAppBar(
        windowInsets = WindowInsets(top = 20.dp),
        colors = topAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(0, 0, 20, 20))
            .background(
                brush = Brush.linearGradient(
                    listOf(CollabColors.TopBarStart, CollabColors.TopBarEnd)
                ),
                shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp) // Soft curves
            ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onModelDrawerClicked
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.rotate(isDrawerOpened.value)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable {
                            if (showTopBars) {
                                onIconClick()
                            }
                        },
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.weight(1f))


                if (showTopBars) {
                    IconButton(
                        onClick = {
                            if (!isConnected) {
                                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                                context.startActivity(intent)
                            }
                        }) {
                        Icon(
                            imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = if (isConnected) Color.Green else Color.Gray,
                        )
                    }
                    IconButton(
                        enabled = true,
                        onClick = {
                            onImportClick.invoke()
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "History",
                            tint = Color.White
                        )
                    }

                }
                icon(
                    Modifier.padding(5.dp).size(50.dp).clip(CircleShape)
                )
            }


        },
    )
}