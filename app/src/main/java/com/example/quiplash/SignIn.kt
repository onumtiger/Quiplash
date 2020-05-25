package com.example.quiplash


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class SignIn : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var progressBar: ProgressBar? = null


    private var authListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get Firebase auth instance
        FirebaseApp.initializeApp(this);
        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance()

        authListener = AuthStateListener { firebaseAuth: FirebaseAuth ->
            val user1 = firebaseAuth.currentUser
            if (user1 != null) {
                // launch login activity
                startActivity(Intent(this@SignIn, HomeScreen::class.java))
                finish()
            }
        }

        setContentView(R.layout.activity_sign_in)

        val inputEmail = findViewById<EditText>(R.id.emailField)
        val inputPassword = findViewById<EditText>(R.id.passwordField)
        progressBar = findViewById(R.id.progressBarLogin)
        val btnSignup = findViewById<Button>(R.id.signup)
        val btnLogin = findViewById<Button>(R.id.signupBtn)


        btnSignup.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(Intent(this@SignIn, SignUp::class.java))
            finish()
        })



        btnLogin.setOnClickListener(View.OnClickListener { v: View? ->
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()


            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                progressBar!!.visibility = View.VISIBLE

                //authenticate user
                auth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@SignIn) { task ->
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar!!.visibility = View.INVISIBLE
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(
                                this@SignIn,
                                getString(R.string.auth_failed),
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            val intent = Intent(this@SignIn, HomeScreen::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
            } else {
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT)
                        .show()
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        })
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth!!.currentUser
                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this@SignIn,
                        getString(R.string.auth_failed),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                }
            }
    }


    override fun onStart() {
        super.onStart()
        auth!!.addAuthStateListener(authListener!!)
        val currentUser: FirebaseUser? = auth!!.currentUser
        //updateUI(currentUser)
    }


    override fun onStop() {
        super.onStop()
        if (authListener != null) {
            auth!!.removeAuthStateListener(authListener!!)
        }
    }
}
