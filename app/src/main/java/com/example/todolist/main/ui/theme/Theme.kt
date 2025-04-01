package com.example.compose
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.todolist.main.ui.theme.backgroundDark
import com.example.todolist.main.ui.theme.backgroundDarkHighContrast
import com.example.todolist.main.ui.theme.backgroundDarkMediumContrast
import com.example.todolist.main.ui.theme.backgroundLight
import com.example.todolist.main.ui.theme.backgroundLightHighContrast
import com.example.todolist.main.ui.theme.backgroundLightMediumContrast
import com.example.todolist.main.ui.theme.errorContainerDark
import com.example.todolist.main.ui.theme.errorContainerDarkHighContrast
import com.example.todolist.main.ui.theme.errorContainerDarkMediumContrast
import com.example.todolist.main.ui.theme.errorContainerLight
import com.example.todolist.main.ui.theme.errorContainerLightHighContrast
import com.example.todolist.main.ui.theme.errorContainerLightMediumContrast
import com.example.todolist.main.ui.theme.errorDark
import com.example.todolist.main.ui.theme.errorDarkHighContrast
import com.example.todolist.main.ui.theme.errorDarkMediumContrast
import com.example.todolist.main.ui.theme.errorLight
import com.example.todolist.main.ui.theme.errorLightHighContrast
import com.example.todolist.main.ui.theme.errorLightMediumContrast
import com.example.todolist.main.ui.theme.inverseOnSurfaceDark
import com.example.todolist.main.ui.theme.inverseOnSurfaceDarkHighContrast
import com.example.todolist.main.ui.theme.inverseOnSurfaceDarkMediumContrast
import com.example.todolist.main.ui.theme.inverseOnSurfaceLight
import com.example.todolist.main.ui.theme.inverseOnSurfaceLightHighContrast
import com.example.todolist.main.ui.theme.inverseOnSurfaceLightMediumContrast
import com.example.todolist.main.ui.theme.inversePrimaryDark
import com.example.todolist.main.ui.theme.inversePrimaryDarkHighContrast
import com.example.todolist.main.ui.theme.inversePrimaryDarkMediumContrast
import com.example.todolist.main.ui.theme.inversePrimaryLight
import com.example.todolist.main.ui.theme.inversePrimaryLightHighContrast
import com.example.todolist.main.ui.theme.inversePrimaryLightMediumContrast
import com.example.todolist.main.ui.theme.inverseSurfaceDark
import com.example.todolist.main.ui.theme.inverseSurfaceDarkHighContrast
import com.example.todolist.main.ui.theme.inverseSurfaceDarkMediumContrast
import com.example.todolist.main.ui.theme.inverseSurfaceLight
import com.example.todolist.main.ui.theme.inverseSurfaceLightHighContrast
import com.example.todolist.main.ui.theme.inverseSurfaceLightMediumContrast
import com.example.todolist.main.ui.theme.onBackgroundDark
import com.example.todolist.main.ui.theme.onBackgroundDarkHighContrast
import com.example.todolist.main.ui.theme.onBackgroundDarkMediumContrast
import com.example.todolist.main.ui.theme.onBackgroundLight
import com.example.todolist.main.ui.theme.onBackgroundLightHighContrast
import com.example.todolist.main.ui.theme.onBackgroundLightMediumContrast
import com.example.todolist.main.ui.theme.onErrorContainerDark
import com.example.todolist.main.ui.theme.onErrorContainerDarkHighContrast
import com.example.todolist.main.ui.theme.onErrorContainerDarkMediumContrast
import com.example.todolist.main.ui.theme.onErrorContainerLight
import com.example.todolist.main.ui.theme.onErrorContainerLightHighContrast
import com.example.todolist.main.ui.theme.onErrorContainerLightMediumContrast
import com.example.todolist.main.ui.theme.onErrorDark
import com.example.todolist.main.ui.theme.onErrorDarkHighContrast
import com.example.todolist.main.ui.theme.onErrorDarkMediumContrast
import com.example.todolist.main.ui.theme.onErrorLight
import com.example.todolist.main.ui.theme.onErrorLightHighContrast
import com.example.todolist.main.ui.theme.onErrorLightMediumContrast
import com.example.todolist.main.ui.theme.onPrimaryContainerDark
import com.example.todolist.main.ui.theme.onPrimaryContainerDarkHighContrast
import com.example.todolist.main.ui.theme.onPrimaryContainerDarkMediumContrast
import com.example.todolist.main.ui.theme.onPrimaryContainerLight
import com.example.todolist.main.ui.theme.onPrimaryContainerLightHighContrast
import com.example.todolist.main.ui.theme.onPrimaryContainerLightMediumContrast
import com.example.todolist.main.ui.theme.onPrimaryDark
import com.example.todolist.main.ui.theme.onPrimaryDarkHighContrast
import com.example.todolist.main.ui.theme.onPrimaryDarkMediumContrast
import com.example.todolist.main.ui.theme.onPrimaryLight
import com.example.todolist.main.ui.theme.onPrimaryLightHighContrast
import com.example.todolist.main.ui.theme.onPrimaryLightMediumContrast
import com.example.todolist.main.ui.theme.onSecondaryContainerDark
import com.example.todolist.main.ui.theme.onSecondaryContainerDarkHighContrast
import com.example.todolist.main.ui.theme.onSecondaryContainerDarkMediumContrast
import com.example.todolist.main.ui.theme.onSecondaryContainerLight
import com.example.todolist.main.ui.theme.onSecondaryContainerLightHighContrast
import com.example.todolist.main.ui.theme.onSecondaryContainerLightMediumContrast
import com.example.todolist.main.ui.theme.onSecondaryDark
import com.example.todolist.main.ui.theme.onSecondaryDarkHighContrast
import com.example.todolist.main.ui.theme.onSecondaryDarkMediumContrast
import com.example.todolist.main.ui.theme.onSecondaryLight
import com.example.todolist.main.ui.theme.onSecondaryLightHighContrast
import com.example.todolist.main.ui.theme.onSecondaryLightMediumContrast
import com.example.todolist.main.ui.theme.onSurfaceDark
import com.example.todolist.main.ui.theme.onSurfaceDarkHighContrast
import com.example.todolist.main.ui.theme.onSurfaceDarkMediumContrast
import com.example.todolist.main.ui.theme.onSurfaceLight
import com.example.todolist.main.ui.theme.onSurfaceLightHighContrast
import com.example.todolist.main.ui.theme.onSurfaceLightMediumContrast
import com.example.todolist.main.ui.theme.onSurfaceVariantDark
import com.example.todolist.main.ui.theme.onSurfaceVariantDarkHighContrast
import com.example.todolist.main.ui.theme.onSurfaceVariantDarkMediumContrast
import com.example.todolist.main.ui.theme.onSurfaceVariantLight
import com.example.todolist.main.ui.theme.onSurfaceVariantLightHighContrast
import com.example.todolist.main.ui.theme.onSurfaceVariantLightMediumContrast
import com.example.todolist.main.ui.theme.onTertiaryContainerDark
import com.example.todolist.main.ui.theme.onTertiaryContainerDarkHighContrast
import com.example.todolist.main.ui.theme.onTertiaryContainerDarkMediumContrast
import com.example.todolist.main.ui.theme.onTertiaryContainerLight
import com.example.todolist.main.ui.theme.onTertiaryContainerLightHighContrast
import com.example.todolist.main.ui.theme.onTertiaryContainerLightMediumContrast
import com.example.todolist.main.ui.theme.onTertiaryDark
import com.example.todolist.main.ui.theme.onTertiaryDarkHighContrast
import com.example.todolist.main.ui.theme.onTertiaryDarkMediumContrast
import com.example.todolist.main.ui.theme.onTertiaryLight
import com.example.todolist.main.ui.theme.onTertiaryLightHighContrast
import com.example.todolist.main.ui.theme.onTertiaryLightMediumContrast
import com.example.todolist.main.ui.theme.outlineDark
import com.example.todolist.main.ui.theme.outlineDarkHighContrast
import com.example.todolist.main.ui.theme.outlineDarkMediumContrast
import com.example.todolist.main.ui.theme.outlineLight
import com.example.todolist.main.ui.theme.outlineLightHighContrast
import com.example.todolist.main.ui.theme.outlineLightMediumContrast
import com.example.todolist.main.ui.theme.outlineVariantDark
import com.example.todolist.main.ui.theme.outlineVariantDarkHighContrast
import com.example.todolist.main.ui.theme.outlineVariantDarkMediumContrast
import com.example.todolist.main.ui.theme.outlineVariantLight
import com.example.todolist.main.ui.theme.outlineVariantLightHighContrast
import com.example.todolist.main.ui.theme.outlineVariantLightMediumContrast
import com.example.todolist.main.ui.theme.primaryContainerDark
import com.example.todolist.main.ui.theme.primaryContainerDarkHighContrast
import com.example.todolist.main.ui.theme.primaryContainerDarkMediumContrast
import com.example.todolist.main.ui.theme.primaryContainerLight
import com.example.todolist.main.ui.theme.primaryContainerLightHighContrast
import com.example.todolist.main.ui.theme.primaryContainerLightMediumContrast
import com.example.todolist.main.ui.theme.primaryDark
import com.example.todolist.main.ui.theme.primaryDarkHighContrast
import com.example.todolist.main.ui.theme.primaryDarkMediumContrast
import com.example.todolist.main.ui.theme.primaryLight
import com.example.todolist.main.ui.theme.primaryLightHighContrast
import com.example.todolist.main.ui.theme.primaryLightMediumContrast
import com.example.todolist.main.ui.theme.scrimDark
import com.example.todolist.main.ui.theme.scrimDarkHighContrast
import com.example.todolist.main.ui.theme.scrimDarkMediumContrast
import com.example.todolist.main.ui.theme.scrimLight
import com.example.todolist.main.ui.theme.scrimLightHighContrast
import com.example.todolist.main.ui.theme.scrimLightMediumContrast
import com.example.todolist.main.ui.theme.secondaryContainerDark
import com.example.todolist.main.ui.theme.secondaryContainerDarkHighContrast
import com.example.todolist.main.ui.theme.secondaryContainerDarkMediumContrast
import com.example.todolist.main.ui.theme.secondaryContainerLight
import com.example.todolist.main.ui.theme.secondaryContainerLightHighContrast
import com.example.todolist.main.ui.theme.secondaryContainerLightMediumContrast
import com.example.todolist.main.ui.theme.secondaryDark
import com.example.todolist.main.ui.theme.secondaryDarkHighContrast
import com.example.todolist.main.ui.theme.secondaryDarkMediumContrast
import com.example.todolist.main.ui.theme.secondaryLight
import com.example.todolist.main.ui.theme.secondaryLightHighContrast
import com.example.todolist.main.ui.theme.secondaryLightMediumContrast
import com.example.todolist.main.ui.theme.surfaceBrightDark
import com.example.todolist.main.ui.theme.surfaceBrightDarkHighContrast
import com.example.todolist.main.ui.theme.surfaceBrightDarkMediumContrast
import com.example.todolist.main.ui.theme.surfaceBrightLight
import com.example.todolist.main.ui.theme.surfaceBrightLightHighContrast
import com.example.todolist.main.ui.theme.surfaceBrightLightMediumContrast
import com.example.todolist.main.ui.theme.surfaceContainerDark
import com.example.todolist.main.ui.theme.surfaceContainerDarkHighContrast
import com.example.todolist.main.ui.theme.surfaceContainerDarkMediumContrast
import com.example.todolist.main.ui.theme.surfaceContainerHighDark
import com.example.todolist.main.ui.theme.surfaceContainerHighDarkHighContrast
import com.example.todolist.main.ui.theme.surfaceContainerHighDarkMediumContrast
import com.example.todolist.main.ui.theme.surfaceContainerHighLight
import com.example.todolist.main.ui.theme.surfaceContainerHighLightHighContrast
import com.example.todolist.main.ui.theme.surfaceContainerHighLightMediumContrast
import com.example.todolist.main.ui.theme.surfaceContainerHighestDark
import com.example.todolist.main.ui.theme.surfaceContainerHighestDarkHighContrast
import com.example.todolist.main.ui.theme.surfaceContainerHighestDarkMediumContrast
import com.example.todolist.main.ui.theme.surfaceContainerHighestLight
import com.example.todolist.main.ui.theme.surfaceContainerHighestLightHighContrast
import com.example.todolist.main.ui.theme.surfaceContainerHighestLightMediumContrast
import com.example.todolist.main.ui.theme.surfaceContainerLight
import com.example.todolist.main.ui.theme.surfaceContainerLightHighContrast
import com.example.todolist.main.ui.theme.surfaceContainerLightMediumContrast
import com.example.todolist.main.ui.theme.surfaceContainerLowDark
import com.example.todolist.main.ui.theme.surfaceContainerLowDarkHighContrast
import com.example.todolist.main.ui.theme.surfaceContainerLowDarkMediumContrast
import com.example.todolist.main.ui.theme.surfaceContainerLowLight
import com.example.todolist.main.ui.theme.surfaceContainerLowLightHighContrast
import com.example.todolist.main.ui.theme.surfaceContainerLowLightMediumContrast
import com.example.todolist.main.ui.theme.surfaceContainerLowestDark
import com.example.todolist.main.ui.theme.surfaceContainerLowestDarkHighContrast
import com.example.todolist.main.ui.theme.surfaceContainerLowestDarkMediumContrast
import com.example.todolist.main.ui.theme.surfaceContainerLowestLight
import com.example.todolist.main.ui.theme.surfaceContainerLowestLightHighContrast
import com.example.todolist.main.ui.theme.surfaceContainerLowestLightMediumContrast
import com.example.todolist.main.ui.theme.surfaceDark
import com.example.todolist.main.ui.theme.surfaceDarkHighContrast
import com.example.todolist.main.ui.theme.surfaceDarkMediumContrast
import com.example.todolist.main.ui.theme.surfaceDimDark
import com.example.todolist.main.ui.theme.surfaceDimDarkHighContrast
import com.example.todolist.main.ui.theme.surfaceDimDarkMediumContrast
import com.example.todolist.main.ui.theme.surfaceDimLight
import com.example.todolist.main.ui.theme.surfaceDimLightHighContrast
import com.example.todolist.main.ui.theme.surfaceDimLightMediumContrast
import com.example.todolist.main.ui.theme.surfaceLight
import com.example.todolist.main.ui.theme.surfaceLightHighContrast
import com.example.todolist.main.ui.theme.surfaceLightMediumContrast
import com.example.todolist.main.ui.theme.surfaceVariantDark
import com.example.todolist.main.ui.theme.surfaceVariantDarkHighContrast
import com.example.todolist.main.ui.theme.surfaceVariantDarkMediumContrast
import com.example.todolist.main.ui.theme.surfaceVariantLight
import com.example.todolist.main.ui.theme.surfaceVariantLightHighContrast
import com.example.todolist.main.ui.theme.surfaceVariantLightMediumContrast
import com.example.todolist.main.ui.theme.tertiaryContainerDark
import com.example.todolist.main.ui.theme.tertiaryContainerDarkHighContrast
import com.example.todolist.main.ui.theme.tertiaryContainerDarkMediumContrast
import com.example.todolist.main.ui.theme.tertiaryContainerLight
import com.example.todolist.main.ui.theme.tertiaryContainerLightHighContrast
import com.example.todolist.main.ui.theme.tertiaryContainerLightMediumContrast
import com.example.todolist.main.ui.theme.tertiaryDark
import com.example.todolist.main.ui.theme.tertiaryDarkHighContrast
import com.example.todolist.main.ui.theme.tertiaryDarkMediumContrast
import com.example.todolist.main.ui.theme.tertiaryLight
import com.example.todolist.main.ui.theme.tertiaryLightHighContrast
import com.example.todolist.main.ui.theme.tertiaryLightMediumContrast
import com.example.ui.theme.AppTypography

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun ToDoListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {
  val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      
      darkTheme -> darkScheme
      else -> lightScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}

