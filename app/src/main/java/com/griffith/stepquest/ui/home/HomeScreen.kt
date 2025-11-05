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
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.griffith.stepquest.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.style.TextAlign

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderBar()
            StepProgressCircle(3600, 6000)
            Spacer(Modifier.height(12.dp))
            StreakSection(4)
            Spacer(Modifier.height(20.dp))
            WeeklyMonthlyCards()
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
                tint = Color.DarkGray,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Hey User!",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.coin),
                contentDescription = "Coin",
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "30",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
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
            val strokeWidth = 30.dp.toPx()
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

        // Text inside ring
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$currentSteps",
                fontWeight = FontWeight.Bold,
                fontSize = 45.sp
            )
            Text(
                text = "/$goalSteps steps",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Image(
                painter = painterResource(R.drawable.shoe),
                contentDescription = "Running Shoe",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// steak section showcasing the current streak
@Composable
fun StreakSection(streaks : Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.fire),
            contentDescription = "Streak fire",
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "$streaks-Day Streak",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = Color(0xFFFF6B00)
        )
    }
}

// creating weekly and monthly stat cards aligned
@Composable
fun WeeklyMonthlyCards() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        // adding space between cards
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard("Weekly", 18232, R.drawable.shoe, Modifier.weight(1f))
        StatCard("Monthly", 72123, R.drawable.shoe, Modifier.weight(1f))
    }
}

// reusable component for stat cards to creat monthly weekly steps stats
@Composable
fun StatCard(title: String, value: Int, iconRes: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(120.dp)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top=15.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal=20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column{
                    Text(
                        text = "$value",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Steps",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

            }
        }

    }
}











































