package com.example.quiplash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton

class FriendsActivity : AppCompatActivity() {


    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        val btnBack = findViewById<AppCompatImageButton>(R.id.friends_go_back_arrow)

        btnBack.setOnClickListener() {
            super.onBackPressed();
        }


    }

}
