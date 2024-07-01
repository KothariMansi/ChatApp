package com.example.chatapp.data

import java.sql.Timestamp

data class UserData(
    var userId: String = "",
    var name: String = "",
    var number: String = "",
    var imageUrl: String = "",
)

data class ChatData(
    val chatId: String = "",
    val user1: ChatUser = ChatUser(),
    val user2: ChatUser = ChatUser()
)

data class ChatUser(
    val userId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val number: String = ""

)

data class Message(
    var sendBy: String = "",
    val message: String = "",
    val time: String = ""
)

data class Status(
    val user: ChatUser = ChatUser(),
    val imageUrl: String = "",
    val timestamp: Long = 0
)
