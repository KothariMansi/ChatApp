package com.example.chatapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatapp.data.Event
import com.example.chatapp.data.LCState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth
): ViewModel() {
    private var _uiState = MutableStateFlow(LCState())
    val uiState = _uiState.asStateFlow()

    init {

    }
    var inProgress = mutableStateOf(false)
    private val eventMutableState = mutableStateOf<Event<String>?>(null)

    fun updateUi(
        name: String = _uiState.value.name,
        number: String = _uiState.value.number,
        email: String = _uiState.value.email,
        password: String = _uiState.value.password


    ) {
        _uiState.update {
            it.copy(
                name = name,
                number = number,
                email = email,
                password = password
            )
        }
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        inProgress.value = true
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if (it.isSuccessful) {
                Log.d("TAG", "signUp: User Logged In")
            } else {
               // handleException()
            }
        }
    }

    private fun handleException(exception: Exception?=null, customMessage:String = "") {
        Log.e("TAG", "Live chat Exception: $exception")
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage?: ""
        val message = customMessage.ifEmpty { errorMsg }
        eventMutableState.value = Event(message)
        inProgress.value = false
    }

}