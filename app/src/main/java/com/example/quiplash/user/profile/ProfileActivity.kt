package com.example.quiplash.user.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.quiplash.*
import com.example.quiplash.database.DBMethods.Companion.getUser
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.user.authentication.RegisterGuestActivity
import com.example.quiplash.user.addQuestions.AddQuestionActivity
import com.example.quiplash.user.authentication.ModalGuestInfo
import com.example.quiplash.user.UserQP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class ProfileActivity : AppCompatActivity() {
    //FirebaseAuth object
    private var auth: FirebaseAuth? = null
    lateinit var currentUser: UserQP
    lateinit var db: CollectionReference
    private val dbUsersPath = DBMethods.usersPath


    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance().collection(dbUsersPath)

        val fotostorage = FirebaseStorage.getInstance()
        val storageRef = fotostorage.reference

        val btnBack = findViewById<Button>(R.id.profile_go_back_arrow)
        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)
        val btnaddQuestion = findViewById<Button>(R.id.btnaddQuestion)
        val btnSignOut = findViewById<Button>(R.id.sign_out)
        val btnRegisterGuest = findViewById<Button>(R.id.buttonRegisterGuest)

        val viewProfilePic: ImageView = findViewById(R.id.imageView)
        val viewEmail: TextView = findViewById(R.id.email)
        val viewLabelEmail: TextView = findViewById(R.id.textViewMail)
        val viewScore: TextView = findViewById(R.id.score)
        val viewUsernameBig: TextView = findViewById(R.id.usernameBig)
        var photoPath = DBMethods.defaultUserImg
        val dialogFragmentGuest =
            ModalGuestInfo()
        val fm = supportFragmentManager


        val callback = object : Callback<UserQP> {
            override fun onTaskComplete(result: UserQP) {
                currentUser = result
                if (result.guest!!) {
                    btnRegisterGuest.visibility = View.VISIBLE
                    viewEmail.visibility = View.GONE
                    viewLabelEmail.visibility = View.GONE
                }

                if (currentUser.userName == "User") {
                    // display default data if fetching user data fails
                    val userinfodefault = getUserInfoDefault()
                    viewUsernameBig.text = userinfodefault[0]
                    viewEmail.text = userinfodefault[1]
                    viewScore.text = ("Score: " + userinfodefault[2])

                    // set default user image if fetchting data fails
                    val spaceRef = storageRef.child(photoPath)
                    spaceRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            Glide
                                .with(applicationContext)
                                .load(uri)
                                .into(viewProfilePic)
                        }.addOnFailureListener { Log.d("Test", " Failed!") }

                } else {
                    viewUsernameBig.text = currentUser.userName
                    viewEmail.text = auth?.currentUser?.email.toString()
                    viewScore.text = ("Score: " + currentUser.score.toString())

                    if (currentUser.photo == null) {
                        photoPath = DBMethods.defaultUserImg
                    } else {
                        photoPath = currentUser.photo.toString()
                    }

                    // timer is needed to load new photo is user edits its profile pic
                    val handler = Handler()
                    handler.postDelayed({
                        val spaceRef = storageRef.child(photoPath)
                        spaceRef.downloadUrl
                            .addOnSuccessListener { uri ->
                                Glide
                                    .with(applicationContext)
                                    .load(uri)
                                    .into(viewProfilePic)
                            }.addOnFailureListener { Log.d("Test", " Failed!") }
                    }, 200)
                }
            }
        }
        getUser(callback)

        btnaddQuestion.setOnClickListener {
            if (currentUser.guest!!) {
                dialogFragmentGuest.show(fm, "modal_guest_info")
            } else {
                Sounds.playClickSound(this)

                val intent = Intent(this, AddQuestionActivity::class.java)
                startActivity(intent)
            }
        }

        btnSignOut.setOnClickListener {
            if (currentUser.guest!!) {
                dialogFragmentGuest.show(fm, "modal_guest_info")

            } else {
                Sounds.playClickSound(this)

                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        btnBack.setOnClickListener {
                Sounds.playClickSound(this)

                val intent = Intent(this, LandingActivity::class.java)
                startActivity(intent)
        }

        btnEditProfile.setOnClickListener {
            if (currentUser.guest!!) {
                dialogFragmentGuest.show(fm, "modal_guest_info")
            } else {
                Sounds.playClickSound(this)

                val intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
            }

        }

        btnRegisterGuest.setOnClickListener {
            Sounds.playClickSound(this)

            val intent = Intent(this, RegisterGuestActivity::class.java)
            startActivity(intent)
        }

/*
        btnDeleteAccount.setOnClickListener{
                    Sounds.playClickSound(this)

            val dialogFragment = DeleteAccount()
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("delete")
            if (prev != null)
            {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, "delete")
        }

 */
    }

    fun getUserInfoDefault(): Array<String> {
        val username = "No Username found"
        val email = "No Email found"
        val score = "0"

        val userinfo = arrayOf(
            username,
            email,
            score
        )

        return userinfo
    }


}
