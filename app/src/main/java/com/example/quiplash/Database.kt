package com.example.quiplash

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.DBMethods.DBCalls.Companion.allUsers
import com.example.quiplash.DBMethods.DBCalls.Companion.editUser
import com.example.quiplash.DBMethods.DBCalls.Companion.getQuestions
import com.example.quiplash.DBMethods.DBCalls.Companion.getUsers
import com.example.quiplash.DBMethods.DBCalls.Companion.saveQuestion
import com.example.quiplash.DBMethods.DBCalls.Companion.saveUser

class Database : AppCompatActivity() {


    //Add Question
    lateinit var saveButton: Button
    lateinit var show_users: Button
    lateinit var question_text: EditText
    lateinit var question_type: EditText
    lateinit var show_users2: EditText
    lateinit var test: ArrayList<User>

    //Add User
    lateinit var saveButtonUser: Button
    lateinit var user_name: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)


        //Questions DB
        saveButton = findViewById(R.id.save)
        question_text = findViewById(R.id.question_text)
        question_type = findViewById(R.id.question_id)

        //Users DB
        saveButtonUser = findViewById(R.id.save_user)
        user_name = findViewById(R.id.user_name)
        show_users = findViewById(R.id.show_users)
        show_users2 = findViewById(R.id.plain_text_input)

        saveButton.setOnClickListener {
            insert_question_into_db()
        }
        saveButtonUser.setOnClickListener {
            insert_user_into_db()
        }

        show_users.setOnClickListener {


            val user = User("test", "Juli<3<3>", false, 0)
            editUser("sSl61HRtniQLZI92Ifj2", user)

            Toast.makeText(this, test.first().userName, Toast.LENGTH_LONG).show()
        }


        val callback = object: Callback<ArrayList<User>> {
            override fun onTaskComplete(result: ArrayList<User>) {
                test = result
            }
        }
        getUsers(callback)
    }



    private fun insert_question_into_db(){
        val question = question_text.text.toString().trim()
        val type = question_type.text.toString().trim()

        if (question != "" && type != "") {
            saveQuestion(question, type)
        } else {
            Toast.makeText(this, "Füll den Spass aus!", Toast.LENGTH_LONG).show()
        }
    }

    private fun insert_user_into_db(){
        val user_name = user_name.text.toString().trim()
        val guest = true
        val score = 0

        if (user_name != "") {
            saveUser(user_name, guest, score)
        } else {
            Toast.makeText(this, "Füll den Spass aus!", Toast.LENGTH_LONG).show()
        }
    }
}