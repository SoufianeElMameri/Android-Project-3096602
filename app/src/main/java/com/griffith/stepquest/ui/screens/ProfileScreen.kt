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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

import com.griffith.stepquest.R
import coil.compose.rememberAsyncImagePainter

import java.io.File
import java.io.FileOutputStream


// PROFILE SCREEN SHOWCASE THE USER PROFILE WITH STATS AND ALLOWS FOR PROIFLE PICTURE UPLOAD
@Composable
fun ProfileScreen() {
    // loading the porifle picture from the local storage if it exists if not load default profile
    val context = LocalContext.current
    // used to refresh the composable if we change the profile picture
    var refresh by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap =
                if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
            // saving the bitmap as a png in the local storage
            val file = File(context.filesDir, "profile_picture.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                refresh = !refresh
            }
        }
    }

    val file = File(context.filesDir, "profile_picture.png")
    val profileImage = if (file.exists()) {
        refresh
        BitmapFactory.decodeFile(file.absolutePath)
    } else {
        BitmapFactory.decodeResource(context.resources, R.drawable.default_profile)
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8FCD8), Color(0xFFFFF1C1))
                )
            ),
        color = Color.Transparent
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

//********************************************** TOP BAR *********************************************
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Profile picture with an edit button
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clickable {launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = if (profileImage != null)
                            rememberAsyncImagePainter(
                                model = profileImage
                            )
                        else
                            painterResource(id = R.drawable.default_profile),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = "Profile",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

//********************************************** USER LEVEL CARD *********************************************
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFFA6FFCB), Color(0xFFFFF3B0))
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "Level 12",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Active Explorer",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
//********************************************** OVERVIEW CARDS *********************************************
            Text(
                text = "Overview",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                OverviewCard(
                    "Longest Streak",
                    "42",
                    R.drawable.fire
                )
                OverviewCard(
                    "Current Streak",
                    "4",
                    R.drawable.fire
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                OverviewCard(
                    "Total Steps",
                    "15763435",
                    R.drawable.shoe
                )
                OverviewCard(
                    "Gold Medals",
                    "3",
                    R.drawable.gold_medal
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
//********************************************** OBTAINED MEDALS *********************************************
            Text(
                text = "Your Medals",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Color(0xFFFFFFFF).copy(alpha = 0.6f),
                        shape = RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bronze_medal),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.silver_medal),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.gold_medal),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.bronze_medal),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
//********************************************** USER RANK *********************************************
            Text(
                text = "Your Rank",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Color(0xFFFFFFFF).copy(alpha = 0.6f),
                        shape = RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gold_rank),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp)
                )
            }
        }
    }
}


//TO GENERATE OVERVIEW CARDS
@Composable
fun OverviewCard(title: String, value: String, iconRes: Int) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(110.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFA6FFCB), Color(0xFFFFF3B0))
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
