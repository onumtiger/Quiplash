package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.quiplash.DBMethods.DBCalls.Companion.setGame
import com.example.quiplash.GameManager.Companion.game
import com.google.firebase.auth.FirebaseAuth


class New_GameActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_new_game)
        auth = FirebaseAuth.getInstance()

        val btnBack = findViewById<AppCompatImageButton>(R.id.new_game_go_back_arrow)
        val switchGamePrivate = findViewById<Switch>(R.id.switchPrivateGame)
        val btnStart = findViewById<Button>(R.id.start_game)
        val categorySpinner: Spinner = findViewById(R.id.category_dropdown)
        val playerSpinner: Spinner = findViewById(R.id.player_dropdown)
        val roundsSpinner: Spinner = findViewById(R.id.rounds_dropdown)
        var check = 0
        var isPublic = true

        categorySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                if(++check > 3) {
                    Log.d("check cat", check.toString())
                    Sounds.playClickSound(this@New_GameActivity)
                }
            }
        }

        playerSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                if(++check > 3) {
                    Log.d("check play", check.toString())
                    Sounds.playClickSound(this@New_GameActivity)
                }
            }
        }

        roundsSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                if(++check > 3) {
                    Log.d("check round", check.toString())
                    Sounds.playClickSound(this@New_GameActivity)
                }
            }
        }

        btnBack.setOnClickListener {
            Sounds.playClickSound(this)

            super.onBackPressed()
        }

        switchGamePrivate.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isPublic = false
            } else {
                isPublic = true
            }
        }

        btnStart.setOnClickListener {
            Sounds.playClickSound(this)

            val intent = Intent(this, HostWaitingActivity::class.java)
            intent.putExtra("gameID", createNewGame(isPublic))
            startActivity(intent)
        }
    }

    fun createNewGame(isPublic: Boolean): String {
        val categorySpinner: Spinner = findViewById(R.id.category_dropdown)
        val playerSpinner: Spinner = findViewById(R.id.player_dropdown)
        val roundsSpinner: Spinner = findViewById(R.id.rounds_dropdown)
        val gameTitleEdit = findViewById<EditText>(R.id.game_title)

        val category = categorySpinner.selectedItem.toString()
        val playerNumbersSpinner = playerSpinner.selectedItem.toString()
        val playerNumbers = playerNumbersSpinner.substringBefore(' ').toInt()
        val roundSpinner = roundsSpinner.selectedItem.toString()
        val rounds = roundSpinner.substringBefore(' ').toInt()
        val activeRound = 1
        val users: ArrayList<String> = arrayListOf(auth.currentUser?.uid.toString())
        val gameID = ""
        val gameTitle = gameTitleEdit.text.toString()

        val newGame = Game(activeRound, category, playerNumbers, rounds, users, gameID, auth.currentUser?.uid.toString(), isPublic, gameTitle, arrayListOf<Question>(), arrayListOf<String>())
        game = newGame
        return setGame(newGame)
    }
}
