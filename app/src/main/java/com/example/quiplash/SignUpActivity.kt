package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {
    //view objects
    private lateinit var progressBar: ProgressBar

    //FirebaseAuth object
    private lateinit var auth: FirebaseAuth
    private var errotext: String = ""
    private lateinit var simpleViewFlipper: ViewFlipper

    //Firestore
    lateinit var db: DocumentReference
    private val collectionUser: String = "Users" //TODO: namen an den aus der DB anpassen

    //UserInfo
    var userFB: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //Get Firebase auth instance
        FirebaseApp.initializeApp(this);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance()
        //db = FirebaseFirestore.getInstance().document(collectionUser)

        val btnSignIn = findViewById<Button>(R.id.signinBtn)
        val btnSignUp = findViewById<Button>(R.id.signupBtn)
        val btnSubmit = findViewById<Button>(R.id.submitBtn)
        val inputEmail = findViewById<EditText>(R.id.emailFieldSU)
        val inputPassword = findViewById<EditText>(R.id.passwordFieldSU)
        val inputPassword2 = findViewById<EditText>(R.id.passwordRetypeFieldSU)
        val inputUsername = findViewById<EditText>(R.id.editTextUsername)
        val textviewError = findViewById<TextView>(R.id.textError)
        progressBar = findViewById(R.id.progressBarSignup)
        simpleViewFlipper = findViewById(R.id.simpleViewFlipper) // get the reference of ViewFlipper


        // Declare in and out animations and load them using AnimationUtils class
        val inAni = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)

        // set the animation type to ViewFlipper

        // set the animation type to ViewFlipper
        simpleViewFlipper.setInAnimation(inAni)
        simpleViewFlipper.setOutAnimation(out)

        val inflaterDialog = layoutInflater

        //set onClickListener for buttons
        btnSignIn.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
            finish()
        }

        btnSubmit.setOnClickListener {
            createUser()
            startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
            finish()
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
                                userFB = auth.getCurrentUser()
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


    }

    private fun checkInput(): Boolean {
        return (!TextUtils.isEmpty(emailFieldSU.text) && !TextUtils.isEmpty(passwordRetypeFieldSU.text) &&
                !TextUtils.isEmpty(passwordFieldSU.text) && (passwordRetypeFieldSU.text.toString() == passwordFieldSU.text.toString()) && passwordFieldSU.text.length >= 6)
    }

    private fun createUser() {
        /*val user = User(userFB?.uid, inputUsername.text.toString())
        db.set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "DocumentSnapshot successfully written!", Toast.LENGTH_LONG).show()
                Log.d("SUCCESS", "DocumentSnapshot successfully written!")

            }.addOnFailureListener {
                    e -> Log.e("ERROR", "Error writing document", e)
            }*/
    }
}
