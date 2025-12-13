package com.griffith.stepquest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.griffith.stepquest.ui.theme.*
import androidx.compose.ui.draw.clip
import androidx.compose.material3.OutlinedTextField
import com.griffith.stepquest.data.FirebaseAuthManger
import com.griffith.stepquest.ui.viewmodels.AuthViewModel


@Composable
fun AuthScreen(authVM: AuthViewModel, onLoginSuccess: () -> Unit) {

    // which tab user is on
    var isRegister by remember { mutableStateOf(false) }

    LaunchedEffect(authVM.authSuccess) {
        if (authVM.authSuccess) {
            onLoginSuccess()
        }
    }
    // user inputs
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(BackgroundTop, BackgroundBottom)
                )
            ),
        color = Glass
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//*************************************************** HEADER TITLE ***************************************************

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 28.dp)
            ) {
                Text(
                    text = "Welcome",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "to",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )

                Text(
                    text = "StepQuest",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Dark
                )
            }
            if (isRegister) {
                Spacer(modifier = Modifier.height(20.dp))
            }else{
                Spacer(modifier = Modifier.height(60.dp))
            }
//*************************************************** REGISTER OR LOGIN OPTIONS ***************************************************
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                val activeBg = LightBronze
                val inactiveBg = Glass

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!isRegister) activeBg else inactiveBg)
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                        .clickable { isRegister = false }
                ) {
                    Text("Login", fontSize = 20.sp, color = if (!isRegister) Dark else TextSecondary)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isRegister) activeBg else inactiveBg)
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                        .clickable { isRegister = true }
                ) {
                    Text("Register", fontSize = 20.sp, color = if (isRegister) Dark else TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
//*************************************************** REGISTER OR LOGIN TEXT FIELDS ***************************************************

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false
                    )
                    .background(Bright, RoundedCornerShape(20.dp))
                    .padding(22.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // only show the username text field if the user is trying to register
                    if (isRegister) {
                        CostumeTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = "Username",
                            error = authVM.usernameError
                        )
                    }
                    // always show the email text field
                    CostumeTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        error = authVM.emailError
                    )
                    // always show the password text field
                    CostumeTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isPassword = true,
                        error = authVM.pwdError
                    )

                    // only show the confirm password text field if the user is trying to register
                    if (isRegister) {
                        CostumeTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Password",
                            isPassword = true,
                            error = authVM.pwdConfirmError
                        )
                    }

                    Button(
                        onClick = {
                            if (isRegister) {
                                authVM.register(username, email, password, confirmPassword)
                            } else {
                                authVM.login(email, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isRegister) "Register" else "Login",
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// a function that create text fields
@Composable
fun CostumeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    error: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // use outlined text field for better visual effects
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(label, color = TextSecondary) },
//                colors = TextFieldDefaults.colors(
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent
//                ),
                visualTransformation = if (isPassword)
                    PasswordVisualTransformation() else
                    VisualTransformation.None,
                singleLine = true
            )
        }

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = AlertRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(15.dp))
}