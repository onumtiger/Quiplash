package com.example.quiplash.database

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.database.DBMethods.Companion.editUser
import com.example.quiplash.database.DBMethods.Companion.getUsers
import com.example.quiplash.database.DBMethods.Companion.saveQuestion
import com.example.quiplash.database.DBMethods.Companion.saveUser
import com.example.quiplash.R
import com.example.quiplash.user.UserQP

class Database : AppCompatActivity() {


    //Add Question
    lateinit var saveButton: Button
    lateinit var show_users: Button
    lateinit var question_text: EditText
    lateinit var question_type: EditText
    lateinit var show_users2: EditText
    lateinit var test: ArrayList<UserQP>

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
        //show_users2 = findViewById(R.id.plain_text_input)

        saveButton.setOnClickListener {
            insert_question_into_db()
        }
        saveButtonUser.setOnClickListener {
            insert_user_into_db()
        }

        show_users.setOnClickListener {

        }






        val callback = object:
            Callback<ArrayList<UserQP>> {
            override fun onTaskComplete(result: ArrayList<UserQP>) {
                test = result

                editUser("1ZqX1o543dZzMW4fCJL3pVvloZ83", test.first())
                editUser("1ZqX1o543dZzMW4fCJL3pVvloZ83", test.last())
                editUser("1ZqX1o543dZzMW4fCJL3pVvloZ83", test.get(2))

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
        val photo = "images/default-user.png"
        val friends = emptyList<String>()

        if (user_name != "") {
            saveUser(user_name, guest, score, photo, friends)
        } else {
            Toast.makeText(this, "Füll den Spass aus!", Toast.LENGTH_LONG).show()
        }
    }
}