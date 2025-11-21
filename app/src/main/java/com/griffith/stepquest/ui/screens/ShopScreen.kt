package com.griffith.stepquest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.griffith.stepquest.R
import com.griffith.stepquest.ui.components.HeaderBar
import com.griffith.stepquest.ui.viewmodels.ViewModel
import com.griffith.stepquest.ui.theme.*

// Challenges screen showcases the daily challenges and tips
@Composable
fun ShopScreen(navController: NavController, userVM: ViewModel) {
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
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)

        ) {
//***************************************************** HEADER BAR *****************************************************
            HeaderBar("Shop", navController)
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "The shop is currently Closed! Will open soon",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(100.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.closed),
                    contentDescription = "Closed sign",
                    modifier = Modifier.size(150.dp)
                )
            }
        }
    }
}