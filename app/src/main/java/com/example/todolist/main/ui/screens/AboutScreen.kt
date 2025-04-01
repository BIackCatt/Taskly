package com.example.todolist.main.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.LocalContext
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.todolist.R
import androidx.core.net.toUri

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    backHandler: () -> Unit,
) {
    BackHandler {
        backHandler()
    }
    Scaffold(
        topBar = {
            AboutTopBar { backHandler() }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AboutAnimation(modifier = Modifier.size(250.dp))
            Text(text = "About Taskly", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Card {
                Text(
                    text = "An innovative task management app designed for productivity.",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card {
                Column {
                    Text(
                        text = "Developed by: Ibrahim Mahmoud",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp),
                        fontSize = 20.sp
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = modifier.fillMaxWidth()
                ) {
                    SocialText(
                        platform = "Instagram",
                        uri = "https://www.instagram.com/hemako_m?igsh=dXB5dXBnYmgyaTFh",
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFEDA75), // Yellow
                                        Color(0xFFFA7E1E), // Orange
                                        Color(0xFFD62976), // Pink
                                        Color(0xFF962FBF), // Purple
                                        Color(0xFF4F5BD5)  // Blue
                                    )
                                )
                            )
                    )
                    Spacer(Modifier.padding(10.dp))
                    SocialText(
                        platform = "LinkedIn",
                        uri = "https://eg.linkedin.com/in/ibrahim-mahmoud-1930b3329",
                        modifier = modifier
                            .clip(CircleShape)
                            .background(Color(0, 119, 181))
                    )
                    Spacer(Modifier.padding(10.dp))
                    SocialText(
                        platform = "Github",
                        uri = "https://github.com/BIackCatt",
                        modifier = modifier
                            .clip(CircleShape)
                            .background(Color(0xFF1F1F1F))
                    )
                    Spacer(Modifier.padding(10.dp))
                }
            }




            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Version: 2.0.0")
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "© 2025 Ibrahim Mahmoud. All rights reserved.")
        }
    }
}


@Composable
fun AboutAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.about)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 2f,
        reverseOnRepeat = true
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}


@Composable
fun SocialText(modifier: Modifier = Modifier, platform: String, uri: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Button(
        modifier = modifier.padding(),
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
            context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
        )
    ) {
        Text(
            text = platform,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutTopBar(
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit,
) {
    CenterAlignedTopAppBar(

        navigationIcon = {
            IconButton(
                onNavigationClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                    contentDescription = "Navigate Back",
                )
            }
        },
        title = {
            Text(
                text = "About",
                style = MaterialTheme.typography.titleLarge
            )
        }
    )
}


@Preview(showBackground = true)
@Composable
private fun AboutScreenPrev() {
    AboutScreen(backHandler = {})

}