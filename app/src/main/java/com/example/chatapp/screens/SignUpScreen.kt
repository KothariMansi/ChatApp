package com.example.chatapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chatapp.CommonProgressBar
import com.example.chatapp.DestinationScreen
import com.example.chatapp.LCViewModel
import com.example.chatapp.R
import com.example.chatapp.navigateTo

@Composable
fun SignUpScreen(navController: NavController, vm: LCViewModel) {

    val lcState by vm.uiState.collectAsState()
    val focus = LocalFocusManager.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight()
            .verticalScroll(
                rememberScrollState()
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.chat),
                contentDescription = null,
                modifier = Modifier
                    .width(150.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )
            Text(
                text = stringResource(R.string.sign_up),
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            OutlinedTextField(
                value = lcState.name,
                onValueChange = { vm.updateUi(name = it) },
                label = { Text(text = stringResource(R.string.name)) },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = lcState.number,
                onValueChange = {vm.updateUi(number = it)} ,
                label = { Text(text = stringResource(R.string.phone)) },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = lcState.email,
                onValueChange = {vm.updateUi(email = it)} ,
                label = { Text(text = stringResource(R.string.email)) },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = lcState.password,
                onValueChange = {vm.updateUi(password = it)} ,
                label = { Text(text = stringResource(R.string.password)) },
                modifier = Modifier.padding(8.dp)
            )
            
            Button(
                onClick = { vm.signUp(lcState.name, lcState.number, lcState.email, lcState.password) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = stringResource(id = R.string.sign_up))
            }

            Text(
                text = "Already a user? Go to Login ->",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.Login.route)
                    },
            )
        }
    }

    if (vm.inProgress.value) {
        CommonProgressBar()
    }
}