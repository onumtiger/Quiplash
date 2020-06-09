package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class Choose_AnswerActivity : AppCompatActivity() {

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_answer)

       /* Handler().postDelayed({
            val intent = Intent(this, EvaluationActivity::class.java);
            startActivity(intent)
        }, 3000)*/
    }
}