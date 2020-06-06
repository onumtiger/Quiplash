package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.auth.FirebaseAuth

class Profile_RegisteredActivity : AppCompatActivity() {
    //FirebaseAuth object
    private var auth: FirebaseAuth? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null

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

        val userinfo = getUserInfo()
        viewUsername.text = userinfo[0]
        viewUsernameBig.text = userinfo[0]
        viewEmail.text = userinfo[1]
        viewScore.text = userinfo[2]

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
        var username: String = "No Username found"
        var email: String = "No Email found"
        var score: String = "Score: 123456789"

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
