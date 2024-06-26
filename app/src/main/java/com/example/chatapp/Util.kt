package com.example.chatapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.chatapp.data.ChatData
import com.example.chatapp.data.ChatUser

fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route){
        popUpTo(route)
        launchSingleTop = true
    }
}

@Composable
fun CommonProgressBar() {
    Row(
        modifier = Modifier
            .alpha(0.05f)
            .background(Color.LightGray)
            .fillMaxSize()
            .clickable(enabled = false) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CheckSignedIn(vm: LCViewModel,navController: NavController) {
    val alreadySignedIn = remember { mutableStateOf(false) }
    val signIn = vm.signIn.value
    if (signIn && !alreadySignedIn.value){
        alreadySignedIn.value = true
        navController.navigate(DestinationScreen.ChatList.route) {
            popUpTo(0)
        }
    }
}

@Composable
fun CommonImage(
    data: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = rememberImagePainter(data = data)
    Image(painter = painter, contentDescription = null, modifier = modifier.wrapContentSize().clip(CircleShape).fillMaxSize(),contentScale = contentScale)
}

@Composable
fun CommonRow(
    chatUser: ChatUser,
    navController: NavController,
    destinationScreen: String
) {
    Row(modifier = Modifier
        .padding(4.dp)
        .fillMaxWidth()
        .clickable {
                navigateTo(navController, destinationScreen)
        }
    ) {
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