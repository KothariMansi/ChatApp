package com.example.chatapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.CommonImage
import com.example.chatapp.LCViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChatScreen(
    navController: NavController,
    vm: LCViewModel,
    chatId: String,
    //name: String
) {
    // val context = LocalContext.current
    val lcState by vm.uiState.collectAsState()
    val myUser = lcState.userData
    val currentChats = lcState.chats.first { it.chatId == chatId }


    val chatUser =
        if (myUser.userId == currentChats.user1.userId) currentChats.user2 else currentChats.user1
    LaunchedEffect(key1 = Unit) {
        vm.populateMessage(chatId)
    }
    BackHandler {
        vm.depopulateMessage()
    }
    /* Make sure to do that if image is empty than show tha user logo Todo */
    // Text(text = chatId)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(35.dp)
                        ) {
                            CommonImage(data = chatUser.imageUrl)
                        }
                        Text(text = chatUser.name, fontWeight = FontWeight.SemiBold)
                    }
                },
                //scrollBehavior = TopAppBarDefaults.(),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                        vm.depopulateMessage()
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                }
            )
        },
        bottomBar = {
            ReplyBox(
                reply = lcState.reply,
                onReplyChange = { vm.updateReply(it) }
            ) {
                if (lcState.reply.isNotBlank()) {
                    vm.onSendReply(chatId = chatId, message = lcState.reply)
                    vm.updateReply("")
                }
            }
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .verticalScroll(rememberScrollState())) {
            Text(text = lcState.chatMessage.toString())
        }
    }

}

@Composable
fun ReplyBox(
    reply: String,
    onReplyChange: (String) -> Unit,
    onSendReply: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(value = reply, onValueChange = { onReplyChange(it) })
            Button(onClick = { onSendReply() }) {
                Text(text = "Send")
            }
        }
    }

}