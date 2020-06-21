package com.example.quiplash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.GameManager.Companion.setUserinfo
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {
    //view objects
    private lateinit var progressBar: ProgressBar

    //FirebaseAuth object
    private lateinit var auth: FirebaseAuth
    private var errotext: String = ""
    private lateinit var simpleViewFlipper: ViewFlipper

    //Firestore
    lateinit var db: CollectionReference
    private val dbUsersPath = "users"

    //Local-Storage
    private val PREF_NAME = "Quiplash"
    private var PRIVATE_MODE = 0
    var sharedPreference: SharedPreferences? = null
    val prefKey = "guestid"
    val prefDefValue = "noguest"

    //UserInfo
    var isUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference = getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        setContentView(R.layout.activity_sign_up)

        //Get Firebase auth instance
        FirebaseApp.initializeApp(this)

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance().collection(dbUsersPath)

        val btnSignIn = findViewById<Button>(R.id.signinBtn)
        val btnSignUp = findViewById<Button>(R.id.signupBtn)
        val btnSubmit = findViewById<Button>(R.id.submitBtn)
        val btnAnonymous = findViewById<Button>(R.id.anonymousBtn)
        val inputEmail = findViewById<EditText>(R.id.emailFieldSU)
        val inputPassword = findViewById<EditText>(R.id.passwordFieldSU)
        val inputPassword2 = findViewById<EditText>(R.id.passwordRetypeFieldSU)
        val inputUsername = findViewById<EditText>(R.id.editTextUsername)
        val textviewError = findViewById<TextView>(R.id.textError)
        val textviewErrorUserName = findViewById<TextView>(R.id.textErrorUsername)
        progressBar = findViewById(R.id.progressBarSignup)
        simpleViewFlipper = findViewById(R.id.simpleViewFlipper) // get the reference of ViewFlipper


        // Declare in and out animations and load them using AnimationUtils class
        val inAni = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)

        // set the animation type to ViewFlipper
        simpleViewFlipper.inAnimation = inAni
        simpleViewFlipper.outAnimation = out


        //set onClickListener for buttons
        btnSignIn.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
            finish()
        }

        btnSubmit.setOnClickListener {
            /* db.whereEqualTo("userName", inputUsername.text.toString()).get().addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     Log.d("TAG", "Inside onComplete function!");
                     textviewError.text = getString(R.string.username_not_available)
                 } else {
                     createUser()
                 }
             }*/

            var userExist = false
            db.get()
                .addOnSuccessListener { userCollectionDB ->
                    for (userItemDB in userCollectionDB) {
                        val userDB = userItemDB.toObject(UserQP::class.java)

                        if (userDB.userName.toLowerCase() == inputUsername.text.toString()
                                .toLowerCase()
                        ) {
                            textviewErrorUserName.text = getString(R.string.username_not_available)
                            userExist = true
                        }
                        continue
                    }
                    if (userExist) {
                        textviewErrorUserName.text = getString(R.string.username_not_available)
                    } else {
                        createUser()
                    }

                }
                .addOnFailureListener { exception ->
                    Log.d("ERROR", "" + exception)
                    createUser()
                }

        }

        btnSignUp.setOnClickListener {
            if (checkInput()) {
                progressBar.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(
                    inputEmail.text.toString(),
                    inputPassword.text.toString()
                )
                    .addOnCompleteListener(this,
                        OnCompleteListener<AuthResult?> { task ->
                            progressBar.visibility = View.INVISIBLE

                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                isUser = true
                                simpleViewFlipper.showNext()

                            } else {

                                Toast.makeText(
                                    this@SignUpActivity, "Authentication failed." + task.result,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            // ...
                        })
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

        btnAnonymous.setOnClickListener {
            anonymousLogin()
        }


    }

    private fun anonymousLogin() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    isUser = false
                    simpleViewFlipper.showNext()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "Authentication failed." + task.exception,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // ...
            }
    }

    private fun checkInput(): Boolean {
        return (!TextUtils.isEmpty(emailFieldSU.text) && !TextUtils.isEmpty(passwordRetypeFieldSU.text) &&
                !TextUtils.isEmpty(passwordFieldSU.text) && (passwordRetypeFieldSU.text.toString() == passwordFieldSU.text.toString()) && passwordFieldSU.text.length >= 6)
    }


    private fun createUser() {
        //if user is guest...
        if (!isUser) {
            //save id local
            val editor = sharedPreference?.edit()
            editor?.putString(prefKey, auth.currentUser?.uid.toString())
            editor?.apply()
        }

        //create user-object
        val user = UserQP(auth.currentUser?.uid.toString(), editTextUsername.text.toString(), !isUser, 0, "images/default-user.png", emptyList<String>(), "")

        //save user in game-manager (for easy access in further dev)
        setUserinfo(user)


        //save user (name, score,...) in database
        db.document(auth.currentUser?.uid.toString())
            .set(user)
            .addOnSuccessListener {
                startActivity(Intent(this@SignUpActivity, LandingActivity::class.java))
                finish()
            }
            .addOnFailureListener { e -> Log.w("ERROR", "Error writing document", e) }

    }

}
