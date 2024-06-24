package com.example.chatapp

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.chatapp.data.Event
import com.example.chatapp.data.LCState
import com.example.chatapp.data.USER_NODE
import com.example.chatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private var db: FirebaseFirestore,
    private var storage: FirebaseStorage
): ViewModel() {
    private var _uiState = MutableStateFlow(LCState())
    val uiState = _uiState.asStateFlow()

    var inProgress = mutableStateOf(false)
    private val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    var userDate = mutableStateOf<UserData?>(null)

    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserDate(it)
        }

    }
    fun clear() {
        _uiState.update { it.copy(
            email = "",
            password = ""
        ) }
    }
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

    fun login(email: String, password: String) {
        if (email.isEmpty() && password.isEmpty()) {

            handleException(customMessage = "Please fill all details")
            return
        }
        else {
            inProgress.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    signIn.value = true
                    inProgress.value = false
                    auth.currentUser?.uid?.let {
                        getUserDate(it)
                    }
                }
                else {
                    handleException(it.exception, "Login Failed")
                }
            }
        }
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        inProgress.value = true
        if (name.isEmpty() && number.isEmpty() && email.isEmpty() && password.isEmpty()) {
            handleException(customMessage = "Please fill all fields.")
            return
        }
        inProgress.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (!it.isEmpty) {
                handleException(customMessage = "Number Already Exists")
                inProgress.value = true
                //return@addOnSuccessListener
            }
            else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if (it.isSuccessful) {
                        signIn.value = true
                        createOrUpdateProfile(name, number)
                    } else {
                        handleException(exception = it.exception, customMessage = "Sign Up Failed")
                    }
                }
            }
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if (it.isSuccessful) {
                signIn.value = true
                createOrUpdateProfile(name, number)
            } else {
                handleException(exception = it.exception, customMessage = "Sign Up Failed")
            }
        }

    }

    private fun createOrUpdateProfile(name: String? = null, number: String? = null, imageUrl: String? = null) {
        var uid = auth.currentUser?.uid
        val userData = UserData(
            uid,
            name?: userDate.value?.name,
            number?: userDate.value?.number,
            imageUrl?: userDate.value?.imageUrl
        )
        uid?.let {
            inProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()){
                    // Todo: Update User Data
                } else {
                    db.collection(USER_NODE).document(uid).set(userData)
                    inProgress.value = false
                    getUserDate(uid)
                }
            }.addOnFailureListener {
                handleException(it, "Cannot retrieve User")
            }
        }
    }

    private fun getUserDate(uid: String) {
        inProgress.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener{
            value, error ->
            if (error!=null) {
                handleException(error, "Cannot retrieve User")
            }
            if (value != null) {
                var user = value.toObject<UserData>()
                userDate.value = user
                inProgress.value = false
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

    fun uploadProfileImage(uri: Uri) {
        //uploadImage(){}
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true
        val storageRef = storage.reference
    }

}