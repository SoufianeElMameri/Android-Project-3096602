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

//***************************************************** PRIVACY POLICY SCREEN *****************************************************
@Composable
fun PrivacyPolicyScreen(navController: NavController) {

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
//***************************************************** HEADER BAR *****************************************************
            HeaderBar("", navController)

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Privacy Policy",
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
                            Your privacy matters to us.
                            
                            StepQuest collects only the data necessary to provide its core features:
                            
                            • Step counts and activity-related data
                            • Basic account information (email, username)
                            • App usage data for performance and improvement
                            
                            We do NOT sell or share your personal data with third parties.
                            All data is securely stored and handled according to best practices.
                            
                            You may request deletion of your account and data at any time.
                            
                            By using StepQuest, you agree to this privacy policy.
                    """.trimIndent(),
                    fontSize = 15.sp,
                    color = Dark,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
