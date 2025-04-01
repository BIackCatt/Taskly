package com.example.todolist.main.ui.screens

import androidx.compose.ui.graphics.Color


object CollabColors {
    // 🔵 Top Bar Gradient Colors
    val TopBarStart = Color(0xFF0D47A1) // Deep Blue (Start)
    val TopBarEnd = Color(0xFF1976D2) // Medium Blue (End)

    // 🔵 Bottom Navigation Bar Color
    val BottomBar = Color(0xFF0D47A1) // Dark Blue

    // 🔵 Task Card Gradient
    val UncompletedTask = Color(0xFF0072CB) // Light Blue (Start)

    // 🔵 Current User's Task Card (Highlighting user’s own tasks)
    val CompletedTask = Color(0x9C45BD17) // Bright Blue

    // ✅ Completed Task Card (To indicate completion)
    val CurrentUserTask = Color(0xFF357F73) // Blue-Green

    // 🎨 Background Color
    val Background = Color(0xFFE3F2FD) // Soft Light Blue

    // ✨ Text Colors
    val PrimaryText = Color(0xFFFFFFFF) // White for contrast
    val SecondaryText = Color(0xFFB0BEC5) // Grayish Blue for secondary text
}

object CollabDialogColors {
    // 🌫️ Background Gradient for Dialog
    val DialogStart = Color(0xFF1565C0) // Deep Blue (Left Side)
    val DialogEnd = Color(0xFF1E88E5) // Bright Blue (Right Side)

    // 🔵 Input Field Colors
    val InputText = Color.White // White Text
    val InputPlaceholder = Color.White.copy(alpha = 0.7f) // Lighter White for Placeholder
    val InputLabel = Color.White.copy(alpha = 0.8f) // Slightly Dimmed White for Label
    val InputBorder = Color.White.copy(alpha = 0.8f) // Border Color for Input Fields
    val CursorColor = Color.White // White Cursor for Input Fields

    // 🆗 Primary Action Button Colors
    val PrimaryButton = Color(0xFF0D47A1) // Dark Blue
    val PrimaryButtonText = Color.White // White Button Text
    val PrimaryButtonDisabled = Color(0xFF5C6BC0) // Muted Blue for Disabled Button

    // ❌ Dismiss Button Colors
    val DismissButton = Color.Transparent // Transparent Background for Text Button
    val DismissButtonText = Color.White // White Text for "Cancel"

    // 🎨 Icon Colors
    val IconColor = Color.White.copy(alpha = 0.8f) // Semi-Transparent White for Icons

    // ✨ General Text Colors
    val PrimaryText = Color.White // White for Primary Text
    val SecondaryText = Color(0xFFB3E5FC) // Light Blue for Secondary Text

    // 🌊 Blur Effect Background (Frosted Glass Look)
    val BlurBackground = Color(0xFF0D47A1).copy(alpha = 0.2f) // Very Transparent Deep Blue
}
