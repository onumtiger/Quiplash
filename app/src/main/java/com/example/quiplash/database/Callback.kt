package com.example.quiplash.database

//for database funcions
interface Callback<T>{
    fun onTaskComplete(result: T)
}