package com.example.quiplash.user.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.example.quiplash.game.GameManager.Companion.setUserinfo
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.example.quiplash.user.profile.ProfileActivity
import com.example.quiplash.user.UserQP

class RegisterGuestActivity : AppCompatActivity() {

    //view objects
    private lateinit var progressBar: ProgressBar
    private lateinit var inputEmail : EditText
    private lateinit var inputUsername : EditText
    private lateinit var inputPassword : EditText
    private lateinit var inputPassword2 : EditText
    private lateinit var textviewError : TextView

    private lateinit var guestid : String

    //FirebaseAuth object
    private lateinit var auth: FirebaseAuth
    private var errotext: String = ""

    //Firestore
    lateinit var db: CollectionReference
    private val dbUsersPath = DBMethods.usersPath

    //Local-Storage
    private val prefName = "Quiplash"
    private var privateMode = 0
    private var sharedPreference: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_register_guest)

        //Get Firebase auth instance
        FirebaseApp.initializeApp(this)

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance()
        sharedPreference =  getSharedPreferences(prefName, privateMode)

        db = FirebaseFirestore.getInstance().collection(dbUsersPath)

        val btnSignUp = findViewById<Button>(R.id.btnSignupGuest)
        val btnBack = findViewById<Button>(R.id.buttonRegisterGuestBack)

        inputUsername = findViewById(R.id.usernameFieldGuest)
        inputEmail = findViewById(R.id.emailFieldGuest)
        inputPassword = findViewById(R.id.passwordFieldGuest)
        inputPassword2 = findViewById(R.id.passwordRetypeFieldGuest)

        textviewError = findViewById(R.id.textErrorGuest)
        progressBar = findViewById(R.id.progressBarGuestSignup)

        inputPassword2.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                signup()
                return@OnKeyListener true
            }
            false
        })

        btnBack.setOnClickListener {
            startActivity(Intent(this@RegisterGuestActivity, ProfileActivity::class.java))
            finish()
        }


        btnSignUp.setOnClickListener {
            Sounds.playClickSound(this)
            textviewError.visibility = TextView.GONE
            progressBar.visibility = View.INVISIBLE
            signup()
        }
    }

    private fun checkInput(): Boolean {
        return (!TextUtils.isEmpty(inputEmail.text) && !TextUtils.isEmpty(inputPassword2.text) &&
                !TextUtils.isEmpty(inputPassword.text) && (inputPassword2.text.toString() == inputPassword.text.toString()) && inputPassword.text.length >= 6)
    }


    private fun signup(){
        if (checkInput()) {
            progressBar.visibility = View.VISIBLE

            //get user-object
            val callbackUser = object :
                Callback<UserQP> {
                override fun onTaskComplete(result: UserQP) {
                    Log.d("SPIELER","Task complete")
                    Log.d("SPIELER", result.toString())

                    //get user-object
                    if(result.userID == ""){
                        Log.d("SPIELER", "kein user vorhanden")
                        textviewError.text = getString(R.string.username_not_exist)
                        textviewError.visibility = TextView.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                    }else {
                        guestid = result.userID
                        result.userID = auth.currentUser?.uid.toString()
                        result.guest = false

                        //save user in game-manager (for easy access in further dev)
                        setUserinfo(result)

                        DBMethods.deleteUser(guestid)
                        removeLocalGuestInfo()
                        //save user (name, score,...) in database
                        db.document(result.userID)
                            .set(result)
                            .addOnSuccessListener {
                                Log.d("SUCCESS", "DocumentSnapshot successfully written!")

                                //If guest is a logged in Annonymous User...
                                if (auth.currentUser!!.isAnonymous) {
                                    val credential = EmailAuthProvider.getCredential(
                                        inputEmail.text.toString(),
                                        inputPassword.text.toString()
                                    )

                                    auth.currentUser!!.linkWithCredential(credential)
                                        .addOnCompleteListener(this@RegisterGuestActivity) { task ->
                                            if (task.isSuccessful) {
                                                Log.d("SUCCESS", "linkWithCredential:success")
                                                startActivity(
                                                    Intent(
                                                        this@RegisterGuestActivity,
                                                        ProfileActivity::class.java
                                                    )
                                                )
                                                finish()
                                                //createUser()
                                            } else {
                                                Log.w("ERROR", "linkWithCredential:failure", task.exception)
                                                Toast.makeText(
                                                    baseContext, "Authentication failed.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        }
                                } else {
                                    //else if guest was already logged out (the anonymous-user doesn't exist anymore on Firebase-Auth) -> we have to create a new User in Firebase
                                    //Remove Guest-data from DB

                                    auth.createUserWithEmailAndPassword(
                                        inputEmail.text.toString(),
                                        inputPassword.text.toString()
                                    )
                                        .addOnCompleteListener(this@RegisterGuestActivity) { task ->
                                            progressBar.visibility = View.INVISIBLE

                                            if (task.isSuccessful) {
                                                // Sign in success, update UI with the signed-in user's information
                                                //createUser()
                                                startActivity(
                                                    Intent(
                                                        this@RegisterGuestActivity,
                                                        ProfileActivity::class.java
                                                    )
                                                )
                                                finish()
                                            } else {

                                                Toast.makeText(
                                                    this@RegisterGuestActivity,
                                                    "Authentication failed." + task.result,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        }
                                }

                            }
                            .addOnFailureListener { e -> Log.w("ERROR", "Error writing document", e) }

                    }
                }
            }
            DBMethods.getUserByName(callbackUser, inputUsername.text.toString())


        } else {

            errotext = ""
            if (TextUtils.isEmpty(inputEmail.text)) {
                errotext += "Enter email address! \n"
            }

            if (TextUtils.isEmpty(inputPassword.text)) {
                errotext += "Enter password! \n"
            }

            if (inputPassword.text.toString() != inputPassword2.text.toString()) {
                errotext += "Passwords need to be identical! \n"
            }

            if (inputPassword.text.length < 6) {
                errotext += "Password too short, enter minimum 6 characters! \n"
            }

            textviewError.text = errotext
            textviewError.visibility = TextView.VISIBLE

        }
    }


    private fun removeLocalGuestInfo(){
        val editor = sharedPreference?.edit()
        editor?.clear()
        editor?.apply()
    }



}