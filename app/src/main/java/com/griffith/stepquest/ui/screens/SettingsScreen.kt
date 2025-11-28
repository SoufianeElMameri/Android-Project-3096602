package com.griffith.stepquest.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

import com.griffith.stepquest.R
import coil.compose.rememberAsyncImagePainter
import com.griffith.stepquest.data.UserInformation

import java.io.File
import java.io.FileOutputStream
import com.griffith.stepquest.ui.theme.*

import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.input.PasswordVisualTransformation

// PROFILE SCREEN SHOWCASE THE USER PROFILE WITH STATS AND ALLOWS FOR PROIFLE PICTURE UPLOAD
@Composable
fun SettingsScreen(userInfo: UserInformation, onLogout: () -> Unit) {

    val context = LocalContext.current
    var showUsernameDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }

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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

//***************************************************** HEADER *****************************************************
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Settings",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

//***************************************************** ACCOUNT SECTION *****************************************************
            Text(
                text = "Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Dark
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingsItem(
                title = "Change User Name",
                iconRes = R.drawable.username
            ) {
                showUsernameDialog = true
            }

            SettingsItem(
                title = "Change Password",
                iconRes = R.drawable.pwd
            ) {
                showPasswordDialog = true
            }
            if (showUsernameDialog) {
                ChangeUsernameDialog(
                    userInfo = userInfo,
                    iconRes = R.drawable.username,
                    onDismiss = { showUsernameDialog = false },
                    onSuccess = { successMessage = it }
                )
            }

            if (showPasswordDialog) {
                ChangePasswordDialog(
                    userInfo = userInfo,
                    iconRes = R.drawable.pwd,
                    onDismiss = { showPasswordDialog = false },
                    onSuccess = { successMessage = it }
                )
            }
            if (successMessage != null) {
                SuccessDialog(
                    message = successMessage!!,
                    onDismiss = { successMessage = null }
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
//***************************************************** LEGAL SECTION *****************************************************
            Text(
                text = "Legal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Dark
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingsItem(
                title = "Terms & Conditions",
                iconRes = R.drawable.tc
            ) {}

            SettingsItem(
                title = "Privacy Policy",
                iconRes = R.drawable.privacy
            ) {}

            Spacer(modifier = Modifier.height(30.dp))

//***************************************************** logout button *****************************************************
            Button(
                onClick = {
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AlertRed
                )
            ) {
                Text(
                    text = "Logout",
                    fontSize = 18.sp,
                    color = Bright
                )
            }
        }
    }
}


// reusable compoenent to create settings items
@Composable
fun SettingsItem(title: String, iconRes: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() }
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                clip = false
            )
            .background(
                Bright,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Dark
        )
    }
}

// component to create a dialog for the user to change their user name
@Composable
fun ChangeUsernameDialog(userInfo: UserInformation, iconRes: Int, onDismiss: () -> Unit, onSuccess: (String) -> Unit) {
    var newName by remember { mutableStateOf(userInfo.getUsername() ?: "") }
    var error by remember { mutableStateOf<String?>(null) }


    Dialog(onDismissRequest = { onDismiss() }) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(18.dp))
                .background(Bright, RoundedCornerShape(18.dp))
                .padding(20.dp)
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//***************************************************** HEADER TITLE WITH IMAGE *****************************************************
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Change Username",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Dark
                )

                Spacer(modifier = Modifier.height(16.dp))
//***************************************************** NEW USER NAME FIELD *****************************************************
                OutlinedTextField(
                    value = newName,
                    onValueChange = {
                        newName = it
                        error = null
                    },
                    label = { Text("New Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null
                )

                if (error != null) {
                    Text(error!!, color = AlertRed, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))
//***************************************************** CONFIRM CANCEL BUTTONS *****************************************************

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // cancel button
                    Box(
                        modifier = Modifier
                            .background(AlertRed, RoundedCornerShape(10.dp)) // red
                            .clickable { onDismiss() }
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 15.sp,
                            color = Bright,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // save button
                    Box(
                        modifier = Modifier
                            .background(ConfirmGreen, RoundedCornerShape(10.dp)) // green
                            .clickable {
                                if (newName.isBlank()) {
                                    error = "Username cannot be empty"
                                    return@clickable
                                }

                                userInfo.saveUsername(newName)
                                onDismiss()
                                onSuccess("Username changed successfully!")
                            }
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 15.sp,
                            color = Bright,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}


// component to create a dialog for the user to change their username
@Composable
fun ChangePasswordDialog(userInfo: UserInformation, iconRes: Int, onDismiss: () -> Unit, onSuccess: (String) -> Unit) {

    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = { onDismiss() }) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(18.dp))
                .background(Bright, RoundedCornerShape(18.dp))
                .padding(20.dp)
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//***************************************************** HEADER TITLE WITH IMAGE *****************************************************
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Change Password",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Dark
                )

                Spacer(modifier = Modifier.height(16.dp))

//***************************************************** OLD PASSWORD FIELD *****************************************************

                OutlinedTextField(
                    value = oldPass,
                    onValueChange = {
                        oldPass = it
                        error = null
                    },
                    label = { Text("Old Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = error != null
                )

                Spacer(modifier = Modifier.height(14.dp))

//***************************************************** NEW PASSWORD FIELD *****************************************************

                OutlinedTextField(
                    value = newPass,
                    onValueChange = {
                        newPass = it
                        error = null
                    },
                    label = { Text("New Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = error != null
                )

                Spacer(modifier = Modifier.height(14.dp))

//***************************************************** CONFIRM PASSWORD FIELD *****************************************************
                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = {
                        confirmPass = it
                        error = null
                    },
                    label = { Text("Confirm New Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = error != null
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(error!!, color = AlertRed, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

//***************************************************** CONFIRM CANCEL BUTTONS *****************************************************
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // cancel botton
                    Box(
                        modifier = Modifier
                            .background(AlertRed, RoundedCornerShape(10.dp)) // red
                            .clickable { onDismiss() }
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 15.sp,
                            color = Bright,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // save botton
                    Box(
                        modifier = Modifier
                            .background(ConfirmGreen, RoundedCornerShape(10.dp)) // green
                            .clickable {

                                val storedPass = userInfo.getPassword() ?: ""

                                if (oldPass != storedPass) {
                                    error = "Old password is incorrect"
                                    return@clickable
                                }

                                if (newPass.isBlank()) {
                                    error = "New password cannot be empty"
                                    return@clickable
                                }

                                if (newPass != confirmPass) {
                                    error = "Passwords do not match"
                                    return@clickable
                                }

                                userInfo.savePassword(newPass)
                                onDismiss()
                                onSuccess("Password changed successfully!")
                            }
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 15.sp,
                            color = Bright,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// function that creates a success popup
@Composable
fun SuccessDialog(message: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(18.dp))
                .background(Bright, RoundedCornerShape(18.dp))
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.check),
                    contentDescription = null,
                    tint = ConfirmGreen,
                    modifier = Modifier.size(46.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = message,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Dark
                )

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .background(ConfirmGreen, RoundedCornerShape(10.dp))
                        .clickable { onDismiss() }
                        .padding(vertical = 10.dp, horizontal = 26.dp)
                ) {
                    Text(
                        text = "OK",
                        fontSize = 15.sp,
                        color = Bright,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}