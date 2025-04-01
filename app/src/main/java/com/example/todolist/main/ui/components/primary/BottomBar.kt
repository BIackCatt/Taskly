package com.example.todolist.main.ui.components.primary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.outlined.GroupWork
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material.icons.outlined.SpaceDashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.todolist.main.ui.screens.CollabColors

data class BottomNavigationBarItem(
    val selected: ImageVector,
    val unselected: ImageVector,
    val color: Color,
    val label: String,
    val pageCount: Int,
)

@Composable
fun BottomNavigation(
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,

    pageIndex: Int,
) {
    val items = listOf(
        BottomNavigationBarItem(
            selected = Icons.Filled.SpaceDashboard,
            unselected = Icons.Outlined.SpaceDashboard,
            label = "All Taskz",
            color = Color.White,
            pageCount = 0
        ),
        BottomNavigationBarItem(
            selected = Icons.Filled.CheckCircle,
            unselected = Icons.Filled.CheckCircleOutline,
            label = "Completed",
            color = Color.Green,
            pageCount = 2,
        ),
        BottomNavigationBarItem(
            selected = Icons.Filled.Pending,
            unselected = Icons.Outlined.Pending,
            label = "Uncompleted",
            color = Color(0xFFD9D0FF),
            pageCount = 1
        )
    )
    NavigationBar(
        containerColor = Color.Transparent,
        windowInsets = WindowInsets(0.dp),
        tonalElevation = 5.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(20, 20, 0, 0))
            .background(Color(0xFF6A0DAD))
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.LightGray,
                    indicatorColor = item.color.copy(alpha = 0.3f),
                    selectedTextColor = item.color,
                    selectedIconColor = item.color,
                ),
                onClick = {
                    onClick(index)
                },
                selected = index == pageIndex,
                icon = {
                    Icon(
                        imageVector = if (pageIndex == index) item.selected else item.unselected,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1
                    )
                },
            )
        }
    }
}


@Composable
fun CollabsBottomNavigation(
    modifier: Modifier = Modifier,
    pageIndex: Int,
    onClick: (Int) -> Unit,
) {
    val items = listOf(
        BottomNavigationBarItem(
            selected = Icons.Filled.GroupWork,
            unselected = Icons.Outlined.GroupWork,
            label = "Collab Taskz",
            color = Color.White,
            pageCount = 0
        ),
        BottomNavigationBarItem(
            selected = Icons.Filled.CheckCircle,
            unselected = Icons.Filled.CheckCircleOutline,
            label = "Completed",
            color = CollabColors.CompletedTask,
            pageCount = 2,
        ),
        BottomNavigationBarItem(
            selected = Icons.Filled.Pending,
            unselected = Icons.Outlined.Pending,
            label = "Uncompleted",
            color = Color(0xFFBAC4FF),
            pageCount = 1
        )
    )
    NavigationBar(
        containerColor = CollabColors.BottomBar,
        windowInsets = WindowInsets(0.dp),
        tonalElevation = 5.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(20, 20, 0, 0))
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.LightGray,
                    indicatorColor = item.color.copy(alpha = 0.3f),
                    selectedTextColor = item.color,
                    selectedIconColor = item.color,
                ),
                onClick = {
                    onClick(index)
                },
                selected = index == pageIndex,
                icon = {
                    Icon(
                        imageVector = if (pageIndex == index) item.selected else item.unselected,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1
                    )
                },
            )
        }
    }
}