package com.example.quiplash

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.iid.FirebaseInstanceId
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



class DBMethods {

    class DBCalls {
        companion object {

            val db = FirebaseFirestore.getInstance()
            lateinit var res: QuerySnapshot
            var allUsers = ArrayList<UserQP>()
            var singleUser :UserQP = UserQP()
            var allQuestions = ArrayList<Question>()
            var GameQuestions = ArrayList<Question>()
            var allGames = mutableListOf<Game>()
            var actual = false
            private var auth: FirebaseAuth? = FirebaseAuth.getInstance()

            var newQuestionType : Int? = null

            val usersPath = "users"
            val gamesPath = "games"
            val questionsPath = "questions"

            fun saveQuestion(question_text: String, question_type: String){
                val ID = createID().toString()
                val attributes = HashMap<String, Any>()
                attributes.put("text", question_text)
                attributes.put("ID", ID)
                attributes.put("Type", question_type)


                val qustn = Question(ID, question_text, question_type, "")

                db.collection(questionsPath).document().set(qustn).addOnSuccessListener {
                    //Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    //exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
            }

            fun saveUser(user_name: String, guest: Boolean, score: Int, photo: String, friends: List<String>) {
                val ID = createID().toString()
                val attributes = HashMap<String, Any>()
                attributes.put("name", user_name)
                attributes.put("ID", ID)
                attributes.put("guest", guest)
                attributes.put("score", score)
                attributes.put("photo", photo)
                attributes.put("friends", friends)
                val usr = UserQP(ID, user_name, true, score, photo, friends, "")
                db.collection(usersPath).document().set(usr).addOnSuccessListener {
                    //Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    //exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
            }

            fun getUser(callback: Callback<UserQP>) {
                val user1 = auth?.currentUser
                db.collection(usersPath)
                        .whereEqualTo("userID", user1?.uid)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                val documents2 = documents
                                documents2.forEach{
                                    val user = it.toObject(UserQP::class.java)
                                    singleUser = user
                                }
                            }
                            callback.onTaskComplete(singleUser)
                        }
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                        }
            }

            fun addToken(user_t: UserQP){
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
                        // Get new Instance ID token
                        val token_new = task.result?.token
                        FirebaseInstanceId.getInstance().getInstanceId()
                        user_t.token = token_new.toString()
                        editUser(user_t.userID, user_t)
                    })
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

            fun getUsers(callback: Callback<ArrayList<UserQP>>) {
                db.collection(usersPath)
                    //.whereEqualTo("capital", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                            val user = document.toObject(UserQP::class.java)
                            allUsers.add(user)
                            println(allUsers.size)
                        }
                        callback.onTaskComplete(allUsers)
                    }
                    .addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                    }
            }


            //returns arraylist with all Questions
            fun getQuestions(callback: Callback<ArrayList<Question>>) {
                db.collection(questionsPath)
                    //.whereEqualTo("capital", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                            val documents2 = documents
                            documents2.forEach{
                                val question = it.toObject(Question::class.java)

                                    allQuestions.add(question)
                                    //Log.w(TAG,user.userID.toString() , e)

                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                    }
            }

            //to get a random question
            fun getRandomQuestion(): Question {
                if (!actual || GameQuestions.size > 0){
                    val callback = object: Callback<ArrayList<Question>> {
                        override fun onTaskComplete(result: ArrayList<Question>) {
                            GameQuestions = result
                            actual = true
                        }
                    }
                    getQuestions(callback)
                }
                val position = (0..GameQuestions.size-1).random()
                val question = GameQuestions[position]
                GameQuestions.drop(position)
                return question
            }

            fun editUser(ID :String, user :UserQP) {
                db.collection(usersPath).document(ID).set(user).addOnSuccessListener {
                    //Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    //exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
            }

            fun deleteUser(){
            }

            fun editQuestion(){
            }

            fun deleteQuestion(ID :String, question :Question){
                db.collection(questionsPath).document(ID).set(question).addOnSuccessListener {
                    //Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    //exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
            }

            fun setGame(game: Game): String {
                val ref = db.collection(gamesPath).document()
                game.gameID = ref.id
                ref.set(game)

                return game.gameID
            }


            fun updateGameUsers(game: Game) {
                val gameID = game.gameID
                val ref = db.collection(gamesPath).document(gameID)
                ref.update(usersPath, game.users)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
            }

            fun getActiveGames(callback: Callback<MutableList<Game>>, gameList: MutableList<Game>) {
                val docRef = db.collection(gamesPath)
                docRef.get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            Log.d("TAG", "${document.id} => ${document.data}")
                            val activeGame = document.toObject(Game::class.java)
                            if (activeGame.playerNumber != activeGame.users.size) {
                                gameList.add(activeGame)
                            }
                        }
                        callback.onTaskComplete(gameList)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "Error getting documents: ", exception)
                    }
            }

            fun getCurrentGame(callback: Callback<Game>, gameID: String) {
                var currentGame: Game = Game()
                var playersList = mutableListOf<String>()
                val docRef = db.collection(gamesPath).document(gameID)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d("TAG", "${document.id} => ${document.data}")
                            val game = document.toObject (Game::class.java)
                            if (game != null) {
                                currentGame = game
                                val players = game.users
                                    playersList = players.toMutableList()
                                    Log.d("playersListSize", "${playersList.size}")

                            }
                            callback.onTaskComplete(currentGame)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "Error getting documents: ", exception)

                    }
            }

            fun getUserWithID(callback: Callback<UserQP>, userID: String) {
               println(userID)
                val docRef = db.collection(usersPath).document(userID)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d("TAG", "${document.id} => ${document.data}")
                            val user = document.toObject(UserQP::class.java)!!
                            callback.onTaskComplete(user)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "Error getting documents: ", exception)}
            }

            fun deleteGame(gameID: String, callback: Callback<Boolean>) {
                val docRef = db.collection(gamesPath).document(gameID)
                    docRef.delete()
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!")
                            callback.onTaskComplete(true)}
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e)
                            callback.onTaskComplete(false)}
            }

            fun removeUserFromGame(gameID: String, userID: String) {
            }

            @Throws(Exception::class)
            fun createID(): String? {
                return UUID.randomUUID().toString()
            }
        }
    }


}
