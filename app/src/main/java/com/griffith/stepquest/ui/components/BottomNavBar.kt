package com.griffith.stepquest.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.griffith.stepquest.R
import com.griffith.stepquest.ui.theme.*

// function that creates a navigation bar
@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = NavBackgroundColor,
        tonalElevation = 8.dp
    ){

//******************************************** HOME ********************************************
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                if (currentRoute != "home") {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Home",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = {
                Text(
                    text = "Home",
                    color = Dark,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = SelectedNavItemColor
            )
        )

//******************************************** CHALLENGES ********************************************
        NavigationBarItem(
            selected = currentRoute == "challenges",
            onClick = {
                if (currentRoute != "challenges") {
                    navController.navigate("challenges") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.target),
                    contentDescription = "Challenges",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = {
                Text(
                    text = "Challenges",
                    color = Dark,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = SelectedNavItemColor
            )
        )


//******************************************** BADGES ********************************************
        NavigationBarItem(
            selected = currentRoute == "badges",
            onClick = {
                if (currentRoute != "badges") {
                    navController.navigate("badges") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.badge),
                    contentDescription = "Badges",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = {
                Text(
                    text = "Badges",
                    color = Dark,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = SelectedNavItemColor
            )
        )
//******************************************** RANKS ********************************************
        NavigationBarItem(
            selected = currentRoute == "rank",
            onClick = {
                if (currentRoute != "rank") {
                    navController.navigate("rank") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.leader),
                    contentDescription = "rank",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = {
                Text(
                    text = "Rank",
                    color = Dark,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = SelectedNavItemColor
            )
        )

        // I AM KEEPING THE SHOP CODE FOR FUTURE TO BE IMPLEMENTED
//******************************************** SHOP ********************************************
//        NavigationBarItem(
//            selected = currentRoute == "shop",
//            onClick = {
//                if (currentRoute != "shop") {
//                    navController.navigate("shop") {
//                        popUpTo("home") { inclusive = false }
//                        launchSingleTop = true
//                    }
//                }
//            },
//            icon = {
//                Image(
//                    painter = painterResource(id = R.drawable.shop),
//                    contentDescription = "Shop",
//                    modifier = Modifier.size(28.dp)
//                )
//            },
//            label = {
//                Text(
//                    text = "Shop",
//                    color = Dark,
//                    fontSize = MaterialTheme.typography.labelSmall.fontSize
//                )
//            },
//            colors = NavigationBarItemDefaults.colors(
//                indicatorColor = Color(0xFF00FF00)
//            )
//        )
    }
}
