package com.example.chatapp.data

open class Event<out T> (val content: T){
    private var hasBeenHandled = false
    fun getContentOrNUll(): T? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }
}