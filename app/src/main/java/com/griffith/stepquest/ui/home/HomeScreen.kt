package com.griffith.stepquest.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.sp

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
            StepProgressCircle(3600, 6000)
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
fun StepProgressCircle(currentSteps: Int,
                       goalSteps: Int) {
//    calculating the progress
    val progress = currentSteps.toFloat() / goalSteps.toFloat()
//    adding a spacer
    Spacer(modifier = Modifier.height(40.dp))

    Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Arc settings
            val strokeWidth = 18.dp.toPx()
            // 270 arc to leave a gap at the bottom
            val sweepAngle = 270f

            // Background arc
            drawArc(
                color = Color(0xFFB2B2A7),
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            // Progress arc
            drawArc(
                color = Color(0xFF4ADE80),
                startAngle = 135f,
                sweepAngle = sweepAngle * progress,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}



















































