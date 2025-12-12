package com.griffith.stepquest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.griffith.stepquest.ui.components.HeaderBar
import com.griffith.stepquest.ui.theme.*

//***************************************************** TERMS & CONDITIONS SCREEN *****************************************************
@Composable
fun TermsAndConditionsScreen(navController: NavController) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundTop, BackgroundBottom)
                )
            ),
        color = Glass
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            HeaderBar("", navController)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Terms & Conditions",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Dark
            )

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .background(
                        color = Bright,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(18.dp)
            ) {
                Text(
                    text = """
                        Welcome to StepQuest.
                        
                        By using this application, you agree to the following terms:
                        
                        • StepQuest is provided for fitness and entertainment purposes only.
                        • Step counts and statistics are estimates and may not be 100% accurate.
                        • You are responsible for maintaining the confidentiality of your account.
                        • Any misuse, abuse, or attempt to exploit the system may result in account suspension.
                        • We reserve the right to update these terms at any time.
                        
                        Continued use of the app indicates acceptance of these terms.
                    """.trimIndent(),
                    fontSize = 15.sp,
                    color = Dark,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
