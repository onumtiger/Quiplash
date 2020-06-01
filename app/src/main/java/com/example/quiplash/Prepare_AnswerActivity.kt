package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Prepare_AnswerActivity : AppCompatActivity() {

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prepare_answer)

        val btnReady = findViewById<Button>(R.id.btnReady)

        btnReady.setOnClickListener() {
            val intent = Intent(this, Choose_AnswerActivity::class.java);
            startActivity(intent);
        }
    }
}