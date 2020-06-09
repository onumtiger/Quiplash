package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.bumptech.glide.Glide
import com.example.quiplash.DBMethods.DBCalls.Companion.getUser
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class Profile_RegisteredActivity : AppCompatActivity() {
    //FirebaseAuth object
    private var auth: FirebaseAuth? = null
    lateinit var current_User: User

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_registered)

        auth = FirebaseAuth.getInstance()
        var fotostorage = FirebaseStorage.getInstance();
        var storageRef = fotostorage!!.reference

        val btnBack = findViewById<AppCompatImageButton>(R.id.profile_game_go_back_arrow)
        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)
        val btnDeleteAccount = findViewById<Button>(R.id.btnDeleteAccount)
        val viewProfilePic: ImageView = findViewById(R.id.imageView)
        var viewUsername : TextView = findViewById<TextView>(R.id.pw)
        var viewEmail : TextView = findViewById<TextView>(R.id.email)
        var viewScore : TextView = findViewById<TextView>(R.id.score)
        var viewUsernameBig : TextView = findViewById<TextView>(R.id.usernameBig)
        var photoPath = "images/default-guest.png"



        val callback = object: Callback<User> {
            override fun onTaskComplete(result :User) {
                current_User = result
                if (current_User.userName.toString() == "User") {
                    // display default data if fetching user data fails
                    val userinfodefault = getUserInfoDefault()
                    viewUsername.text = userinfodefault[0]
                    viewUsernameBig.text = userinfodefault[0]
                    viewEmail.text = userinfodefault[1]
                    viewScore.text = userinfodefault[2]

                    // set default user image if fetchting data fails
                    var spaceRef = storageRef.child(photoPath)
                    spaceRef.downloadUrl
                        .addOnSuccessListener(OnSuccessListener<Uri?> { uri ->
                            Glide
                                .with(applicationContext)
                                .load(uri)
                                .into(viewProfilePic)
                        }).addOnFailureListener(OnFailureListener { Log.d("Test", " Failed!") })

                }
                else {
                    viewUsername.text = current_User.userName.toString()
                    viewUsernameBig.text = current_User.userName.toString()
                    viewEmail.text = auth?.currentUser?.email.toString()
                    viewScore.text = current_User.score.toString()

                    photoPath = current_User.photo.toString()
                    // timer is needed to load new photo is user edits its profile pic
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        var spaceRef = storageRef.child(photoPath)
                        spaceRef.downloadUrl
                            .addOnSuccessListener(OnSuccessListener<Uri?> { uri ->
                                Glide
                                    .with(applicationContext)
                                    .load(uri)
                                    .into(viewProfilePic)
                            }).addOnFailureListener(OnFailureListener { Log.d("Test", " Failed!") })
                    }, 200)
                }
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

    fun getUserInfoDefault(): Array<String> {
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
