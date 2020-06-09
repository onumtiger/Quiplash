package com.example.quiplash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*
import com.example.quiplash.GameManager.Companion.setUserinfo
import com.example.quiplash.GameManager.Companion.getUserInfo

class Profile_UnregisteredActivity : AppCompatActivity() {

    //view objects
    private lateinit var progressBar: ProgressBar

    //FirebaseAuth object
    private lateinit var auth: FirebaseAuth
    private var errotext: String = ""

    //Firestore
    lateinit var db: CollectionReference
    private val dbUsersPath = "users"

    //Local-Storage
    private val PREF_NAME = "Quiplash"
    private var PRIVATE_MODE = 0
    var sharedPreference: SharedPreferences? = null
    val prefKey = "guestid"
    val prefDefValue = "noguest"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile_unregistered)

        //Get Firebase auth instance
        FirebaseApp.initializeApp(this)

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance()
        sharedPreference =  getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        db = FirebaseFirestore.getInstance().collection(dbUsersPath)

        val btnSignUp = findViewById<Button>(R.id.btnSignupGuest)
        val inputUsername: EditText = findViewById(R.id.usernameFieldGuest)
        val inputEmail = findViewById<EditText>(R.id.emailFieldGuest)
        val inputPassword = findViewById<EditText>(R.id.passwordFieldGuest)
        val inputPassword2 = findViewById<EditText>(R.id.passwordRetypeFieldGuest)
        val textviewError = findViewById<TextView>(R.id.textError)
        progressBar = findViewById(R.id.progressBarGuestSignup)

        btnSignUp.setOnClickListener {
            if (checkInput()) {
                progressBar.visibility = View.VISIBLE

                //If guest is a logged in Annonymous User...
                if(auth.currentUser!!.isAnonymous){
                    val credential = EmailAuthProvider.getCredential(inputEmail.text.toString(), inputPassword.text.toString())

                    auth.currentUser!!.linkWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Log.d("SUCCESS", "linkWithCredential:success")
                                createUser()
                            } else {
                                Log.w("ERROR", "linkWithCredential:failure", task.exception)
                                Toast.makeText(baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            }

                        }
                } else {
                    //else if guest was already logged out (the anonymous-user doesn't exist anymore on Firebase) -> we have to create a new User in Firebase
                    //Remove Guest-data from DB
                    db.document(sharedPreference?.getString(prefKey,prefDefValue).toString()).delete()
                        .addOnSuccessListener { Log.d("SUCCESS", "DocumentSnapshot successfully deleted!")
                            val editor = sharedPreference?.edit()
                            editor?.clear()
                            editor?.apply()
                            }
                        .addOnFailureListener { e -> Log.w("ERROR", "Error deleting document", e) }

                    auth.createUserWithEmailAndPassword(
                        inputEmail.text.toString(),
                        inputPassword.text.toString()
                    )
                        .addOnCompleteListener(this,
                            OnCompleteListener<AuthResult?> { task ->
                                progressBar.visibility = View.INVISIBLE

                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    createUser()

                                } else {

                                    Toast.makeText(
                                        this@Profile_UnregisteredActivity,
                                        "Authentication failed." + task.result,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                // ...
                            })
                }
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

            }
        }
    }

    private fun checkInput(): Boolean {
        return (!TextUtils.isEmpty(emailFieldSU.text) && !TextUtils.isEmpty(passwordRetypeFieldSU.text) &&
                !TextUtils.isEmpty(passwordFieldSU.text) && (passwordRetypeFieldSU.text.toString() == passwordFieldSU.text.toString()) && passwordFieldSU.text.length >= 6)
    }


    private fun createUser() {

        //create user-object
        val user = UserQP(auth.currentUser?.uid, getUserInfo().userName, false, getUserInfo().score)

        //save user in game-manager (for easy access in further dev)
        setUserinfo(user)

        //save user (name, score,...) in database
        db.document(auth.currentUser?.uid.toString())
            .set(user)
            .addOnSuccessListener {
                Log.d("SUCCESS", "DocumentSnapshot successfully written!")
                startActivity(Intent(this@Profile_UnregisteredActivity, LandingActivity::class.java))
                finish()
            }
            .addOnFailureListener { e -> Log.w("ERROR", "Error writing document", e) }

    }

}