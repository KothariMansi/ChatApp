package com.example.chatapp.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.chatapp.LCViewModel

@Composable
fun SingleChatScreen(
    navController: NavController,
    vm: LCViewModel,
    chatId: String
) {
    Text(text = chatId)
}