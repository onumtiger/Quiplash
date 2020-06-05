package com.example.quiplash

interface Callback<T>{
    fun onTaskComplete(result: T)
}