package com.griffith.stepquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import com.griffith.stepquest.R
import com.griffith.stepquest.ui.theme.*
import com.griffith.stepquest.utils.IconsMapping

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


    val medal = parts.getOrNull(0) ?: ""

    var xpText = ""
    var coinsText = ""
    var rankUpText = ""
    var positionText = ""

    for (part in parts) {
        when {
            part.startsWith("EXP:") -> xpText = part.replace("EXP:", "")
            part.startsWith("COINS:") -> coinsText = part.replace("COINS:", "")
            part.startsWith("RANKUP:") -> rankUpText = part.replace("RANKUP:", "")
            part.startsWith("POS:") -> positionText = part.replace("POS:", "")
        }
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
                if (medal != "LOSER") {
                    // medal icon
                    val iconRes = IconsMapping.medalIcons[medal] ?: R.drawable.bronze_medal


                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = "",
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(Modifier.height(12.dp))
                }
                Text(
                    text = "Weekly Rank Results",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(Modifier.height(8.dp))

                var medalText = ""
                if (medal == "Gold") {
                    medalText = "You Scored Gold!!!"
                } else if (medal == "Silver") {
                    medalText = "You Scored Silver!!!"
                } else if (medal == "Bronze") {
                    medalText = "You Scored Bronze!!!"
                } else {
                    medalText = ""
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
                        if (medal == "LOSER") {
                            Text(
                                text = "You finished in position $positionText",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Dark,
                                textAlign = TextAlign.Center
                            )
                        } else {
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
                                    IconsMapping.rankIcons[rankUpText] ?: R.drawable.no_rank


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

// popup that shows the streak result (new, incremented, or broken)
@Composable
fun StreakResultPopup(
    message: String,
    streak: Int,
    coins: Int,
    streakBroken: Boolean,
    onRepair: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xB0000000))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Bright),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .shadow(20.dp, RoundedCornerShape(30.dp))
        ) {
            Column(
                modifier = Modifier.padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    FireTextColor,
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.fire),
                        contentDescription = null,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Column(
                    modifier = Modifier.padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$streak",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = FireTextColor,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = message,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = FireTextColor,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (streakBroken) {

                    Button(
                        onClick = { if (coins >= 35) onRepair() },
                        enabled = coins >= 35,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (coins >= 35) FireTextColor else EmptyBarColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Repair Streak",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Bright
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "-35",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Bright
                            )
                            Spacer(Modifier.width(6.dp))
                            Image(
                                painter = painterResource(R.drawable.coin),
                                contentDescription = "",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Bright,
                                    BackgroundBottom
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = Bright,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Close",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Dark
                    )
                }
            }
        }
    }
}

// popup that shows the new badge unlocked
@Composable
fun BadgeUnlockedPopup(
    badgeKey: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val mediaPlayer = remember { android.media.MediaPlayer.create(context, R.raw.reward_sound) }
    LaunchedEffect(badgeKey) {
        mediaPlayer.start()
    }
    val iconRes = IconsMapping.badgeIcons[badgeKey] ?: R.drawable.moon_badge

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {

        ConfettiBurst(
            modifier = Modifier.align(Alignment.Center)
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Bright),
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(0.85f)
                .shadow(18.dp, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    LimeColor,
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Badge Unlocked!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = badgeKey,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Dark,
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
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You’ve earned a new badge!!!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Dark,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(22.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LimeColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Awesome!!!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Dark
                    )
                }
            }
        }
    }
}
