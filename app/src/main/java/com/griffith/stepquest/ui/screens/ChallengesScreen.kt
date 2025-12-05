package com.griffith.stepquest.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.griffith.stepquest.R
import com.griffith.stepquest.ui.components.HeaderBar
import androidx.compose.ui.text.buildAnnotatedString
import com.griffith.stepquest.data.StepCounter
import com.griffith.stepquest.ui.viewmodels.UserViewModel
import com.griffith.stepquest.ui.theme.*
import com.griffith.stepquest.ui.viewmodels.CoinsViewModel
import com.griffith.stepquest.ui.viewmodels.StepsViewModel

// Challenges screen showcases the daily challenges and tips
@Composable
fun ChallengesScreen(navController: NavController, userVM: UserViewModel, stepsVM: StepsViewModel, coinsVM: CoinsViewModel) {
    // grab the current steps the user did
    val steps = stepsVM.steps
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundTop,
                        BackgroundBottom
                    )
                )
            ),
        color = Glass
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
//***************************************************** HEADER BAR *****************************************************
            HeaderBar("Challenges", navController)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Today's Challenges",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

//***************************************************** TODAY CHALLENGES *****************************************************
            ChallengeCard(
                title = "Walk 5,000 steps today",
                steps,
                5000,
                1,
                onClaim = { coins, xp ->
                    coinsVM.addCoins(coins)
                    userVM.addUserExperience(xp) }
            )
            Spacer(Modifier.height(20.dp))
            ChallengeCard(
                title = "Walk 10,000 steps today",
                steps,
                10000,
                2,
                onClaim = { coins, xp ->
                    coinsVM.addCoins(coins)
                    userVM.addUserExperience(xp) }
            )
            Spacer(Modifier.height(20.dp))
            ChallengeCard(
                title = "Walk 15,000 steps today",
                steps,
                15000,
                3,
                onClaim = { coins, xp ->
                    coinsVM.addCoins(coins)
                    userVM.addUserExperience(xp)}
            )
            Spacer(Modifier.height(20.dp))
//***************************************************** TODAY'S TIP *****************************************************
            DailyTip("Short walks throughout the day add up. Keep moving!")
            Spacer(Modifier.height(10.dp))

            val context = LocalContext.current
//***************************************************** WATCH YOUTUBE VIDEO *****************************************************
            Button(onClick = {
                Intent(Intent.ACTION_MAIN).also { intent ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=eEWa7cpiyD8"))
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }
            },
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text("Why Walk? Watch this!")
            }
        }
    }
}

// function to help create challenge cards
@Composable
fun ChallengeCard(title: String, progress: Int, goal: Int, difficulty: Int, onClaim: (Int, Int) -> Unit, modifier: Modifier = Modifier ){
    // to keep track of claimed challenges
    val alreadyClaimed = remember { mutableStateOf(false) }
    val progressRatio = (progress.toFloat() / goal).coerceIn(0f, 1f)
    val isCompleted = progress >= goal
    val experience = difficulty * 10
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Bright)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Dark
                    )
                    Text(
                        text = "$progress / $goal steps",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = if (isCompleted) LimeColor else BadgeLockedDark,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = isCompleted && !alreadyClaimed.value) {
                            alreadyClaimed.value = true
                            onClaim(difficulty, experience)
                        }
                        .padding(horizontal = 5.dp, vertical = 6.dp)
                ) {
                    // check if user claimed the rewards and replace the reward icon with check icon
                    if (!alreadyClaimed.value) {
                        Row(
                            modifier = Modifier.width(35.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "+$difficulty",
                                color = Bright,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Image(
                                painter = painterResource(R.drawable.coin),
                                contentDescription = "Coin",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Image(
                            painter = painterResource(R.drawable.check),
                            contentDescription = "Coin",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                }
            }

            Spacer(Modifier.height(10.dp))
            // Difficulty and experience
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // difficulty row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Difficulty:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(Modifier.width(8.dp))
                    repeat(difficulty) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = IconGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                // Experience row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Experience: $experience",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(Modifier.width(8.dp))
                    Image(
                        painter = painterResource(R.drawable.xp),
                        contentDescription = "Experience",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(EmptyBarColor, RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressRatio)
                        .background(LimeColor, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

// function to create daily tips (we will have a table with tips, one will be loaded randomly each day)
@Composable
fun DailyTip(tip: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .background(LightLimeColor,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.bulb),
            contentDescription = "Light bulb",
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = tip,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = GrassColor
        )
    }
}