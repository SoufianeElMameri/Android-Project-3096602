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
import androidx.compose.material3.Icon
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
import androidx.navigation.NavController

import com.griffith.stepquest.R
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import java.io.File
import java.io.FileOutputStream
import com.griffith.stepquest.ui.theme.*
import com.griffith.stepquest.data.ProfileManager
import com.griffith.stepquest.ui.viewmodels.UserViewModel

// PROFILE SCREEN SHOWCASE THE USER PROFILE WITH STATS AND ALLOWS FOR PROIFLE PICTURE UPLOAD
@Composable
fun ProfileScreen(navController: NavController, userVM: UserViewModel) {

    var firebaseUrl by remember { mutableStateOf("") }

    val title       = userVM.userTitle
    val level       = userVM.userLevel
    val nextLevel   = userVM.userNextLevelExp
    val userXp      = userVM.userExperience

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val url = doc.getString("profile_picture")
                    if (url != null) {
                        firebaseUrl = url
                    }
                }
        }
    }
    // loading the porifle picture from the local storage if it exists if not load default profile
    val context = LocalContext.current
    // used to refresh the composable if we change the profile picture
    var refresh by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {

            val bitmap =
                if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }

            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user"
            val file = File(context.filesDir, "profile_picture_$uid.png")

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            ProfileManager.uploadProfileImage(
                uri,
                onSuccess = { },
                onError = { }
            )

            refresh = !refresh
        }
    }

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user"
    val file = File(context.filesDir, "profile_picture_$uid.png")

    var localBitmap: Bitmap? = null

    if (file.exists()) {
        refresh
        localBitmap = BitmapFactory.decodeFile(file.absolutePath)
    }

    val finalPainter =
        if (firebaseUrl.isNotEmpty())
            rememberAsyncImagePainter(firebaseUrl)
        else if (localBitmap != null)
            rememberAsyncImagePainter(localBitmap)
        else
            painterResource(R.drawable.default_profile)


    val profileUrl = ""

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
                        painter = finalPainter,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(60.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                clip = false
                            )
                            .border(
                                width = 3.dp,
                                color = Bright,
                                shape = CircleShape
                            )
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                            .background(Bright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Profile",
                            tint = Dark,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = "Profile",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )

                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = TextPrimary,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { navController.navigate("settings") },
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

//********************************************** USER LEVEL CARD *********************************************
            val progressRatio = (userXp.toFloat() / nextLevel.toFloat()).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false
                    )
                    .background(
                        color = Bright,
                        shape = RoundedCornerShape(25.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {

                    Text(
                        text = "Level: $level",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Dark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = title,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(EmptyBarColor, RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progressRatio)
                                .background(LimeColor, RoundedCornerShape(4.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$userXp / $nextLevel XP",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
//********************************************** OVERVIEW CARDS *********************************************
            Text(
                text = "Overview",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Dark
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
                color = Dark
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(CardTopColor, CardBottomColor)
                        ),
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
                color = Dark
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(22.dp),
                        clip = false
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(CardTopColor, CardBottomColor)
                        ),
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
            Spacer(modifier = Modifier.height(28.dp))
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
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            )
            .background(
                Bright,
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Dark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Dark
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
