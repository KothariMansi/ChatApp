package com.example.chatapp.data

import androidx.compose.runtime.mutableStateOf

data class LCState(
    //val name: String = "",
    //val number: String = "",
    val email: String = "",
    val password: String = "",
    var userData: UserData = UserData(),
    val addChatNumber: String = "",
    val isShowDialog: Boolean = false,
    val chatInProgress:Boolean = false,
    val chats: List<ChatData> = listOf(),
    val reply:  String = "",
    val chatMessage: List<Message> = listOf(),
    val inProgressChatMessage: Boolean = false,

)