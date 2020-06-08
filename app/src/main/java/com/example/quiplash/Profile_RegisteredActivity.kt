package com.example.quiplash

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.quiplash.DBMethods.DBCalls.Companion.getUser
import com.example.quiplash.DBMethods.DBCalls.Companion.singleUser
import com.google.firebase.auth.FirebaseAuth

class Profile_RegisteredActivity : AppCompatActivity() {
    //FirebaseAuth object
    private var auth: FirebaseAuth? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null
    lateinit var current_User: User

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_registered)

        auth = FirebaseAuth.getInstance()

        val btnBack = findViewById<AppCompatImageButton>(R.id.profile_game_go_back_arrow)
        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)
        val btnDeleteAccount = findViewById<Button>(R.id.btnDeleteAccount)
        // TO DO: load userinformation
        val viewProfilePic: ImageView = findViewById(R.id.imageView)
        var viewUsername : TextView = findViewById<TextView>(R.id.pw)
        var viewEmail : TextView = findViewById<TextView>(R.id.email)
        var viewScore : TextView = findViewById<TextView>(R.id.score)
        var viewUsernameBig : TextView = findViewById<TextView>(R.id.usernameBig)

        viewUsername.text = ""
        viewUsernameBig.text = ""
        viewEmail.text = ""
        viewScore.text = ""

        val callback = object: Callback<User> {
            override fun onTaskComplete(result :User) {
                current_User = result
                viewUsername.text = current_User.userName.toString()
                viewUsernameBig.text = current_User.userName.toString()
                viewEmail.text = auth?.currentUser?.email.toString()
                viewScore.text = current_User.score.toString()
            }
        }
        getUser(callback)

        btnBack.setOnClickListener() {
            val intent = Intent(this, LandingActivity::class.java);
            startActivity(intent);
        }

        btnEditProfile.setOnClickListener() {
            val intent = Intent(this, Edit_ProfileActivity::class.java);
            startActivity(intent);
        }

        btnDeleteAccount.setOnClickListener(){
            val dialogFragment = Delete_Account()
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("delete")
            if (prev != null)
            {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, "delete")
        }
    }

    // TO DO: GET USER INFO
    fun getUserInfo(): Array<String> {


        var username: String =  "singleUser.userName.toString()"
        var email: String = "auth?.currentUser?.email.toString()"
        var score: String = "singleUser.score.toString()"

        val userinfo = arrayOf(
            username,
            email,
            score
        )

        return userinfo
    }

    fun signOut() {
        auth?.signOut()
        startActivity(Intent(this@Profile_RegisteredActivity, SignInActivity::class.java))
        finish()
    }
}
