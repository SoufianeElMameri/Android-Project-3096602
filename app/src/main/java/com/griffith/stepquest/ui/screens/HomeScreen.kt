package com.griffith.stepquest.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.griffith.stepquest.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth

import androidx.compose.material3.Card

import com.griffith.stepquest.data.StepCounter
import java.io.File
import com.griffith.stepquest.ui.theme.*
import com.griffith.stepquest.ui.viewmodels.CoinsViewModel
import com.griffith.stepquest.ui.viewmodels.RankViewModel
import com.griffith.stepquest.ui.viewmodels.StepsViewModel
import com.griffith.stepquest.ui.viewmodels.UserViewModel

@Composable
fun HomeScreen(navController: NavController, userVM: UserViewModel, stepsVM: StepsViewModel, coinsVM: CoinsViewModel, rankVM: RankViewModel) {


    val popupMessage = rankVM.weeklyResult

    // get the mobile screen width
    val screenWidth = LocalConfiguration.current.screenWidthDp
    // grab the current steps the user did
    val steps           = stepsVM.steps
    val dailyStepGoal   = stepsVM.dailyGoal
    val currentStreak   = userVM.currentStreak
    val weeklyStats     = stepsVM.weeklySteps
    val monthlyStats    = stepsVM.monthlySteps
    val weeklyHistory   = stepsVM.weeklyHistory



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
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//***************************************************** HEADER BAR **************************************************
            HeaderBar(navController, coinsVM.coinStash)
//***************************************************** PROGRESS CIRCLE *********************************************
            StepProgressCircle(steps, dailyStepGoal)
            Spacer(Modifier.height((screenWidth * 0.02).dp))
//***************************************************** STREAKS *****************************************************
            StreakSection(currentStreak)
            Spacer(Modifier.height((screenWidth * 0.06).dp))
//***************************************************** WEAKLY MONTHLY STEPS COUNT **********************************
            WeeklyMonthlyCards(weeklyStats,monthlyStats )
            Spacer(Modifier.height((screenWidth * 0.06).dp))
//***************************************************** WEAKLY CHART ************************************************
            WeeklyChart(weeklyHistory)


        }
        if (popupMessage != null) {
            WeeklyResultPopup(
                message = popupMessage,
                onDismiss = { rankVM.clearWeeklyResult()  }
            )
        }
    }
}

// header bar that holds the user and the amount of coins earned
@Composable
fun HeaderBar(navController: NavController, coins: Int) {
    // loading the porifle picture from the local storage if it exists if not load default profile
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user"
    val file = File(context.filesDir, "profile_picture_$uid.png")

    val profileImage = if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)
    } else {
        BitmapFactory.decodeResource(context.resources, R.drawable.default_profile)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // currency owned
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.coin),
                contentDescription = "Coin",
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "$coins",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }
        // profile picture clickable
        Image(
            painter = if (profileImage != null)
                rememberAsyncImagePainter(
                    model = profileImage
                )
            else
                painterResource(id = R.drawable.default_profile),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(60.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    clip = false
                )
                .border(
                    width = 3.dp,
                    color = Bright,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .clickable { navController.navigate("profile") },
            contentScale = ContentScale.Crop
        )
    }
}

// header bar that holds the user and the amount of coins earned
@Composable
fun StepProgressCircle(currentSteps: Int,goalSteps: Int) {
    // calculating the circle size based on t he device screen width
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val circleSize = (screenWidth * 0.5).dp
    //    calculating the progress
    val progress = currentSteps.toFloat() / goalSteps.toFloat()
    //    adding a spacer
    Spacer(modifier = Modifier.height(40.dp))

    Box(
        modifier = Modifier.size(circleSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Arc settings
            val strokeWidth = 25.dp.toPx()
            // 270 arc to leave a gap at the bottom
            val sweepAngle = 270f

            // Background arc
            drawArc(
                color = EmptyBarColor,
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
                color = ProgressBarColor,
                startAngle = 135f,
                sweepAngle = sweepAngle * progress,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        // Text inside the arc
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$currentSteps",
                fontWeight = FontWeight.Bold,
                fontSize = 45.sp
            )
            Text(
                text = "/$goalSteps steps",
                fontSize = 14.sp,
                color = TextPrimary
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
    var day = "Day"
    if (streaks > 1){
        day = "Days"
    }

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
            text = "$streaks-$day Streak",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = FireTextColor
        )
    }
}

// creating weekly and monthly stat cards aligned
@Composable
fun WeeklyMonthlyCards(weeklyStats : Int ,monthlyStats : Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        // adding space between cards
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard("Weekly", weeklyStats, R.drawable.shoe, Modifier.weight(1f))
        StatCard("Monthly", monthlyStats, R.drawable.shoe, Modifier.weight(1f))
    }
}

// reusable component for stat cards to creat monthly weekly steps stats
@Composable
fun StatCard(title: String, value: Int, iconRes: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(100.dp)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Bright)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top=10.dp, bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                        color = Dark
                    )
                    Text(
                        text = "Steps",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Dark
                    )
                }

            }
        }

    }
}

// a chart that takes in a dictionary and transforms it into a chart 7 days
@Composable
fun WeeklyChart(data: Map<String, Int>) {
    // getting the maximum value in the map
    val maxSteps = data.values.maxOrNull() ?: 1
    // calculating the y from the maximum value to create a 5 levels of data
    val ySteps = 4
    val yValues = (0..ySteps).map { i ->
        maxSteps - (maxSteps / ySteps.toFloat() * i)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.Bottom
    ) {

//******************************************** Y AXE STEP COUNT LEVELS
        Column(
            modifier = Modifier
                .padding(end = 8.dp)
                .height(130.dp)
                // adding an offset to make the y axe match the bar starting point
                .offset(y = -15.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            yValues.forEach { value ->
                Text(
                    text = value.toInt().toString(),
                    fontSize = 10.sp,
                    color = TextPrimary
                )
            }
        }

//******************************************** DAILY STEP COUNT BARS
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Weekly Steps",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // loop through the map and create green bars
                data.forEach { (day, steps) ->
                    val barHeight = (steps.toFloat() / maxSteps) * 120f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(barHeight.dp)
                                .background(
                                    LimeColor,
                                    RoundedCornerShape(6.dp)
                                )
                        )
                        Spacer(Modifier.height(6.dp))
//******************************************** X AXE DAYS
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

// weekly reward popup that shows medal, xp, coins and rank up
@Composable
fun WeeklyResultPopup(message: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val mediaPlayer = remember { android.media.MediaPlayer.create(context, R.raw.reward_sound) }
    LaunchedEffect(message) {
        mediaPlayer.start()
    }
    // split message into pieces  the message comes in this format key value | ....
    val parts = message.split("|")

    var medal = ""
    if (parts.isNotEmpty()) {
        medal = parts[0]
    }

    var xpText = ""
    if (parts.size > 1) {
        xpText = parts[1].replace("EXP:", "")
    }

    var coinsText = ""
    if (parts.size > 2) {
        coinsText = parts[2].replace("COINS:", "")
    }

    var rankUpText = ""
    if (parts.size > 3) {
        rankUpText = parts[3].replace("RANKUP:", "")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // rendering the confetti behind popup
        ConfettiBurst(
            modifier = Modifier.align(Alignment.Center)
        )
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Bright),
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(0.85f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // medal icon
                val iconRes =
                    if (medal == "GOLD") {
                        R.drawable.gold_medal
                    } else if (medal == "SILVER") {
                        R.drawable.silver_medal
                    } else if (medal == "BRONZE") {
                        R.drawable.bronze_medal
                    } else {
                        R.drawable.bronze_medal
                    }

                Image(
                    painter = painterResource(iconRes),
                    contentDescription = "",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Weekly Rank Results",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(Modifier.height(8.dp))

                var medalText = ""
                if (medal == "GOLD") {
                    medalText = "You Scored Gold!!!"
                } else if (medal == "SILVER") {
                    medalText = "You Scored Silver!!!"
                } else if (medal == "BRONZE") {
                    medalText = "You Scored Bronze!!!"
                } else {
                    medalText = "Rewards"
                }

                Text(
                    text = medalText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Glass,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
//******************************************* coin section *******************************************
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Experience: +$xpText",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                color = Dark
                            )

                            Spacer(Modifier.width(8.dp))

                            Image(
                                painter = painterResource(R.drawable.xp),
                                contentDescription = "Experience",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(Modifier.height(10.dp))
//******************************************* coin section *******************************************
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Coins Earned: +$coinsText",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                color = Dark
                            )
                            Spacer(Modifier.width(8.dp))

                            Image(
                                painter = painterResource(R.drawable.coin),
                                contentDescription = "Coin",
                                modifier = Modifier.size(20.dp)
                            )
                        }
//******************************************* rank up section *******************************************
                        if (rankUpText.isNotEmpty()) {

                            Spacer(Modifier.height(12.dp))

                            val rankIcon =
                                if (rankUpText == "Bronze") {
                                    R.drawable.bronze_rank
                                } else if (rankUpText == "Silver") {
                                    R.drawable.silver_rank
                                } else if (rankUpText == "Gold") {
                                    R.drawable.gold_rank
                                } else if (rankUpText == "Diamond") {
                                    R.drawable.diamond_rank
                                } else if (rankUpText == "Legend") {
                                    R.drawable.legend_rank
                                } else {
                                    R.drawable.no_rank
                                }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "New Rank: $rankUpText",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Spacer(Modifier.width(8.dp))


                                Image(
                                    painter = painterResource(rankIcon),
                                    contentDescription = "Rank Icon",
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }
                    }
                }


                Spacer(Modifier.height(24.dp))

                androidx.compose.material3.Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = LimeColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Dark
                    )
                }
            }
        }
    }
}



// function to create a confetti
@Composable
fun ConfettiBurst(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center
) {
    val particles = 40
    val anim = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(Unit) {
        anim.animateTo(
            1f,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 900,
                easing = {
                    androidx.compose.animation.core.FastOutSlowInEasing.transform(it)
                }
            )
        )
    }
    // confetti size and shape
    Box(
        modifier = modifier.size(300.dp),
        contentAlignment = alignment
    ) {
        for (i in 0 until particles) {
            val angle = (i * (360f / particles)) * (Math.PI / 180f)
            val radius = anim.value * 180f
            val x = kotlin.math.cos(angle).toFloat() * radius
            val y = kotlin.math.sin(angle).toFloat() * radius

            Box(
                modifier = Modifier
                    .offset(x.dp, y.dp)
                    .size(12.dp)
                    .background(
                        if (i % 2 == 0) LimeColor else Bright,
                        shape = CircleShape
                    )
            )
        }
    }
}








































