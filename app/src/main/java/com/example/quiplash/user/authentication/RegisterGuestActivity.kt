package com.example.quiplash.user.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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
import com.example.quiplash.user.profile.ProfileActivity
import com.example.quiplash.user.UserQP

class RegisterGuestActivity : AppCompatActivity() {

    //view objects
    private lateinit var progressBar: ProgressBar
    private lateinit var inputEmail : EditText
    private lateinit var inputUsername : EditText
    private lateinit var inputPassword : EditText
    private lateinit var inputPassword2 : EditText

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

        val textviewError = findViewById<TextView>(R.id.textError)
        progressBar = findViewById(R.id.progressBarGuestSignup)


        btnBack.setOnClickListener {
            startActivity(Intent(this@RegisterGuestActivity, ProfileActivity::class.java))
            finish()
        }


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
                    //else if guest was already logged out (the anonymous-user doesn't exist anymore on Firebase-Auth) -> we have to create a new User in Firebase
                    //Remove Guest-data from DB

                    auth.createUserWithEmailAndPassword(
                        inputEmail.text.toString(),
                        inputPassword.text.toString()
                    )
                        .addOnCompleteListener(this) { task ->
                            progressBar.visibility = View.INVISIBLE

                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                createUser()

                            } else {

                                Toast.makeText(
                                    this@RegisterGuestActivity,
                                    "Authentication failed." + task.result,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
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
        return (!TextUtils.isEmpty(inputEmail.text) && !TextUtils.isEmpty(inputPassword2.text) &&
                !TextUtils.isEmpty(inputPassword.text) && (inputPassword2.text.toString() == inputPassword.text.toString()) && inputPassword.text.length >= 6)
    }


    private fun createUser() {

        //get user-object
        val callbackUser = object :
            Callback<UserQP> {
            override fun onTaskComplete(result: UserQP) {
                guestid = result.userID
                result.userID = auth.currentUser?.uid.toString()
                result.guest = false
                //save user in game-manager (for easy access in further dev)
                setUserinfo(result)

                //save user (name, score,...) in database
                db.document(result.userID)
                    .set(result)
                    .addOnSuccessListener {
                        Log.d("SUCCESS", "DocumentSnapshot successfully written!")
                        DBMethods.deleteUser(guestid)
                        removeLocalGuestInfo()

                        startActivity(Intent(this@RegisterGuestActivity, ProfileActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e -> Log.w("ERROR", "Error writing document", e) }
            }
        }
        DBMethods.getUserByName(callbackUser, inputUsername.text.toString())




    }




    private fun removeLocalGuestInfo(){
        val editor = sharedPreference?.edit()
        editor?.clear()
        editor?.apply()
    }



}