package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.example.quiplash.GameManager.Companion.game
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PrepareAnswerActivity : AppCompatActivity() {

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = "games"

    //FirebaseAuth object
    private var auth: FirebaseAuth? = null

    var userindex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prepare_answer)
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        auth = FirebaseAuth.getInstance()



        db.document(game.gameID).get()
            .addOnSuccessListener { documentSnapshot ->
                game = documentSnapshot.toObject(Game::class.java)!!

                userindex = game.playrounds[game.activeRound-1].opponents.indexOfFirst { it.userID == auth!!.currentUser?.uid }

                /*if(game.playrounds[game.activeRound-1].opponents[0].userID == auth!!.currentUser?.uid.toString()){
                        userindex = 0
                    } else {
                        userindex = 1
                    }*/

            }




        val btnReady = findViewById<Button>(R.id.btnReady)
        val fieldAnswer = findViewById<EditText>(R.id.answerField)

        btnReady.setOnClickListener {
            game.playrounds[game.activeRound-1].opponents[userindex].answer = fieldAnswer.text.toString()
            db.document("PFIoKme1vrCRnGpSsORn")
                .set(game)
                .addOnSuccessListener {
                    Log.d("Success", "DocumentSnapshot successfully written!")
                    val intent = Intent(this, AnswersActivity::class.java);
                    startActivity(intent);
                }
                .addOnFailureListener { e -> Log.w("Error", "Error writing document", e) }

        }
    }
}