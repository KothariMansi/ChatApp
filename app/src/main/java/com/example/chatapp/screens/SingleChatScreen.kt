package com.example.chatapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chatapp.CommonImage
import com.example.chatapp.LCViewModel
import com.example.chatapp.ui.theme.ChatAppTheme

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
        navController.popBackStack()
    }
    /* Make sure to do that if image is empty than show tha user logo Todo */
    // Text(text = chatId)
    Scaffold(
        topBar = {
            TopAppBar(
               // windowInsets = WindowInsets.,
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                  //  .consumeWindowInsets()
                ,
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
                onReplyChange = { vm.updateReply(it) },
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                ) {
                if (lcState.reply.isNotBlank()) {
                    vm.onSendReply(chatId = chatId, message = lcState.reply)
                    vm.updateReply("")
                }
            }
        }
    ) {
       // val scrollState = rememberScrollState()
        Box(modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            //.verticalScroll(scrollState)
        ){
            LazyColumn(modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .padding(8.dp)
                // .verticalScroll(rememberScrollState())
            ) {
                items(lcState.chatMessage){
                    if (it.sendBy == myUser.userId){
                        Row(modifier = Modifier.padding(4.dp)) {
                            Spacer(modifier = Modifier.weight(1f))
                            Card(
                                modifier = Modifier
                            ) {
                                Text(
                                    text = it.message,
                                    textAlign = TextAlign.Right ,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                    }
                    else{
                        Row(modifier = Modifier.padding(4.dp)) {
                            Card(
                                modifier = Modifier
                            ) {
                                Text(
                                    text = it.message,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ReplyBox(
    reply: String,
    onReplyChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSendReply: () -> Unit,
) {
    Box(modifier = modifier){
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
}