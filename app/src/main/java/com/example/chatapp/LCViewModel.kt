package com.example.chatapp

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatapp.data.Event
import com.example.chatapp.data.LCState
import com.example.chatapp.data.TAG
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
import java.util.UUID
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
    fun updateProfileContent(name: String = uiState.value.userData.name,
                             number: String = uiState.value.userData.number,
                             imageUrl: String = uiState.value.userData.imageUrl
    ) {
        _uiState.update {
            it.copy(
                userData = UserData(name = name, number = number, imageUrl = imageUrl)
            )
        }
    }

    fun updateUi(
        email: String = _uiState.value.email,
        password: String = _uiState.value.password

    ) {
        _uiState.update {
            it.copy(
              //  userData = UserData(name, number),
               // name = name,
               // number = number,
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
                    auth.currentUser?.uid?.let {uid ->
                        getUserDate(uid)
                    }
                }
                else {
                    handleException(it.exception, "Login Failed")
                }
            }
        }
    }

    fun signUp(context: Context, name: String, number: String, email: String, password: String) {
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
                        createOrUpdateProfile(context, name, number)
                    } else {
                        handleException(exception = it.exception, customMessage = "Sign Up Failed")
                    }
                }
            }
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if (it.isSuccessful) {
                signIn.value = true
                createOrUpdateProfile(context = context,name, number)
            } else {
                handleException(exception = it.exception, customMessage = "Sign Up Failed")
            }
        }

    }
    fun createOrUpdateProfile(
        context: Context,
        name: String = uiState.value.userData.name,
        number: String = uiState.value.userData.number,
        imageUrl: String = uiState.value.userData.imageUrl
    ) {
        var uid = auth.currentUser?.uid
        val userData = uid?.let {
            UserData(
                it,
                name= uiState.value.userData.name,
                number= uiState.value.userData.number,
                imageUrl= uiState.value.userData.imageUrl
            )
        }
        uid?.let {
            inProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()){
                    //Toast.makeText(context, "Updating", Toast.LENGTH_SHORT).show()
                    val updates = hashMapOf("name" to name, "number" to number, "imageUrl" to imageUrl)
                    db.collection(USER_NODE).document(uid).update(updates as Map<String, Any>)
                    // Todo: Update User Data
                    inProgress.value = false
                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                } else {
                    if (userData != null) {
                        db.collection(USER_NODE).document(uid).set(userData)
                    }
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
                if (user != null) {
                    uiState.value.userData = user
                }
                inProgress.value = false
            }
        }
    }

    private fun handleException(exception: Exception?=null, customMessage:String = "") {
        Log.e(TAG, "Live chat Exception: $exception")
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage?: ""
        val message = customMessage.ifEmpty { errorMsg }
        eventMutableState.value = Event(message)
        inProgress.value = false
    }

    fun uploadProfileImage(context: Context, uri: Uri) {
        uploadImage(context, uri){
            //Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show()
            updateProfileContent(imageUrl = it.toString())
            //createOrUpdateProfileNew(context, imageUrl = it.toString())
        }
    }

    private fun uploadImage(context: Context, uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("image/$uuid")
        val uploadTask = imageRef.putFile(uri)
       // Toast.makeText(context, "Image Progress", Toast.LENGTH_SHORT).show()
        uploadTask.addOnSuccessListener {
           // Toast.makeText(context, "SuccessListener Image", Toast.LENGTH_SHORT).show()
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
            inProgress.value = false
        }
            .addOnFailureListener{
                Toast.makeText(context, "Failure Listener Image", Toast.LENGTH_SHORT).show()

                handleException(it)
            }
    }

}