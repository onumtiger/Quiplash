package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.quiplash.database.DBMethods
import com.example.quiplash.LaunchingActivity
import com.example.quiplash.MainActivity
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_pw_mail.*


class Edit_PW_Mail_Activity : AppCompatActivity() {

    private var authListener: FirebaseAuth.AuthStateListener? = null
    private lateinit var auth: FirebaseAuth

    lateinit var friend: UserQP
    lateinit var currentUserName: String
    lateinit var otherUsers: ArrayList<UserQP>


    lateinit var view_oldPW :EditText
    lateinit var view_newPW :EditText
    lateinit var view_newPW2 :EditText
    lateinit var view_mail :EditText

    lateinit var btnDeleteAccount :Button

    lateinit var db: CollectionReference
    private val dbUsersPath = DBMethods.usersPath



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



        btnBack.setOnClickListener {
            Sounds.playClickSound(this)

            val intent = Intent(this, ProfileActivity::class.java);
            startActivity(intent)
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
            deleteAccount()
        }

        saveBtn.setOnClickListener() {
            Sounds.playClickSound(this)

            val newPW = view_newPW.text.toString()
            val newPW2 = view_newPW2.text.toString()
            val mail = view_mail.text.toString()

            //check if both passwords are correct
            if(newPW == newPW2 && !newPW.isNullOrEmpty()){
                if(newPW.length > 5 ){
                    //only password change
                    if (mail.isEmpty()){
                        changePW()
                        val intent = Intent(this, LaunchingActivity::class.java);
                        startActivity(intent);
                    } else {
                        //change mail and password
                        changeMail()
                        changePW()
                        Toast.makeText(this, "Your Password and Mail got updated!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LaunchingActivity::class.java);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "New Password is too short", Toast.LENGTH_SHORT).show()
                }
            }
            if(newPW.isNullOrEmpty() && newPW2.isNullOrEmpty()) {
                if (!mail.isNullOrEmpty()) {
                    changeMail()
                    val intent = Intent(this, LaunchingActivity::class.java);
                    startActivity(intent);
                }
            }
            if ((!newPW.isNullOrEmpty() && newPW2.isNullOrEmpty()) || (!newPW2.isNullOrEmpty() && newPW.isNullOrEmpty())){
                Toast.makeText(this, "Please tip in your new Password into both fields", Toast.LENGTH_SHORT).show()
            }
            if (newPW != newPW2 && !newPW.isNullOrEmpty() && !newPW2.isNullOrEmpty()){
                Toast.makeText(this, "Your two Passwords are different", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun changePW(){

        val newPW = view_newPW.text.toString()

        var user = auth.currentUser
        user?.updatePassword(newPW)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Your Password got updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "error in updating password", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeMail() {
        val mail = view_mail.text.toString()
        var user = auth.currentUser

        user?.updateEmail(mail)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Your Mail got updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "error in updating mail", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun deleteAccount(){
        // get friendlist of other user
        for (i in 0..otherUsers.size-1){
            friend = otherUsers[i]
            // check if user is exists in other friend list
            for(j in 0..friend.friends.size-1){
                if(friend.friends[j].equals(currentUserName, true)) {
                    val newfriendsListFriend = emptyList<String>().toMutableList()
                    // copy friendlist to edit it
                    for(k in 0..friend.friends.size-1) {
                        newfriendsListFriend.add(k, friend.friends[k])
                    }
                    // update username
                    newfriendsListFriend.removeAt(j)
                    // update friend
                    friend.friends = newfriendsListFriend
                    friend.userID.let { it1 -> DBMethods.editUserFriends(it1, friend.friends) }
                    break
                }
            }
        }

        var current_user = FirebaseAuth.getInstance().currentUser
        db.document(FirebaseAuth.getInstance().currentUser!!.uid).delete()
        current_user!!.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Error delete account", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
