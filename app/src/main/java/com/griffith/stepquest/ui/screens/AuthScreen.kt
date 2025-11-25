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
                modifier = Modifier.fillMaxWidth()
            ) {
//                Each text is clickable and on click will hide or reveal more text field
                Text(
                    "Login",
                    fontSize = 20.sp,
                    color = if (!isRegister) Color.Black else Color.Gray,
                    modifier = Modifier.clickable { isRegister = false }
                )
                Text(
                    "Register",
                    fontSize = 20.sp,
                    color = if (isRegister) Color.Black else Color.Gray,
                    modifier = Modifier.clickable { isRegister = true }
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            // only show the username text field if the user is trying to register
            if (isRegister) {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (usernameError.isNotEmpty()) {
                    Text(usernameError, color = Color.Red, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
            // always show the email text field
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError.isNotEmpty()) {
                Text(emailError, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(15.dp))

            // always show the password text field
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            if (pwdError.isNotEmpty()) {
                Text(pwdError, color = Color.Red, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(15.dp))

            // only show the confirm password text field if the user is trying to register
            if (isRegister) {
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (pwdConfirmError.isNotEmpty()) {
                    Text(pwdConfirmError, color = Color.Red, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(15.dp))
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
