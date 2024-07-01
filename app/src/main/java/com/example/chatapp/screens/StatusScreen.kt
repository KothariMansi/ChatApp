package com.example.chatapp.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.CommonImage
import com.example.chatapp.CommonProgressBar
import com.example.chatapp.CommonRow
import com.example.chatapp.DestinationScreen
import com.example.chatapp.LCViewModel
import com.example.chatapp.R
import com.example.chatapp.navigateTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(
    navController: NavController,
    vm: LCViewModel
) {
    val context = LocalContext.current
    val lcState by vm.uiState.collectAsState()
    val inProgress = lcState.inProgressStatus
    if (inProgress) {
        CommonProgressBar()
    } else {
        val statuses = lcState.status
        val userData = lcState.userData
        val myStatus = statuses.filter {
            it.user.userId == userData.userId
        }
        val otherStatus = statuses.filter {
            it.user.userId != userData.userId
        }
        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {uri ->
            uri?.let {
                vm.uploadStatus(uri, context = context)
            }
        }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.status)) }
                )
            },
            floatingActionButton = {
                FAB {
                    launcher.launch("image/*")
                }
            },
            bottomBar = {
                BottomNavigationMenu(selectedItem = BottomNavigationItem.STATUS_LIST, navController = navController)
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
            ) {
                if (statuses.isEmpty()){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Status Available",
                            modifier = Modifier
                        )
                    }
                } else {
                    if (myStatus.isNotEmpty()) {
                        CommonRow(
                            chatUser = myStatus[0].user,
                            navController = navController,
                            destinationScreen = DestinationScreen.SingleStatus.createRoute(statusId = myStatus[0].user.userId)
                        )
//                        Row(modifier = Modifier
//                            .padding(4.dp)
//                            .fillMaxWidth()
//                            .clickable {
//                                if (otherStatus[0].user.userId != "") {
//                                    navigateTo(
//                                        navController,
//                                        DestinationScreen.SingleStatus.createRoute(statusId = myStatus[0].user.userId)
//                                    )
//                                }
//                            }) {
//                            CommonImage(
//                                data = myStatus[0].user.imageUrl,
//                                modifier = Modifier
//                                    .background(MaterialTheme.colorScheme.background)
//                                    .size(50.dp)
//                                    .padding(8.dp)
//                            )
//                            Text(
//                                text = myStatus[0].user.name,
//                                fontWeight = FontWeight.SemiBold,
//                                modifier = Modifier.padding(start = 8.dp, top = 12.dp)
//                            )
//                        }
                    }

                    val uniqueStatus = otherStatus.map { it.user }.toSet().toList()
                    LazyColumn {
                        items(uniqueStatus) {user ->
                            CommonRow(
                                chatUser = user,
                                navController = navController,
                                destinationScreen = DestinationScreen.SingleStatus.createRoute(statusId = myStatus[0].user.userId)
                            )

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FAB(
    onFabClick: () -> Unit
) {
    FloatingActionButton(onClick = onFabClick, containerColor = MaterialTheme.colorScheme.secondary) {
        Icon(imageVector = Icons.Filled.Create, contentDescription = "Add Status")
    }
}