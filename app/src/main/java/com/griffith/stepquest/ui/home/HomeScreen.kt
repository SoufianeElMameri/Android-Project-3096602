package com.griffith.stepquest.ui.home

import android.health.connect.datatypes.StepsCadenceRecord
import android.widget.GridLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    val bg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3FCE9),
            Color(0xFFFFF8D2)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)

    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally // centers circle horizontally
        ) {
            HeaderBar()
            StepProgressCircle()
        }
    }
}

// header bar that holds the user and the amount of coins earned
@Composable
fun HeaderBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
                tint = Color.DarkGray
            )
            Spacer(Modifier.width(8.dp))
            Text("Hey User!", fontWeight = FontWeight.SemiBold)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$")
            Spacer(Modifier.width(4.dp))
            Text("30", fontWeight = FontWeight.SemiBold)
        }
    }
}

// header bar that holds the user and the amount of coins earned
@Composable
fun StepProgressCircle() {
    // add space
    Spacer(modifier = Modifier.height(40.dp))

    Box(
        modifier = Modifier
            .size(240.dp),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator(
            progress = { 1f },
            strokeWidth = 18.dp,
            color = Color(0xFFE0E0E0),
            modifier = Modifier.fillMaxSize()
        )
        CircularProgressIndicator(
            progress = { 0.6F },
            strokeWidth = 18.dp,
            color = Color(0xFF4ADE80),
            modifier = Modifier.fillMaxSize()
        )

    }

}
