package com.example.chatapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.CommonImage
import com.example.chatapp.CommonProgressBar
import com.example.chatapp.DestinationScreen
import com.example.chatapp.LCViewModel
import com.example.chatapp.R
import com.example.chatapp.data.LCState
import com.example.chatapp.navigateTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    vm: LCViewModel
) {
    val uiState by vm.uiState.collectAsState()
    val inProcess = uiState.chatInProgress
    if (inProcess) {
        CommonProgressBar()
    } else {
        val userData = uiState.userData
        val chats = uiState.chats
        Fab(
            showDialog = uiState.isShowDialog,
            //  onFabClick = {  },
            vm = vm,
            uiState = uiState

        )
    }



    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
        topBar = {
            TopAppBar(title = { Text(text = "Chats", fontWeight = FontWeight.Bold) })
        },
        bottomBar = {
            BottomNavigationMenu(
                selectedItem = BottomNavigationItem.CHAT_LIST,
                navController = navController
            )
        },
        floatingActionButton = {
            IconButton(
                onClick = { vm.updateShowDialog(true) },
                colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }
    ) {
        if (uiState.chats.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Chats Available",
                    modifier = Modifier
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(it)) {
                items(uiState.chats) {chat ->
                    val chatUser = if (chat.user1.userId == uiState.userData.userId){
                        chat.user2
                    } else{
                        chat.user1
                    }

                    Row(modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .clickable {
                        if (chat.chatId != ""){
                            navigateTo(navController, DestinationScreen.SingleChat.createRoute(id = chat.chatId))
                        }
                    }) {
                        CommonImage(
                            data = chatUser.imageUrl,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .size(50.dp)
                                .padding(8.dp)
                        )
                        Text(text = chatUser.name, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 8.dp, top = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun Fab(
    showDialog: Boolean,
    vm: LCViewModel,
    uiState: LCState
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                vm.updateChatNumber("")
                vm.updateShowDialog(false)
            },
            confirmButton = {
                Button(onClick = {
                    vm.onAddChat(uiState.addChatNumber)
                    vm.updateShowDialog(false)
                }) {
                    Text(text = stringResource(R.string.add_chat))
                }
            },
            title = { Text(text = stringResource(R.string.add_chat)) },
            text = {
                OutlinedTextField(
                    value = uiState.addChatNumber,
                    onValueChange = { vm.updateChatNumber(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        )
    }
}
