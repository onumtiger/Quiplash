package com.example.quiplash

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class GameMethods {

        companion object {
            lateinit var countdown_timer: CountDownTimer
            var time_in_milli_seconds = 0L
            const val voteScore = 10
            const val roundWinnerScore = 50
            const val opp0 = "opponent0"
            const val opp1 = "opponent1"

            val db = FirebaseFirestore.getInstance().collection(DBMethods.gamesPath)

            fun startTimer(textView :TextView, time_in_seconds :Long, callback: Callback<Boolean>) {
                    countdown_timer = object : CountDownTimer(time_in_seconds * 1000, 1000) {
                        override fun onFinish() {
                            callback.onTaskComplete(true)
                        }
                        override fun onTick(p0: Long) {
                            time_in_milli_seconds = p0
                            updateTextUI(textView)
                        }
                    }
                    countdown_timer.start()
            }

            private fun updateTextUI(textView :TextView) {
                val minute = (time_in_milli_seconds / 1000) / 60
                val seconds = (time_in_milli_seconds / 1000) % 60
                if(seconds <10){
                    textView.text = ("$minute:0$seconds")
                }else {
                    textView.text = ("$minute:$seconds")
                }
            }

            private fun resetTimer(textView :TextView) {
                time_in_milli_seconds = 60000L
                updateTextUI(textView)
            }

            fun pauseTimer() {
                countdown_timer.cancel()
            }

            fun playerAllocation(appcontext: Context, userid: String){
                if(GameManager.game.playrounds.size>= GameManager.game.activeRound) {


                    if (GameManager.game.playrounds.getValue("round${GameManager.game.activeRound}").opponents.getValue(opp0).userID.equals(userid) || GameManager.game.playrounds.getValue("round${GameManager.game.activeRound}").opponents.getValue(opp1).userID.equals(userid)
                    ) {
                        val intent = Intent(appcontext, PrepareAnswerActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        appcontext.startActivity(intent)
                    } else {
                        val intent = Intent(appcontext, ChooseAnswerActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        appcontext.startActivity(intent)
                    }
                } else{
                    val intent = Intent(appcontext, LandingActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    appcontext.startActivity(intent)
                }
            }


            fun setPoints(callback: Callback<Boolean>) {
                val callbackGame = object : Callback<Game> {
                    override fun onTaskComplete(result: Game) {
                        GameManager.game = result

                        if (GameManager.game.playrounds.getValue("round${GameManager.game.activeRound}").opponents.getValue(opp0).answerScore > GameManager.game.playrounds.getValue(
                                "round${GameManager.game.activeRound}"
                            ).opponents.getValue(opp1).answerScore
                        ) {
                            GameManager.game.playrounds.getValue("round${GameManager.game.activeRound}").opponents.getValue(opp0).answerScore += roundWinnerScore
                        } else if (GameManager.game.playrounds.getValue("round${GameManager.game.activeRound}").opponents.getValue(
                                opp0
                            ).answerScore < GameManager.game.playrounds.getValue("round${GameManager.game.activeRound}").opponents.getValue(
                                opp1
                            ).answerScore
                        ) {
                            GameManager.game.playrounds.getValue("round${GameManager.game.activeRound}").opponents.getValue(opp1).answerScore += roundWinnerScore
                        }

                        db.document(GameManager.game.gameID)
                            .update(
                                mapOf(
                                    "playrounds.round${GameManager.game.activeRound}.opponents.opponent0.answerScore" to GameManager.game.playrounds.getValue(
                                        "round${GameManager.game.activeRound}"
                                    ).opponents.getValue(opp0).answerScore,
                                    "playrounds.round${GameManager.game.activeRound}.opponents.opponent1.answerScore" to GameManager.game.playrounds.getValue(
                                        "round${GameManager.game.activeRound}"
                                    ).opponents.getValue(opp1).answerScore
                                )
                            )
                            .addOnSuccessListener {
                                callback.onTaskComplete(true)
                            }
                            .addOnFailureListener { e -> Log.w("FAILURE", "Error updating document", e) }
                    }
                }
                DBMethods.getCurrentGame(callbackGame, GameManager.game.gameID)

            }

            fun setProfilePicture(player: UserQP, profileView: ImageView?, appcontext: Context) {
                val playerPhoto: String
                val storageRef = FirebaseStorage.getInstance().reference

                if (player.photo !== null) {
                    playerPhoto = player.photo!!
                } else {
                    playerPhoto = "images/default-guest.png"
                }

                val spaceRef = storageRef.child(playerPhoto)
                spaceRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        if (profileView != null) {
                            Glide
                                .with(appcontext)
                                .load(uri)
                                .into(profileView)
                        }
                    }.addOnFailureListener { Log.d("Test", " Failed!") }
            }
        }
}