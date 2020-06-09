package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class QuestionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
/*
        Handler().postDelayed({
            val intent = Intent(this, Prepare_AnswerActivity::class.java);
            startActivity(intent)
        }, 3000)*/
    }
}