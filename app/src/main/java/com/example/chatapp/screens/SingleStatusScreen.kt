package com.example.chatapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.chatapp.LCViewModel

@Composable
fun SingleStatusScreen(
    navController: NavController,
    vm: LCViewModel,
    statusId: String
) {
    Text(text = statusId,modifier = Modifier.fillMaxSize())
}