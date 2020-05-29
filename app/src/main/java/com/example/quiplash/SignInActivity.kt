package com.example.quiplash


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider


class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private var errotext: String = ""


    private var authListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get Firebase auth instance
        FirebaseApp.initializeApp(this);
        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance()

        //First check, if user is logged in
        authListener = AuthStateListener { firebaseAuth: FirebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is already logged in -> send to Home-View
                startActivity(Intent(this@SignInActivity, HomeActivity::class.java))
                finish()
            }
        }

        setContentView(R.layout.activity_sign_in)

        val inputEmail = findViewById<EditText>(R.id.emailFieldSU)
        val inputPassword = findViewById<EditText>(R.id.passwordFieldSU)
        progressBar = findViewById(R.id.progressBarLogin)
        val btnSignup = findViewById<Button>(R.id.signupBtn)
        val btnLogin = findViewById<Button>(R.id.signinBtn)
        val btnDB = findViewById<Button>(R.id.database)

        val textviewError = findViewById<TextView>(R.id.textErrorLogin)


        btnSignup.setOnClickListener{
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            finish()
        }

        btnDB.setOnClickListener{
            val intent = Intent(this@SignInActivity, Database::class.java);
            startActivity(intent);
        }



        btnLogin.setOnClickListener{
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
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(
                                this@SignInActivity,
                                getString(R.string.auth_failed),
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
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
