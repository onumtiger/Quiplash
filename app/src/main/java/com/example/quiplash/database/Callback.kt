package com.example.quiplash.database

interface Callback<T>{
    fun onTaskComplete(result: T)
}