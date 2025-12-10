package com.griffith.stepquest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.griffith.stepquest.R
import com.griffith.stepquest.ui.components.HeaderBar
import com.griffith.stepquest.ui.theme.*
import com.griffith.stepquest.ui.viewmodels.RankViewModel
import com.griffith.stepquest.ui.viewmodels.StepsViewModel
import com.griffith.stepquest.ui.viewmodels.UserViewModel
import androidx.compose.runtime.LaunchedEffect



fun mapRankToNumber(rank: String): Int {
    return when (rank) {
        "Gold" -> 1
        "Silver" -> 2
        "Bronze" -> 3
        "Diamond" -> 4
        "Legend" -> 5
        else -> 999
    }
}

// Ranks sccreen shocasse the user rank and the current leaderboard for that rank
@Composable
fun RankScreen(navController: NavController, userVM: UserViewModel, stepsVM: StepsViewModel,rankVM: RankViewModel) {
    val players = rankVM.players
    LaunchedEffect(true) {
        rankVM.loadRankPlayers(
            currentName = userVM.userName,
            currentRank = userVM.userRank,
            currentWeeklySteps = stepsVM.weeklySteps
        )
    }

    val ranks = listOf("Bronze", "Silver", "Gold", "Diamond", "Legend")

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
            HeaderBar("LeaderBoard", navController)
            Spacer(modifier = Modifier.height(20.dp))
//********************************************************* RANK TIERS *********************************************************
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ranks.forEach { tier ->
                    val isCurrent = tier.equals(userVM.userRank, ignoreCase = true)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(
                                id = when (tier) {
                                    "Bronze" -> R.drawable.bronze_rank
                                    "Silver" -> R.drawable.silver_rank
                                    "Gold" -> R.drawable.gold_rank
                                    "Diamond" -> R.drawable.diamond_rank
                                    "Legend" -> R.drawable.legend_rank
                                    else -> R.drawable.no_rank
                                }
                            ),
                            contentDescription = tier,
                            modifier = Modifier
                                .size(if (isCurrent) 80.dp else 60.dp)
                        )
                        Text(
                            text = tier,
                            fontSize = if (isCurrent) 20.sp else 15.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) LimeColor else Dark
                        )
                    }
                }
            }
//********************************************************* LADER BOARD *********************************************************
            Text(
                text = "Top Walkers",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Dark
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(players) { index, player ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (player.uid == userVM.userUID) LightLimeColor
                                else Bright
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // adding medal to the top three walkers
                                if (index <= 2) {
                                    val medal = when (index) {
                                        0 -> R.drawable.gold_medal
                                        1 -> R.drawable.silver_medal
                                        2 -> R.drawable.bronze_medal
                                        else -> R.drawable.bronze_medal
                                    }
                                    Image(
                                        painter = painterResource(id = medal),
                                        contentDescription = "Medal",
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                } else {
                                    Text(
                                        text = "#${index + 1}",
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = player.name,
                                    fontWeight = if (player.name == "You") FontWeight.Bold else FontWeight.Normal,
                                    color = Dark
                                )
                            }
                            Text(
                                text = "${player.steps} steps",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = GrassColor
                            )
                        }
                    }
                }
            }
        }
    }
}
