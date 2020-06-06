package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class Edit_PW_Mail_Activity : AppCompatActivity() {

    private var authListener: FirebaseAuth.AuthStateListener? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pw_mail)

        auth = FirebaseAuth.getInstance()

        val saveBtn = findViewById<Button>(R.id.btnEditProfileRest)

        val view_oldPW: EditText = findViewById(R.id.password_old)
        val view_newPW: EditText = findViewById(R.id.password_new)
        val view_newPW2: EditText = findViewById(R.id.password_new2)
        val view_mail: EditText = findViewById(R.id.email_new)

        saveBtn.setOnClickListener() {

            val oldPW = view_oldPW.text.toString()
            val newPW = view_newPW.text.toString()
            val newPW2 = view_newPW2.text.toString()
            val mail = view_mail.text.toString()
            val old_mail = auth.currentUser?.email.toString()
            val ID = auth.currentUser?.uid

            if (oldPW.isEmpty() == false){
                if(newPW == newPW2){
                    if (mail.isEmpty()){
                        var user = auth.currentUser
                        val credential = EmailAuthProvider.getCredential(old_mail, oldPW)
                        user?.reauthenticate(credential)?.addOnCompleteListener{
                            if(it.isSuccessful){
                                Toast.makeText(this, "reauthe nice", Toast.LENGTH_LONG).show()
                                user.updatePassword(newPW).addOnCompleteListener { task ->
                                    if (task.isSuccessful){
                                        Toast.makeText(this, "Update Password", Toast.LENGTH_LONG).show()
                                        val intent = Intent(this, Profile_RegisteredActivity::class.java);
                                        startActivity(intent);
                                    }
                                }
                            } else {
                                Toast.makeText(this, "old password is false", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        var user = auth.currentUser
                        val credential = EmailAuthProvider.getCredential(old_mail, oldPW)
                        user?.reauthenticate(credential)?.addOnCompleteListener{
                            if(it.isSuccessful){
                                Toast.makeText(this, "reauthe nice", Toast.LENGTH_LONG).show()
                                user.updatePassword(newPW).addOnCompleteListener { task ->
                                    if (task.isSuccessful){
                                        Toast.makeText(this, "Update Password and Mail", Toast.LENGTH_LONG).show()
                                        val intent = Intent(this, Profile_RegisteredActivity::class.java);
                                        startActivity(intent);
                                    }
                                }
                                user.updateEmail(mail)
                            } else {
                                Toast.makeText(this, "old password is false", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                if(newPW.isEmpty() == newPW2.isEmpty()) {
                    if (mail.isEmpty() == false) {
                        var user = auth.currentUser
                        val credential = EmailAuthProvider.getCredential(old_mail, oldPW)
                        user?.reauthenticate(credential)?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "reauthe nice", Toast.LENGTH_LONG).show()
                                user.updateEmail(mail).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "Update Mail", Toast.LENGTH_LONG)
                                            .show()
                                        val intent =
                                            Intent(this, Profile_RegisteredActivity::class.java);
                                        startActivity(intent);
                                    }
                                }
                            } else {
                                Toast.makeText(this, "old password is false", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }

                    }
                }

            }
        }

    }
}
