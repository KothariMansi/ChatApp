package com.example.chatapp.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.CommonImage
import com.example.chatapp.CommonProgressBar
import com.example.chatapp.DestinationScreen
import com.example.chatapp.LCViewModel
import com.example.chatapp.R
import com.example.chatapp.navigateTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    vm: LCViewModel,
    context: Context
) {
    val lcState by vm.uiState.collectAsState()
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
                    TextButton(onClick = { vm.createOrUpdateProfile(
                        context = context,
                        name = lcState.userData.name, number = lcState.userData.number
                    ) }) {
                        Text(text = "Save")
                    }
                },
            )
        },
        bottomBar = {
            BottomNavigationMenu(selectedItem = BottomNavigationItem.PROFILE, navController = navController)
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
            ProfileContent(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                vm = vm,
                context = context,
                navigate = { navigateTo(navController, DestinationScreen.Login.route) }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    context: Context,
    vm: LCViewModel,
    navigate:() -> Unit,
    modifier: Modifier,
    //name: String,
   // number: String
) {
    val lcState by vm.uiState.collectAsState()

    val imageUrl = lcState.userData.imageUrl
    ProfileImage(context = context, imageUrl = imageUrl, vm = vm)

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = R.string.name), modifier = Modifier
            .weight(1f)
            .padding(start = 16.dp, top = 8.dp))
        lcState.userData.name.let { name ->
            TextField(
                value = name,
                onValueChange = {  vm.updateProfileContent(name = it) } ,
                colors = TextFieldDefaults.textFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier
                    .weight(3f)
                    .padding(horizontal = 8.dp)
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = R.string.phone), modifier = Modifier
            .weight(1f)
            .padding(start = 16.dp, top = 8.dp))
        lcState.userData.number.let {
            TextField(
                value = it,
                onValueChange = { lcState.userData.name.let { it1 -> vm.updateProfileContent(number = it, name = it1) } },
                colors = TextFieldDefaults.textFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                ),
                modifier = Modifier
                    .weight(3f)
                    .padding(horizontal = 8.dp)
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                vm.onLogOut()
                navigate()
            },
        horizontalArrangement = Arrangement.Center) {
            Text(text = "LogOut")
    }
}

@Composable
fun ProfileImage(context:Context, imageUrl: String?, vm: LCViewModel) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {uri ->
        uri?.let {
            vm.uploadProfileImage(context, uri)
        }
    }
    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                CommonImage(data = imageUrl) 
            }
            Text(text = "Change profile picture")
        }
        if (vm.inProgress.value) {
            CommonProgressBar()
        }
    }
}