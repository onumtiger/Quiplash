package com.example.quiplash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quiplash.user.authentication.SignInActivity
import com.example.quiplash.user.authentication.SignUpActivity
import com.example.quiplash.database.DBMethods
import com.example.quiplash.database.Database
import com.example.quiplash.game.GameManager.Companion.setUserinfo
import com.example.quiplash.user.UserQP
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    //FirebaseAuth object
    private var auth: FirebaseAuth? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null

    //Firestore
    lateinit var db: CollectionReference
    private val dbUsersPath = DBMethods.usersPath

    //Local-Storage
    private val PREF_NAME = "Quiplash"
    private val PRIVATE_MODE = 0
    var sharedPreference: SharedPreferences? = null
    val prefKey = "guestid"
    val prefDefValue = "noguest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreference =  getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        db = FirebaseFirestore.getInstance().collection(dbUsersPath)

        FirebaseApp.initializeApp(this);
        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance()

        authListener = AuthStateListener { firebaseAuth: FirebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // launch login activity
                startActivity(Intent(this@MainActivity, LandingActivity::class.java))
                finish()
            } else{
                checkGuestLogin()
            }
        }


        setContentView(R.layout.activity_main)


        val btnSignIn = findViewById<Button>(R.id.sign_in)
        val btnSignUp = findViewById<Button>(R.id.sign_up)
        val btnStart = findViewById<Button>(R.id.start)
        val btnDB = findViewById<Button>(R.id.database)



        btnSignIn.setOnClickListener() {
            val intent = Intent(this, SignInActivity::class.java);
            startActivity(intent);
        }

        btnSignUp.setOnClickListener() {
            val intent = Intent(this, SignUpActivity::class.java);
            startActivity(intent);
        }

        btnStart.setOnClickListener() {
            val intent = Intent(this, LandingActivity::class.java);
            startActivity(intent);
        }

        btnDB.setOnClickListener() {
            val intent = Intent(this, Database::class.java);
            startActivity(intent);
        }

    }

    /**Check if saved Information about a guest exist. **/
    fun checkGuestLogin(){
        if(sharedPreference?.getString(prefKey,prefDefValue) != prefDefValue){
            fetchGuest()
        } else {
            startActivity(Intent(this@MainActivity, SignInActivity::class.java))
            finish()
        }
    }

    /**
     * Get all Information about the guest by the id, which is saved local via 'SharedPreferences'
     * **/
    fun fetchGuest(){
        //Set Database-Instance
        //val userRef = db.collection(sharedPreference?.getString(prefKey,prefDefValue).toString()).document(dbUsersPath)

        //fetch Data
        db.document(sharedPreference?.getString(prefKey,prefDefValue).toString()).get().addOnSuccessListener { documentSnapshot ->
                //save fetched data in GameManager
                val guest = documentSnapshot.toObject(UserQP::class.java)
                if (guest?.userID != null) {
                    setUserinfo(guest)
                    startActivity(Intent(this@MainActivity, LandingActivity::class.java))
                    finish()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Der Gast existiert nicht unter: " + sharedPreference?.getString(prefKey,prefDefValue).toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        auth!!.addAuthStateListener(authListener!!)
    }


    override fun onStop() {
        super.onStop()
        if (authListener != null) {
            auth!!.removeAuthStateListener(authListener!!)
        }
    }
}
