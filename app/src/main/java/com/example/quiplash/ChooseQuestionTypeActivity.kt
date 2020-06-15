package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.DBMethods.DBCalls.Companion.newQuestionType

class ChooseQuestionTypeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_question_type)

        val btnStandard = findViewById<Button>(R.id.btnStandard)
        val btnFunny = findViewById<Button>(R.id.btnFunny)
        val btnPoetic = findViewById<Button>(R.id.btnPoetic)
        val btnHarsh = findViewById<Button>(R.id.btnHarsh)

        btnStandard.setOnClickListener {
            newQuestionType = 1
            val intent = Intent(this, AddQuestion::class.java);
            startActivity(intent);
        }

        btnFunny.setOnClickListener {
            newQuestionType = 2
            val intent = Intent(this, AddQuestion::class.java);
            startActivity(intent);
        }

        btnPoetic.setOnClickListener {
            newQuestionType = 3
            val intent = Intent(this, AddQuestion::class.java);
            startActivity(intent);
        }

        btnHarsh.setOnClickListener {
            newQuestionType = 4
            val intent = Intent(this, AddQuestion::class.java);
            startActivity(intent);
        }



    }
}