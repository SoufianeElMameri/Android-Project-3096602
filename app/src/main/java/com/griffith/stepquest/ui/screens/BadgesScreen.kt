package com.griffith.stepquest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.griffith.stepquest.R
import com.griffith.stepquest.ui.components.HeaderBar
import androidx.compose.ui.draw.shadow
import androidx.navigation.NavController
import com.griffith.stepquest.ui.theme.*

data class Badge(
    val name: String,
    val description: String,
    val imageRes: Int,
    val obtained: Boolean
)
// badges screen where badges are showcased (obtained badges are colored, none obtained are gray scalled)
@Composable
fun BadgesScreen(navController: NavController) {
    // create a list of badges to loop through and display
    val badges = remember {
        listOf(
            Badge(
                name = "NightWalker",
                description = "Walk 5,000 steps after 9:00 PM.",
                imageRes = R.drawable.moon_badge,
                obtained = true
            ),
            Badge(
                name = "EarlyBird",
                description = "Walk 5,000 steps before 8:00 AM.",
                imageRes = R.drawable.sunrise,
                obtained = false
            ),
            Badge(
                name = "Marathoner",
                description = "Walk 42,000 total steps in a day.",
                imageRes = R.drawable.marathon,
                obtained = false
            ),
            Badge(
                name = "ConsistencyKing",
                description = "Hit your step goal 7 days in a row.",
                imageRes = R.drawable.crown,
                obtained = false
            ),
            Badge(
                name = "WeekendWarrior",
                description = "Walk 10,000 steps on a Saturday or Sunday.",
                imageRes = R.drawable.warrior,
                obtained = false
            ),
            Badge(
                name = "Explorer",
                description = "Walk in 5 different locations in a week.",
                imageRes = R.drawable.map,
                obtained = false
            ),
            Badge(
                name = "StreakMaster",
                description = "Maintain a 30-day active streak.",
                imageRes = R.drawable.streak,
                obtained = false
            ),
            Badge(
                name = "GoalCrusher",
                description = "Surpass your daily step goal by 50%.",
                imageRes = R.drawable.success,
                obtained = false
            ),
        )
    }
    // state to display badge details
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }

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
            HeaderBar("Badges",  navController)
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your Achievements",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Collect badges as you complete challenges!",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(30.dp))

//*************************************************** GRID OF BADGES ***************************************************
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // loop through the badges and create them inside the grid
                items(badges) { badge ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            // On click display badge description
                            .clickable { selectedBadge = badge },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .shadow(
                                    elevation = if (badge.obtained) 10.dp else 5.dp,
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    clip = false
                                )
                                // change background color if badge is obtained
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = if (badge.obtained) {
                                            listOf(
                                                BadgeGoldLight,
                                                BadgeGoldMid,
                                                BadgeGold,
                                                BadgeGoldSoft
                                            )
                                        } else {
                                            listOf(
                                                BadgeLockedLight,
                                                BadgeLockedMid,
                                                BadgeLockedDark
                                            )
                                        }
                                    ),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = badge.imageRes),
                                contentDescription = badge.name,
                                modifier = Modifier.size(50.dp),
                                // If badge is not obtained we use remove its saturation
                                colorFilter = if (!badge.obtained) {
                                    ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                                } else null
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = badge.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Dark
                        )
                    }
                }
            }
        }

        // pop up on badge click to show it's description and how to obtain it
        selectedBadge?.let { badge ->
            AlertDialog(
                onDismissRequest = { selectedBadge = null },
                title = {
                    Text(
                        text = badge.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(text = badge.description)
                       },
                confirmButton = {
                    TextButton(onClick = { selectedBadge = null }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
