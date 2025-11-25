package com.griffith.stepquest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.griffith.stepquest.data.UserInformation
import com.griffith.stepquest.ui.theme.*
import androidx.compose.ui.draw.clip

@Composable
fun AuthScreen(userInfo: UserInformation, onLoginSuccess: () -> Unit) {

    // which tab user is on
    var isRegister by remember { mutableStateOf(false) }

    // user inputs
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // error states
    var usernameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var pwdError by remember { mutableStateOf("") }
    var pwdConfirmError by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(BackgroundTop, BackgroundBottom)
                )
            ),
        color = Color.Transparent
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//*************************************************** REGISTER OR LOGIN OPTIONS ***************************************************
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                val activeBg = Color(0x74FFEB43)
                val inactiveBg = Color.Transparent

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!isRegister) activeBg else inactiveBg)
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                        .clickable { isRegister = false }
                ) {
                    Text("Login", fontSize = 20.sp, color = if (!isRegister) Black else TextSecondary)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isRegister) activeBg else inactiveBg)
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                        .clickable { isRegister = true }
                ) {
                    Text("Register", fontSize = 20.sp, color = if (isRegister) Black else TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
//*************************************************** REGISTER OR LOGIN TEXT FIELDS ***************************************************
            // only show the username text field if the user is trying to register
            if (isRegister) {
                CostumeTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    error = usernameError
                )
            }
            // always show the email text field
            CostumeTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                error = emailError
            )

            // always show the password text field

            CostumeTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true,
                error = pwdError
            )

            // only show the confirm password text field if the user is trying to register
            if (isRegister) {
                CostumeTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    isPassword = true,
                    error = pwdConfirmError
                )
            }

            Button(
                onClick = {

                    // reset errors
                    usernameError = ""
                    emailError = ""
                    pwdError = ""
                    pwdConfirmError = ""

                    // check if the user is trying to register and if the username is empty
                    if (isRegister) {
                        if (username.isBlank()) {
                            usernameError = "Username required"
                            return@Button
                        }
                    }
                    // check if the email is empty
                    if (email.isBlank()) {
                        emailError = "Email required"
                        return@Button
                    }

                    // check if the password is empty
                    if (password.isBlank()) {
                        pwdError = "Password required"
                        return@Button
                    }

                    // check if the user is trying to register and if the password confirmation is empty
                    if (isRegister) {
                        if (confirmPassword.isBlank()) {
                            pwdConfirmError = "Confirm your password"
                            return@Button
                        }
                        // check if the confirm password mismatches the password
                        if (password != confirmPassword) {
                            pwdConfirmError = "Passwords do not match"
                            return@Button
                        }
                        // save on register
                        userInfo.saveUsername(username)
                        userInfo.saveEmail(email)
                        userInfo.savePassword(password)
                    }

                    // login or register mark user as logged
                    userInfo.saveLoginState(true)
                    // call onlogin success func
                    onLoginSuccess()
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
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(label, color = TextSecondary) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Black
                ),
                visualTransformation = if (isPassword)
                    PasswordVisualTransformation() else
                    VisualTransformation.None,
                singleLine = true
            )
        }

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(15.dp))
}