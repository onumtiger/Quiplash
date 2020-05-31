package com.example.quiplash

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

class Database : AppCompatActivity() {

    //Firestore DB
    lateinit var db: DocumentReference
    //lateinit var db2: FirebaseFirestore
    lateinit var db2: DocumentReference

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

        //Firebase DB (Firestore)
        db = FirebaseFirestore.getInstance().document("quiplash/questions")
        db2 = FirebaseFirestore.getInstance().document("quiplash/users")
        val db2 = FirebaseFirestore.getInstance()

        //Questions DB
        saveButton = findViewById(R.id.save)
        question_text = findViewById(R.id.question_text)
        question_type = findViewById(R.id.question_id)

        //Users DB
        saveButtonUser = findViewById(R.id.save_user)
        user_name = findViewById(R.id.user_name)

        saveButton.setOnClickListener {
            insert_question_into_db()
        }
        saveButtonUser.setOnClickListener {
            insert_user_into_db()
        }

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

    public fun saveQuestion(question_text: String, question_type: String){
        var ID = createID().toString()
        val attributes = HashMap<String, Any>()
        attributes.put("text", question_text)
        attributes.put("ID", ID)
        attributes.put("Type", question_type)


        Toast.makeText(this, question_type, Toast.LENGTH_LONG).show()
        db.collection(question_text).document("questionattributes").set(attributes).addOnSuccessListener {
                void: Void? -> Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
                exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
        }
    }

    public fun saveUser(user_name: String, guest: Boolean, score: Int) {
        var ID = createID().toString()
        val attributes = HashMap<String, Any>()
        attributes.put("name", user_name)
        attributes.put("ID", ID)
        attributes.put("guest", guest)
        attributes.put("score", score)

        val uf = User(ID, user_name, true, score)

        //db.collection("users").add(user).addOnSuccessListener { documentReference ->Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")}
        db2.collection(ID).document("userattributes").set(attributes).addOnSuccessListener { void: Void? -> Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show() }.addOnFailureListener { exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show() }
        //db2.collection("users").document(ID).set(uf)

        //db2.collection(user_name).add(attributes).addOnSuccessListener { documentReference -> Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()}


    }
    @Throws(Exception::class)
    fun createID(): String? {
        return UUID.randomUUID().toString()
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