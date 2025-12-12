package com.griffith.stepquest.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.griffith.stepquest.R
import java.io.File
import com.griffith.stepquest.ui.theme.*

// Creates a header bar with the title of the screen and the profile picture (clickable navigates to proifle screen)
@Composable
fun HeaderBar(headerTitle:String, navController: NavController){
    // loading the porifle picture from the local storage if it exists if not load default profile
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user"
    val file = File(context.filesDir, "profile_picture_$uid.png")
    val profileImage = if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)
    } else {
        BitmapFactory.decodeResource(context.resources, R.drawable.default_profile)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            ,
        horizontalArrangement = Arrangement.SpaceBetween ,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = headerTitle,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Dark
            )
        )
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
                .clip(CircleShape)
                .clickable { navController.navigate("profile") },
            contentScale = ContentScale.Crop
        )
    }
}
