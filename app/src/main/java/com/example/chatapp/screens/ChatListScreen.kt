package com.example.chatapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.chatapp.LCViewModel

@Composable
fun ChatListScreen(
    navController: NavController,
    vm: LCViewModel
) {
    Scaffold(
        bottomBar = {
            BottomNavigationMenu(selectedItem = BottomNavigationItem.CHAT_LIST, navController = navController)
        }
    ) {
        Text(text = "CHat list Screen", modifier = Modifier.fillMaxSize().padding(it))
    }
}