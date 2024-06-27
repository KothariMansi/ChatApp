package com.example.chatapp.data

data class UserData(
    var userId: String = "",
    var name: String = "",
    var number: String = "",
    var imageUrl: String = "",
)

data class ChatData(
    val chatId: String = "",
    val user1: ChatUser = ChatUser()
)

data class ChatUser(
    val userId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val number: String = ""

)
