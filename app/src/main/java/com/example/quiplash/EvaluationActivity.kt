package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class EvaluationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation)

        Handler().postDelayed({
            val intent = Intent(this, End_Of_GameActivity::class.java);
            startActivity(intent)
        }, 3000)
    }
}