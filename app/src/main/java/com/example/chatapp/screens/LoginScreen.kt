package com.example.chatapp.screens

import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.LCViewModel
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun LoginScreen(
    navController: NavController,
    vm: LCViewModel
) {
    Text(text = "Hii THis is Login Screen!", modifier=Modifier.background(color= Color.Blue))
}

@Preview
@Composable
fun LoginScreenPreview() {
    ChatAppTheme {
        //LoginScreen(rememberNavController(), LCViewModel())
    }
}