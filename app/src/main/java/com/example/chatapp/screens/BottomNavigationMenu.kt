package com.example.chatapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.DestinationScreen
import com.example.chatapp.R
import com.example.chatapp.navigateTo
import com.example.chatapp.ui.theme.ChatAppTheme

enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen){
    CHAT_LIST(R.drawable.chat_icon, DestinationScreen.ChatList),
    STATUS_LIST(R.drawable.status, DestinationScreen.StatusList),
    PROFILE(R.drawable.profile, DestinationScreen.Profile)
}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem, navController: NavController
) {
    BottomAppBar(
        modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxWidth(),
    ) {
        Image(
            painter = painterResource(id = BottomNavigationItem.CHAT_LIST.icon),
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .weight(1f)
                .clickable {
                    navigateTo(navController = navController, DestinationScreen.ChatList.route)
                },
            colorFilter = if (selectedItem == BottomNavigationItem.CHAT_LIST) ColorFilter.tint(Color.Black) else ColorFilter.tint(Color.Gray)

        )
        Image(
            painter = painterResource(id = BottomNavigationItem.STATUS_LIST.icon),
            contentDescription = "",
            modifier = Modifier.size(40.dp).weight(1f).clickable {
                navigateTo(navController = navController, DestinationScreen.StatusList.route)
            },
            colorFilter = if (selectedItem == BottomNavigationItem.STATUS_LIST) ColorFilter.tint(Color.Black) else ColorFilter.tint(Color.Gray)
        )
        Image(
            painter = painterResource(id = BottomNavigationItem.PROFILE.icon),
            contentDescription = "",
            modifier = Modifier.size(40.dp).weight(1f).clickable {
                navigateTo(navController = navController, DestinationScreen.Profile.route)
            },
            colorFilter = if (selectedItem == BottomNavigationItem.PROFILE) ColorFilter.tint(Color.Black) else ColorFilter.tint(Color.Gray)
        )

    }

}

@Preview
@Composable
fun BottomNavigationMenuPreview() {
    ChatAppTheme {
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.CHAT_LIST,
            navController = rememberNavController()
        )
    }
}