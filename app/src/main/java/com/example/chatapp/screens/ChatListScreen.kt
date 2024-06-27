package com.example.chatapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.example.chatapp.CommonProgressBar
import com.example.chatapp.LCViewModel
import com.example.chatapp.R
import com.example.chatapp.data.LCState

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
            onAddChat = {},
            vm = vm,
            uiState = uiState,

        )
    }

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
        topBar = {
            TopAppBar(title = { Text(text = "Chats") })
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
        }
    }
}

@Composable
fun Fab(
    showDialog: Boolean,
    onAddChat: (String) -> Unit,
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
                    onAddChat(uiState.addChatNumber)
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
