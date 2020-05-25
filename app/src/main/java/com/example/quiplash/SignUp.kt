package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class SignUp : AppCompatActivity() {
    //view objects
    private var progressBar: ProgressBar? = null

    //FirebaseAuth object
    private var auth: FirebaseAuth? = null
    //var databaseUser: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //Get Firebase auth instance
        FirebaseApp.initializeApp(this);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance()


        val btnSignIn = findViewById<Button>(R.id.signinBtn)
        val btnSignUp = findViewById<Button>(R.id.signupBtn)
        val inputEmail = findViewById<EditText>(R.id.emailField)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val inputPassword2 = findViewById<EditText>(R.id.inputPassword2)

        //progressBar = findViewById(R.id.progressBarScore)
        //btnResetPassword = findViewById(R.id.btn_reset_password);


        //set onClickListener for buttons
        //btnResetPassword = findViewById(R.id.btn_reset_password);


        //set onClickListener for buttons
        btnSignIn.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(Intent(this@SignUp, SignIn::class.java))
            finish()
        })

        btnSignUp.setOnClickListener(View.OnClickListener { v: View? ->

            if(checkInput()) {
                auth!!.createUserWithEmailAndPassword(
                    inputEmail.text.toString(),
                    inputPassword.text.toString()
                )
                    .addOnCompleteListener(this,
                        OnCompleteListener<AuthResult?> { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user: FirebaseUser? = auth!!.getCurrentUser()
                                startActivity(Intent(this@SignUp, HomeScreen::class.java))
                                finish()
                            } else {

                                Toast.makeText(
                                    this@SignUp, "Authentication failed." + task.result,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            // ...
                        })
            } else {
                if (TextUtils.isEmpty(inputEmail.text)) {
                    Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT)
                        .show()
                }

                if (TextUtils.isEmpty(inputPassword.text)) {
                    Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT)
                        .show()
                }

                if (inputPassword.text !== inputPassword2.text) {
                    Toast.makeText(applicationContext, "Passwords need to be identical", Toast.LENGTH_SHORT)
                        .show()
                }

                if (inputPassword.text.length < 6) {
                    Toast.makeText(
                        applicationContext,
                        "Password too short, enter minimum 6 characters!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })


    }

    private fun checkInput(): Boolean {
        val email = findViewById<EditText>(R.id.emailField)
        val password = findViewById<EditText>(R.id.inputPassword)
        val password2 = findViewById<EditText>(R.id.inputPassword2)

        return (!TextUtils.isEmpty(email.text) && !TextUtils.isEmpty(password.text) && !TextUtils.isEmpty(password2.text) && (password.text === password2.text) && password.text.length>= 6)
    }
}
