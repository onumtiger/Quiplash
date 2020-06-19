package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class Edit_PW_Mail_Activity : AppCompatActivity() {

    private var authListener: FirebaseAuth.AuthStateListener? = null
    private lateinit var auth: FirebaseAuth

    lateinit var view_oldPW :EditText
    lateinit var view_newPW :EditText
    lateinit var view_newPW2 :EditText
    lateinit var view_mail :EditText

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_edit_pw_mail)

        auth = FirebaseAuth.getInstance()

        val saveBtn = findViewById<Button>(R.id.btnEditProfileRest)
        val btnBack = findViewById<AppCompatImageButton>(R.id.profile_game_go_back_arrow)

        view_oldPW = findViewById(R.id.password_old)
        view_newPW = findViewById(R.id.password_new)
        view_newPW2 = findViewById(R.id.password_new2)
        view_mail = findViewById(R.id.email_new)

        btnBack.setOnClickListener() {
            val intent = Intent(this, Profile_RegisteredActivity::class.java);
            startActivity(intent);
        }

        saveBtn.setOnClickListener() {
            val oldPW = view_oldPW.text.toString()
            val newPW = view_newPW.text.toString()
            val newPW2 = view_newPW2.text.toString()
            val mail = view_mail.text.toString()

            if (oldPW.isEmpty() == false){
                if(newPW == newPW2){
                    if (mail.isEmpty()){
                        changePW()
                        val intent = Intent(this, Profile_RegisteredActivity::class.java);
                        startActivity(intent);
                    } else {
                        changeMail()
                        changePW()
                        val intent = Intent(this, Profile_RegisteredActivity::class.java);
                        startActivity(intent);
                    }
                }
                if(newPW.isEmpty() == newPW2.isEmpty()) {
                    if (mail.isEmpty() == false) {
                        changeMail()
                        val intent = Intent(this, Profile_RegisteredActivity::class.java);
                        startActivity(intent);
                    }
                }

            } else {
                Toast.makeText(this, "Please Tip In Your Password", Toast.LENGTH_LONG).show()
            }

        }

    }

    fun changePW(){

        val oldPW = view_oldPW.text.toString()
        val newPW = view_newPW.text.toString()
        val old_mail = auth.currentUser?.email.toString()

        var user = auth.currentUser
        val credential = EmailAuthProvider.getCredential(old_mail, oldPW)
        user?.reauthenticate(credential)?.addOnCompleteListener{
            if(it.isSuccessful){
                user.updatePassword(newPW).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this, "Update Password", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "old password is false", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeMail(){
        val oldPW = view_oldPW.text.toString()
        val mail = view_mail.text.toString()
        val old_mail = auth.currentUser?.email.toString()

        var user = auth.currentUser
        val credential = EmailAuthProvider.getCredential(old_mail, oldPW)
        user?.reauthenticate(credential)?.addOnCompleteListener{
            if(it.isSuccessful){
                user.updateEmail(mail).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this, "Update Mail", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "old password is false", Toast.LENGTH_LONG).show()
            }
        }
    }
}
