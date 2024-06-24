package com.example.chatapp.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.chatapp.CommonProgressBar
import com.example.chatapp.DestinationScreen
import com.example.chatapp.LCViewModel
import com.example.chatapp.navigateTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    vm: LCViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile") },
                navigationIcon = {
                    IconButton(onClick = { navigateTo(navController, DestinationScreen.ChatList.route) }) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "")
                    }
                },
                actions = {
                    TextButton(onClick = { /*TODO*/ }) {
                        Text(text = "Save")
                    }
                },
            )
        },
        bottomBar = {
            BottomNavigationMenu(selectedItem = BottomNavigationItem.STATUS_LIST, navController = navController)
        },
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        val inProgress = vm.inProgress.value
        if (inProgress) {
            CommonProgressBar()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            ProfileContent()
        }
    }

}

@Composable
fun ProfileContent() {
    // ProfileImage()
    Text(text = "Profile Screen", modifier = Modifier)
}

@Composable
fun ProfileImage(imageUr: String?, vm: LCViewModel) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {uri ->
        uri?.let {
            vm.uploadProfileImage(uri)
        }
        
    }
}