package com.example.quiplash.user.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.example.quiplash.user.authentication.SignInActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

/**If the User forgot the Password he can enter the e-mail-address and receives a message
 * with which he can reset his password**/
class ResetPasswordActivity : AppCompatActivity() {

    //FirebaseAuth object
    private lateinit var auth: FirebaseAuth

    //view objects
    private lateinit var progressBar: ProgressBar
    private lateinit var simpleViewFlipper: ViewFlipper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Remove Top-Bar
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_reset_password)

        FirebaseApp.initializeApp(this)
        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance()

        //Set View-Elements
        val inputEmail = findViewById<EditText>(R.id.emailFieldReset)
        progressBar = findViewById(R.id.progressBarReset)
        val btnResetPassword = findViewById<Button>(R.id.btnSubmitReset)
        val btnBackSignIn = findViewById<Button>(R.id.btnBack)
        val btnSignIn = findViewById<Button>(R.id.btnSigninRP)
        val textViewError = findViewById<TextView>(R.id.textErrorReset)
        simpleViewFlipper =
            findViewById(R.id.simpleViewFlipperRP) // get the reference of ViewFlipper

        // Declare in and out animations and load them using AnimationUtils class
        val inAni = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)

        // set the animation type to ViewFlipper
        simpleViewFlipper.inAnimation = inAni
        simpleViewFlipper.outAnimation = out

        //Switch to Sign-In-View
        btnBackSignIn.setOnClickListener {
            Sounds.playClickSound(this)

            val intent = Intent(this@ResetPasswordActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        //Switch to Sign-In-View
        btnSignIn.setOnClickListener {
            Sounds.playClickSound(this)

            startActivity(Intent(this@ResetPasswordActivity, SignInActivity::class.java))
            finish()
        }

        //Entered E-Mail will be checked. If E-mail does not exist, corresponding Feedback will be displayed.
        //Else E-mail will be send and a Success-View will be shown.
        btnResetPassword.setOnClickListener {
            Sounds.playClickSound(this)

            auth.sendPasswordResetEmail(inputEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) { //E-Mail exists. Success-View appears
                        textViewError.text = ""
                        simpleViewFlipper.showNext()
                    } else { //E-Mial does not exist
                        textViewError.text = getString(R.string.email_already_exists)
                    }
                }
        }


    }


}