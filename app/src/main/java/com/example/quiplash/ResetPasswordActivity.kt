package com.example.quiplash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    //FirebaseAuth object
    private lateinit var auth: FirebaseAuth
    private var authListener: FirebaseAuth.AuthStateListener? = null

    //view objects
    private lateinit var progressBar: ProgressBar
    private var errotext: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        FirebaseApp.initializeApp(this);
        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance()

        val inputEmail = findViewById<EditText>(R.id.emailFieldReset)
        progressBar = findViewById(R.id.progressBarReset)
        val btnResetPassword = findViewById<Button>(R.id.btnSubmitReset)
        val btnSignIn = findViewById<Button>(R.id.btnBack)

        btnResetPassword.setOnClickListener{
            val intent = Intent(this@ResetPasswordActivity, SignInActivity::class.java);
            startActivity(intent);
        }


        btnResetPassword.setOnClickListener{
            auth.sendPasswordResetEmail(inputEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this@ResetPasswordActivity, SignUpActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "This e-mail does not exist in our system. Please check your input",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }        }


    }


}