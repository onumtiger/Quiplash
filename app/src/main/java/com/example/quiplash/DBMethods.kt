package com.example.quiplash

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap



/*
ALL METHODS:

- saveQuestion(question_text: String, question_type: String) -> save a Question in DB
- saveUser(user_name: String, guest: Boolean, score: Int) -> save a User in DB
- getUsers(callback: Callback<ArrayList<User>>) -> get an Arraylist with all Users, Callback as a parameter (Example for Callback below)
- getQuestions(callback: Callback<ArrayList<User>>) -> -""-
- getRandomQuestion() -> returns a rondom Question, that was asked yet
- deleteUser()
- deleteQuestion()
- editUser(ID :String, question :User) -> Edit a user(found by firestore ID)
- editQuestion(ID :String, question :Question) -> Edit a question(found by firestore ID)


 */

// GetUser



class DBMethods {

    class DBCalls {
        companion object {

            val db = FirebaseFirestore.getInstance()
            lateinit var res: QuerySnapshot
            var _users: MutableLiveData<ArrayList<User>> = MutableLiveData<ArrayList<User>>()
            var allUsers = ArrayList<User>()
            var allQuestions = ArrayList<Question>()
            var GameQuestions = ArrayList<Question>()
            var allGames = mutableListOf<Game>()
            var actual = false



            public fun saveQuestion(question_text: String, question_type: String){
                var ID = createID().toString()
                val attributes = HashMap<String, Any>()
                attributes.put("text", question_text)
                attributes.put("ID", ID)
                attributes.put("Type", question_type)

                var qustn = Question(ID, question_text, question_type)

                db.collection("questions").document().set(qustn).addOnSuccessListener {
                    //Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    //exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
            }

            public fun saveUser(user_name: String, guest: Boolean, score: Int) {
                var ID = createID().toString()
                val attributes = HashMap<String, Any>()
                attributes.put("name", user_name)
                attributes.put("ID", ID)
                attributes.put("guest", guest)
                attributes.put("score", score)
                val usr = User(ID, user_name, true, score)
                db.collection("users").document().set(usr).addOnSuccessListener {
                    //Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    //exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
            }

            //returns arraylist with all users

            /*
            val callback = object: Callback<ArrayList<User>> {
                override fun onTaskComplete(result: ArrayList<User>) {
                youVar = result
                }
            }
            getUsers(callback)
            */

            public fun getUsers(callback: Callback<ArrayList<User>>) {
                db.collection("users")
                    //.whereEqualTo("capital", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                            val documents2 = documents
                            documents2.forEach{
                                val user = it.toObject(User::class.java)
                                if (user != null) {
                                    user.userID = it.id
                                    allUsers.add(user)
                                    //Log.w(TAG,user.userID.toString() , e)
                                }
                            }
                        }
                        callback.onTaskComplete(allUsers)
                    }
                    .addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                    }
            }




            //returns arraylist with all Questions
            public fun getQuestions(callback: Callback<ArrayList<Question>>) {
                db.collection("questions")
                    //.whereEqualTo("capital", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                            val documents2 = documents
                            documents2.forEach{
                                val question = it.toObject(Question::class.java)
                                if (question != null) {
                                    allQuestions.add(question)
                                    //Log.w(TAG,user.userID.toString() , e)
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                    }
            }

            //to get a random question
            public fun getRandomQuestion(): Question {
                if (actual == false || GameQuestions.size > 0){
                    val callback = object: Callback<ArrayList<Question>> {
                        override fun onTaskComplete(result: ArrayList<Question>) {
                            GameQuestions = result
                            actual = true
                        }
                    }
                    getQuestions(callback)
                }
                var position = (0..GameQuestions.size-1).random()
                var question = GameQuestions[position]
                GameQuestions.drop(position)
                return question
            }

            public fun editUser(ID :String, user :User) {
                db.collection("users").document(ID).set(user).addOnSuccessListener {
                    //Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    //exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
            }

            public fun deleteUser(){
            }

            public fun editQuestion(){
            }

            public fun deleteQuestion(ID :String, question :Question){
                db.collection("questions").document(ID).set(question).addOnSuccessListener {
                    //Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    //exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
            }

            public fun setGame(game: Game): String {
                val ref = db.collection("games").document()
                game.gameID = ref.id
                ref.set(game)

                return game.gameID
            }


            public fun updateGameUsers(game: Game) {
                val gameID = game.gameID
                val ref = db.collection("games").document(gameID)
                ref.update("users", game.users)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
            }

            @Throws(Exception::class)
            fun createID(): String? {
                return UUID.randomUUID().toString()
            }
        }
    }


}
