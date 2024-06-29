package com.example.chatapp

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.chatapp.data.CHATS
import com.example.chatapp.data.ChatData
import com.example.chatapp.data.ChatUser
import com.example.chatapp.data.Event
import com.example.chatapp.data.LCState
import com.example.chatapp.data.MESSAGE
import com.example.chatapp.data.Message
import com.example.chatapp.data.TAG
import com.example.chatapp.data.USER_NODE
import com.example.chatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.lang.Exception
import java.util.Calendar
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
            populateChats()
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
            updateProfileContent(imageUrl = it.toString())
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

    fun onLogOut() {
        auth.signOut()
        signIn.value = false
        _uiState.update { it.copy(userData = UserData("","","","")) }
        eventMutableState.value = Event("Logged Out")
    }

    fun updateChatNumber(chatMember: String) {
        _uiState.update { it.copy(
            addChatNumber = chatMember
        ) }
    }
    fun updateShowDialog(showDialog: Boolean) {
        _uiState.update { it.copy(isShowDialog = showDialog) }
    }

    private fun updateChatProgress(chatInProgress: Boolean) {
        _uiState.update { it.copy(chatInProgress = chatInProgress) }
    }

    fun onAddChat(number: String) {
        // updateChatProgress(true)
        if (number.isEmpty() or !number.isDigitsOnly()){
            handleException(customMessage = "Not Valid Number")
        }else{
            db.collection(CHATS).where(Filter.or(
                Filter.and(
                    Filter.equalTo("user1.number", number),
                    Filter.equalTo("user2.number", uiState.value.userData.number)
                ),
               Filter.and(
                   Filter.equalTo("user1.number",  uiState.value.userData.number),
                   Filter.equalTo("user2.number",number)
               )
            )).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
                        if (it.isEmpty) {
                            handleException(customMessage = "number not found")
                        }else{
                            val chatPartners = it.toObjects<UserData>()[0]
                            val id = db.collection(CHATS).document().id
                            val chat = ChatData(
                                chatId = id,
                                user1 = ChatUser(
                                    uiState.value.userData.userId,
                                    uiState.value.userData.name,
                                    uiState.value.userData.imageUrl,
                                    uiState.value.userData.number,
                                ),
                                user2 = ChatUser(
                                    chatPartners.userId,
                                    chatPartners.name,
                                    chatPartners.imageUrl,
                                    chatPartners.number
                                )
                            )
                            db.collection(CHATS).document(id).set(chat)
                        }
                    }.addOnFailureListener {ex ->
                        handleException(ex)
                    }
                }
                else{
                    handleException(customMessage = "Chats Already exists")
                }
            }
        }
        // updateChatProgress(false)
    }

    private fun populateChats() {
        updateChatProgress(true)
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", uiState.value.userData.userId),
                Filter.equalTo("user2.userId", uiState.value.userData.userId)
            )
        ).addSnapshotListener{value, error ->
            if (error != null) {
                handleException(error)
            }
            if (value != null) {
                _uiState.update {state ->
                    state.copy(
                        chats = value.documents.mapNotNull {
                            it.toObject<ChatData>()
                        })
                }
                updateChatProgress(false)
            }
        }
    }

    fun updateReply(reply: String) {
        _uiState.update { it.copy(reply = reply) }
    }

    fun onSendReply(chatId: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(uiState.value.userData.userId, message, time)
        db.collection(CHATS).document(chatId).collection(MESSAGE).document().set(msg)
    }
    private var currentChatMessageListener: ListenerRegistration? = null

    fun populateMessage(chatId: String) {
        _uiState.update { it.copy(inProgressChatMessage = true) }
        currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGE).addSnapshotListener{value, error ->
            if (error != null) {
                handleException(error)
            } else {
                if (value != null) {
                    _uiState.update { it.copy(
                        chatMessage = value.documents.mapNotNull {
                            it.toObject<Message>()
                        }.sortedBy { it.time },
                        inProgressChatMessage = false
                    ) }
                }
            }
        }
    }

    fun depopulateMessage() {
        _uiState.update { it.copy(
            chatMessage = listOf(),
        ) }
        currentChatMessageListener = null
    }

}