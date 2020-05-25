package com.example.quiplash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomeScreen : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        auth = FirebaseAuth.getInstance()

        val btnSignout = findViewById<Button>(R.id.btn_signout)

        btnSignout.setOnClickListener(View.OnClickListener { v: View? ->
            signOut()
        })
    }

    //sign out method
    fun signOut() {
        auth?.signOut()
        startActivity(Intent(this@HomeScreen, SignIn::class.java))
        finish()
    }
}
