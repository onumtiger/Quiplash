package com.example.quiplash.user.authentication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.*
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.game.GameManager.Companion.setUserinfo
import com.example.quiplash.user.UserQP
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
    private val dbUsersPath = DBMethods.usersPath

    //Local-Storage
    private val PREFNAME = "Quiplash"
    private var PRIVATEMODE = 0
    var sharedPreference: SharedPreferences? = null
    private val prefKey = "guestid"

    //UserInfo
    var isUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference = getSharedPreferences(PREFNAME, PRIVATEMODE)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
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
            val callbackCheckUsername = object:
                Callback<Boolean> {
                override fun onTaskComplete(result: Boolean) {
                    if (result) {
                        textviewErrorUserName.text = getString(R.string.username_not_available)
                    } else {
                        createUser()
                    }
                }
            }
            DBMethods.checkUsername(
                "",
                inputUsername.text.toString(),
                callbackCheckUsername
            )


        }

        btnSignUp.setOnClickListener {
            if (checkInput()) {
                progressBar.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(
                    inputEmail.text.toString(),
                    inputPassword.text.toString()
                )
                    .addOnCompleteListener(this
                    ) { task ->
                        progressBar.visibility = View.INVISIBLE

                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            isUser = true
                            simpleViewFlipper.showNext()

                        } else {
                            try {
                                throw task.exception!!
                            } // if user enters wrong email.
                            catch (weakPassword: FirebaseAuthWeakPasswordException) {
                                textviewError.text = getString(R.string.wrong_email)
                            } // if user enters wrong password.
                            catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                                textviewError.text = getString(R.string.wrong_password)
                            } catch (existEmail: FirebaseAuthUserCollisionException) {
                                textviewError.text = getString(R.string.email_already_exists)
                            } catch (e: Exception) {
                                textviewError.text = getString(R.string.somethin_went_wrong)
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
                    errotext += getString(R.string.weak_password) +"\n"
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
        val user = UserQP(
            auth.currentUser?.uid.toString(),
            editTextUsername.text.toString(),
            !isUser,
            0,
            "images/default-user.png",
            emptyList<String>(),
            ""
        )

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
