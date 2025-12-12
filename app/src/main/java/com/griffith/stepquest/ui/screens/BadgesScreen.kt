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
import com.griffith.stepquest.ui.viewmodels.BadgeViewModel
import com.griffith.stepquest.ui.viewmodels.UserViewModel

data class Badge(
    val name: String,
    val description: String,
    val imageRes: Int,
    val obtained: Boolean
)
// badges screen where badges are showcased (obtained badges are colored, none obtained are gray scalled)
@Composable
fun BadgesScreen(navController: NavController, userVM: UserViewModel, badgeVM: BadgeViewModel) {
    // create a list of badges to loop through and display
    val badges = remember(userVM.badgesObtained) {
        badgeVM.getAllBadges(userVM.badgesObtained)
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
                items(badges) { badge ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
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
