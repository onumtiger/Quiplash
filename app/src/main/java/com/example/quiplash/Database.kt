package com.example.quiplash

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Database : AppCompatActivity() {

    lateinit var _db: DatabaseReference

    //Add Question
    lateinit var saveButton: Button
    lateinit var question_text: EditText
    lateinit var question_type: EditText

    //Add User
    lateinit var saveButtonUser: Button
    lateinit var user_name: EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)

        //Firebase DB
        _db = FirebaseDatabase.getInstance().reference

        //Questions DB
        saveButton = findViewById(R.id.save)
        question_text = findViewById(R.id.question_text)
        question_type = findViewById(R.id.question_id)

        //Users DB
        saveButtonUser = findViewById(R.id.save_user)
        user_name = findViewById(R.id.user_name)


        saveButton.setOnClickListener {
            saveQuestion()
        }

        saveButtonUser.setOnClickListener {
            saveUser()
        }


    }


    private fun saveQuestion() {
        val question = question_text.text.toString().trim()
        if (question.isEmpty()){
            question_text.error = "Desch Feld isch leer!"
            return
        }
        val ref = FirebaseDatabase.getInstance().getReference("questions")
        val questionId = ref.push().key
        val ques = Question(questionId, question, "0")

        if (questionId != null) {
            ref.child(questionId).setValue(ques).addOnCompleteListener {
                Toast.makeText(applicationContext, "Question saved successfully", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun saveUser() {
        val user = user_name.text.toString().trim()
        if (user.isEmpty()){
            user_name.error = "Desch Feld isch leer!"
            return
        }
        val ref = FirebaseDatabase.getInstance().getReference("users")
        val userId = ref.push().key
        val usr = User(userId, user, false, 0)

        if (userId != null) {
            ref.child(userId).setValue(user).addOnCompleteListener {
                Toast.makeText(applicationContext, "User saved successfully", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun editUser(){
    }

    private fun deleteUser(){
    }

    private fun editQuestion(){
    }

    private fun deleteQuestion(){
    }
}