package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_pw_mail.*
import org.w3c.dom.Text


class Edit_PW_Mail_Activity : AppCompatActivity() {

    private var authListener: FirebaseAuth.AuthStateListener? = null
    private lateinit var auth: FirebaseAuth

    lateinit var view_oldPW :EditText
    lateinit var view_newPW :EditText
    lateinit var view_newPW2 :EditText
    lateinit var view_mail :EditText

    lateinit var btnDeleteAccount :Button

    lateinit var db: CollectionReference
    private val dbUsersPath = "users"



    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_edit_pw_mail)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance().collection(dbUsersPath)

        val saveBtn = findViewById<Button>(R.id.btnEditProfileRest)
        val btnBack = findViewById<AppCompatImageButton>(R.id.profile_game_go_back_arrow)

        view_oldPW = findViewById(R.id.password_old)
        view_newPW = findViewById(R.id.password_new)
        view_newPW2 = findViewById(R.id.password_new2)
        view_mail = findViewById(R.id.email_new)
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount)

        val btnNext = findViewById<Button>(R.id.next_button)

        var textViewMail = findViewById<TextView>(R.id.textViewMail)
        var textViewPw = findViewById<TextView>(R.id.textViewPw)
        var textViewPw2 = findViewById<TextView>(R.id.textViewPw2)

        view_newPW.visibility = View.INVISIBLE
        view_newPW2.visibility = View.INVISIBLE
        view_mail.visibility = View.INVISIBLE
        textViewMail.visibility = View.INVISIBLE
        textViewPw.visibility = View.INVISIBLE
        textViewPw2.visibility = View.INVISIBLE

        saveBtn.visibility = View.INVISIBLE
        btnDeleteAccount.visibility = View.INVISIBLE

        var current_user = FirebaseAuth.getInstance().currentUser



        btnBack.setOnClickListener() {
            Sounds.playClickSound(this)

            val intent = Intent(this, Profile_RegisteredActivity::class.java);
            startActivity(intent);
        }

        btnNext.setOnClickListener() {
            val oldPW = view_oldPW.text.toString()

            if (!oldPW.isNullOrEmpty()) {
                val credential = EmailAuthProvider
                    .getCredential(current_user?.email.toString(), oldPW.toString())
                current_user!!.reauthenticate(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                            btnNext.visibility = View.INVISIBLE
                            textViewPwOld.visibility = View.INVISIBLE
                            view_oldPW.visibility = View.INVISIBLE

                            view_newPW.visibility = View.VISIBLE
                            view_newPW2.visibility = View.VISIBLE
                            view_mail.visibility = View.VISIBLE
                            textViewMail.visibility = View.VISIBLE
                            textViewPw.visibility = View.VISIBLE
                            textViewPw2.visibility = View.VISIBLE

                            saveBtn.visibility = View.VISIBLE
                            btnDeleteAccount.visibility = View.VISIBLE

                        }
                        else {
                            Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show()
                            view_oldPW.text = Editable.Factory.getInstance().newEditable("")
                        }
                    }
            } else {
                Toast.makeText(this, "Please Tip in your current Password", Toast.LENGTH_SHORT).show()
            }
        }

        btnDeleteAccount.setOnClickListener(){
            Sounds.playClickSound(this)

            val del_user = FirebaseAuth.getInstance().currentUser

            val oldPW = view_oldPW.text.toString()

            if (oldPW.isEmpty() == false){
                val credential = EmailAuthProvider
                    .getCredential(del_user?.email.toString(), oldPW.toString())
                del_user!!.reauthenticate(credential)
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            db.document(FirebaseAuth.getInstance().currentUser!!.uid).delete()
                            del_user!!.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        val intent = Intent(this, MainActivity::class.java);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(this, "Password is wrong", Toast.LENGTH_LONG)
                                            .show()
                                    }
                                }
                        }
                    }
            } else {
                Toast.makeText(this, "Please tip in your password", Toast.LENGTH_SHORT).show()
            }
        }

        saveBtn.setOnClickListener() {
            Sounds.playClickSound(this)

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
