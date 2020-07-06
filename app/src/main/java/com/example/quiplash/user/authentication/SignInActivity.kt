package com.example.quiplash.user.authentication


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.*
import com.example.quiplash.database.DBMethods
import com.example.quiplash.game.GameManager.Companion.setUserinfo
import com.example.quiplash.user.profile.ResetPasswordActivity
import com.example.quiplash.user.UserQP
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class SignInActivity : AppCompatActivity() {
    //FirebaseAuth object
    private lateinit var auth: FirebaseAuth
    private var authListener: AuthStateListener? = null

    //view objects
    private lateinit var progressBar: ProgressBar
    private var errotext: String = ""

    //Firestore
    lateinit var db: CollectionReference
    private val dbUsersPath = DBMethods.usersPath

    //Local-Storage
    private val PREFNAME = "Quiplash"
    private var PRIVATEMODE = 0
    var sharedPreference: SharedPreferences? = null
    private val prefKey = "guestid"
    private val prefDefValue = "noguest"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get Firebase auth instance
        FirebaseApp.initializeApp(this)
        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance()

        sharedPreference =  getSharedPreferences(PREFNAME, PRIVATEMODE)
        db = FirebaseFirestore.getInstance().collection(dbUsersPath)

        //First check, if user is logged in
        authListener = AuthStateListener { firebaseAuth: FirebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is already logged in -> save User in GameManager
                setUser(auth.currentUser?.uid.toString())
            }
        }
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_sign_in)

        val inputEmail = findViewById<EditText>(R.id.emailFieldSU)
        val inputPassword = findViewById<EditText>(R.id.passwordFieldSU)
        progressBar = findViewById(R.id.progressBarLogin)
        val btnSignup = findViewById<Button>(R.id.signupBtn)
        val btnLogin = findViewById<Button>(R.id.signinBtn)
        val btnLoginGuest = findViewById<Button>(R.id.signinGuestBtn)
        val btnResetPassword = findViewById<Button>(R.id.btnResetPassword)
        val textviewError = findViewById<TextView>(R.id.textErrorLogin)


        btnSignup.setOnClickListener{
            Sounds.playClickSound(this)

            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            finish()
        }

        btnLoginGuest.setOnClickListener{
            Sounds.playClickSound(this)

            if(sharedPreference?.getString(prefKey,prefDefValue) != prefDefValue){
                setUser(sharedPreference?.getString(prefKey,prefDefValue).toString())
            } else{
                Toast.makeText(
                    this@SignInActivity,
                    "No Guest-Account available. Register as Guest or User",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
        }


        btnResetPassword.setOnClickListener{
            Sounds.playClickSound(this)

            val intent = Intent(this@SignInActivity, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener{
            Sounds.playClickSound(this)

            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()

            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                progressBar.visibility = View.VISIBLE

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@SignInActivity) { task ->
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar.visibility = View.INVISIBLE

                        if (!task.isSuccessful) {
                            try {
                                throw task.exception!!
                            } // if user enters wrong email.
                            catch (invalidEmail: FirebaseAuthInvalidUserException) {
                                textviewError.text = getString(R.string.wrong_email)
                            } // if user enters wrong password.
                            catch (wrongPassword: FirebaseAuthInvalidCredentialsException) {
                                textviewError.text = getString(R.string.wrong_password)
                            } catch (e: Exception) {
                                textviewError.text = e.message
                            }
                        } else {
                            setUser(auth.currentUser?.uid.toString())
                        }
                    }
            } else {
                errotext = ""
                if (TextUtils.isEmpty(email)) {
                    errotext += "Enter email address! \n"
                }
                if (TextUtils.isEmpty(password)) {
                    errotext += "Enter password! \n"
                }
                textviewError.text = errotext
            }

        }
    }


    /**User-information will be fetched by id (which we got after login) and saved in GameManager.
     * Then the User is logged in an the view changes to Home-Screen**/
    private fun setUser(userid: String) {
        //fetch User-Data
        FirebaseFirestore.getInstance().collection(dbUsersPath).document(userid).get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(UserQP::class.java)
            if (user != null) {
                //if user exist save fetched user-data in GameManager
                setUserinfo(user)
            }
        }
        val intent = Intent(this@SignInActivity, LandingActivity::class.java)
        startActivity(intent)
        finish()

    }


    /**
     * Authentication functon for login with a google-account
     **/
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //val user = auth.currentUser
                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this@SignInActivity,
                        getString(R.string.auth_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }


    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener!!)
        //val currentUser: FirebaseUser? = auth!!.currentUser
    }


    override fun onStop() {
        super.onStop()
        if (authListener != null) {
            auth.removeAuthStateListener(authListener!!)
        }
    }
}
