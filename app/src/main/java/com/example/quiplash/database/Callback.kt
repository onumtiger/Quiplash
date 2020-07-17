package com.example.quiplash.database

//for database functions
interface Callback<T>{
    fun onTaskComplete(result: T)
}