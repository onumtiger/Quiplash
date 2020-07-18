package com.example.quiplash.database

import android.content.ContentValues.TAG
import android.util.Log
import com.example.quiplash.game.Game
import com.example.quiplash.game.Question
import com.example.quiplash.user.UserQP
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

        companion object {

            val db = FirebaseFirestore.getInstance()
            lateinit var res: QuerySnapshot
            var GameQuestions = ArrayList<Question>()
            var actual = false
            private var auth: FirebaseAuth? = FirebaseAuth.getInstance()

            const val usersPath = "users"
            private const val invitationsPath = "invitations"
            private const val friendsPath = "friends"
            const val gamesPath = "games"
            private const val questionsPath = "questions"
            private const val usernamePath = "userName"
            const val playroundsPath = "playrounds"
            const val drinksPath = "drinks"
            const val defaultUserImg = "images/default_user_QP.png"
            const val defaultGuestImg = "images/default_guest_QP.png"

            fun saveQuestion(question_text: String, question_type: String){
                val ID = createID()
                    .toString()
                val attributes = HashMap<String, Any>()
                attributes.put("text", question_text)
                attributes.put("ID", ID)
                attributes.put("Type", question_type)


                val qustn = Question(
                    ID,
                    question_text,
                    question_type
                )

                db.collection(
                    questionsPath
                ).document().set(qustn).addOnSuccessListener {
                    //Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    //exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
            }

            //get current user from DB
            fun getUser(callback: Callback<UserQP>) {
                db.collection(
                    usersPath
                ).document(auth?.currentUser?.uid.toString())
                    .get()
                    .addOnSuccessListener { useritem ->
                        callback.onTaskComplete(useritem.toObject(UserQP::class.java)!!)
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
            }

            //add token to user
            fun addToken(user_t: UserQP){
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
                        // Get new Instance ID token
                        val tokenNew = task.result?.token
                        FirebaseInstanceId.getInstance().instanceId
                        user_t.token = tokenNew.toString()
                        db.collection(
                            usersPath
                        ).document(user_t.userID)
                            .update("token", tokenNew)
                            .addOnSuccessListener { Log.d("SUCCESS", "Token successfully updated!") }
                            .addOnFailureListener { e -> Log.w("FAILURE", "Error updating document", e) }
                    })
            }

            //get all users from DB
            fun getUsers(callback: Callback<ArrayList<UserQP>>) {
                val allUsers = ArrayList<UserQP>()
                db.collection(
                    usersPath
                )
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            val user = document.toObject(UserQP::class.java)
                            allUsers.add(user)
                            println(allUsers.size)
                        }
                        callback.onTaskComplete(allUsers)
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting users: ", exception)
                    }
            }

            //get all questions from DB
            fun getQuestions(callback: Callback<ArrayList<Question>>) {
                val allQuestions = ArrayList<Question>()
                db.collection(
                    questionsPath
                )
                    //.whereEqualTo("capital", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            val question = document.toObject(Question::class.java)
                            allQuestions.add(question)
                            println(allQuestions.size)
                        }
                        callback.onTaskComplete(allQuestions)
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting users: ", exception)
                    }
            }

            //edit User (found by ID) in DB
            fun editUser(ID :String, user : UserQP) {
                db.collection(
                    usersPath
                ).document(ID).set(user).addOnSuccessListener {
                    //Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    //exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
            }

            fun editGame(ID :String, game : Game) {
                db.collection(
                    gamesPath
                ).document(ID).set(game).addOnSuccessListener {
                    //Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    //exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
            // game.playrounds.getValue("round${game.activeRound}").question

            //edit User friends (found by ID) in DB
            fun editUserFriends(userid: String, friends: List<String>) {
                    db.collection(
                        usersPath
                    ).document(userid).update(friendsPath, friends)
                    .addOnSuccessListener { Log.d(TAG, "Friendslist successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating friendslist", e) }
            }

            //delete User from DB
            fun deleteUser(userid: String) {
                db.collection(
                    usersPath
                ).document(userid).delete()
                    .addOnSuccessListener {
                        Log.d("SUCCESS", "DocumentSnapshot successfully deleted!")
                    }
                    .addOnFailureListener { e -> Log.w("ERROR", "Error deleting document", e) }
            }

            //Update image of User in DB
            fun updateUserImage(userid :String, imagepath :String, callback: Callback<Boolean>) {
                db.collection(
                    usersPath
                ).document(userid).update("photo", imagepath)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                        callback.onTaskComplete(true)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                        callback.onTaskComplete(false)
                    }
            }

            //set net Game to DB
            fun setGame(game: Game): String {
                val ref = db.collection(
                    gamesPath
                ).document()
                game.gameID = ref.id
                ref.set(game)

                return game.gameID
            }

            //Updtae users of current game in DB
            fun updateGameUsers(game: Game) {
                val gameID = game.gameID
                val ref = db.collection(
                    gamesPath
                ).document(gameID)
                ref.update(usersPath, game.users)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
            }

            //update invitations of game in DB
            fun updateInvitations(game: Game) {
                val gameID = game.gameID
                val ref = db.collection(
                    gamesPath
                ).document(gameID)
                ref.update(invitationsPath, game.invitations)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
            }

            //Update scores of users in game in DB
            fun updateUserScores(userID: String, gameScore: Int) {
                val callbackUser = object :
                    Callback<UserQP> {
                    override fun onTaskComplete(result: UserQP) {
                        val user = result
                        val newScore = user.score + gameScore

                        val ref = db.collection(
                            usersPath
                        ).document(userID)
                        ref.update("score", newScore)
                            .addOnSuccessListener {
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot successfully updated!"
                                )
                            }
                            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                    }
                }
                getUserWithID(
                    callbackUser,
                    userID
                )
            }

            //get active games from DB
            fun getActiveGames(callback: Callback<MutableList<Game>>, gameList: MutableList<Game>) {
                val docRef = db.collection(
                    gamesPath
                )
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

            //get the current game from DB
            fun getCurrentGame(callback: Callback<Game>, gameID: String) {
                var currentGame = Game()
                var playersList: MutableList<String>
                val docRef = db.collection(
                    gamesPath
                ).document(gameID)
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

            //get User by ID from DB
            fun getUserWithID(callback: Callback<UserQP>, userID: String) {
                db.collection(
                    usersPath
                ).document(userID).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val user = document.toObject(UserQP::class.java)!!
                            callback.onTaskComplete(user)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("ERROR", "Error getting documents: ", exception)
                    }
            }

            //get User by name from DB
            fun getUserByName(callback: Callback<UserQP>, username: String) {
                db.collection(
                    usersPath
                ).whereEqualTo(usernamePath, username)
                    .get()
                    .addOnSuccessListener { documents ->
                        if(documents.size() == 0){
                            callback.onTaskComplete(UserQP())
                        }else {
                            for (document in documents) {
                                val user = document.toObject(UserQP::class.java)
                                callback.onTaskComplete(user)
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("ERROR", "Error getting documents: ", exception)
                        callback.onTaskComplete(UserQP())
                    }
            }

            //delete Game from DB
            fun deleteGame(gameID: String, callback: Callback<Boolean>) {
                val docRef = db.collection(
                    gamesPath
                ).document(gameID)
                    docRef.delete()
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!")
                            callback.onTaskComplete(true)}
                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting game", e)
                            callback.onTaskComplete(false)}
            }


            //check Username from DB
            fun checkUsername(curName: String, username: String, callback: Callback<Boolean>) {
                var usernameExists = false
                db.collection(
                    usersPath
                ).get()
                    .addOnSuccessListener { userCollectionDB ->
                        for (userItemDB in userCollectionDB) {
                            val userDB = userItemDB.toObject(UserQP::class.java)

                            if (userDB.userName.toLowerCase(Locale.ROOT) == username.toLowerCase(
                                    Locale.ROOT
                                ) && userDB.userName.toLowerCase(Locale.ROOT) != curName.toLowerCase(Locale.ROOT)
                            ) {
                                usernameExists = true
                            }
                            continue
                        }
                        callback.onTaskComplete(usernameExists)

                    }
                    .addOnFailureListener { exception ->
                        Log.d("ERROR", "" + exception)
                        callback.onTaskComplete(usernameExists)
                    }
            }

            @Throws(Exception::class)
            fun createID(): String? {
                return UUID.randomUUID().toString()
            }
        }


}
