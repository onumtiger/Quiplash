package com.example.quiplash.user.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.bumptech.glide.Glide
import com.example.quiplash.database.DBMethods.Companion.editUser
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.example.quiplash.user.UserQP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class EditProfileActivity : AppCompatActivity() {
    lateinit var currentUser: UserQP
    lateinit var friend: UserQP
    lateinit var otherUsers: ArrayList<UserQP>
    //Firebase
    private lateinit var auth: FirebaseAuth
    private var authListener: FirebaseAuth.AuthStateListener? = null
    private var storage: FirebaseStorage? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.acitvity_edit_profile)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        val fotostorage = FirebaseStorage.getInstance()
        val storageRef = fotostorage.reference
        var photoPath = DBMethods.defaultUserImg
        var score = 0
        var friends = emptyList<String>()
        val viewProfilePic: ImageView = findViewById(R.id.imageView)
        val textErrorUserName = findViewById<TextView>(R.id.textErrorUsernameEdit)
        val btnBack = findViewById<AppCompatImageButton>(R.id.profile_game_go_back_arrow)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnEditPicture = findViewById<Button>(R.id.btnPrrofilePic)
        val btnChangeRest = findViewById<Button>(R.id.edit_rest)
        val viewUsername : EditText = findViewById(R.id.usernameFieldEdit)

        /**
         * load user information and display them
         * if loading data fails display default profile picture and username
         */
        val callback = object: Callback<UserQP> {
            override fun onTaskComplete(result : UserQP) {
                currentUser = result
                if (currentUser.userName == "User") {
                    // display default info if fetching data fails
                    viewUsername.hint = "Username"
                    // set default user image if fetchting data fails
                    val spaceRef = storageRef.child(photoPath)
                    spaceRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            Glide
                                .with(applicationContext)
                                .load(uri)
                                .into(viewProfilePic)
                        }.addOnFailureListener { Log.d("Test", " Failed!") }

                }
                else {
                    photoPath = currentUser.photo.toString()
                    viewUsername.hint = "Username"
                    viewUsername.setText(currentUser.userName)
                    score = currentUser.score
                    friends = currentUser.friends
                    // timer is needed to load new photo if user edits its profile pic
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
        DBMethods.getUser(callback)

        // get all other users
        val callbackGetUsers = object:
            Callback<ArrayList<UserQP>> {
            override fun onTaskComplete(result: ArrayList<UserQP>) {
                otherUsers = result
            }
        }
        DBMethods.getUsers(callbackGetUsers)

        /**
         * go back to profile activity
         */
        btnBack.setOnClickListener {
            Sounds.playClickSound(this)

            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        /**
         * display overlay to choose image source
         */
        btnEditPicture.setOnClickListener{
            Sounds.playClickSound(this)

            val dialogFragment =
                ChooseImageSource()
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("Choose")
            if (prev != null)
            {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, "delete")
        }

        /**
         * start activity to edit sensitive information
         */
        btnChangeRest.setOnClickListener {
            Sounds.playClickSound(this)

            val intent = Intent(this, EditPWMailActivity::class.java)
            startActivity(intent)
        }

        /**
         * save changes in username and update username in every friendslist of his friends
         */
        btnSave.setOnClickListener {
            Sounds.playClickSound(this)
            val username = viewUsername.text.toString()
            val uID = auth.currentUser?.uid.toString()
            val user = UserQP(
                uID,
                username,
                false,
                score,
                photoPath,
                friends,
                currentUser.token
            )
             if (username.isNotEmpty()) {
                 val callbackCheckUsername = object:
                     Callback<Boolean> {
                     override fun onTaskComplete(result: Boolean) {
                         if (result) {
                             textErrorUserName.visibility = View.VISIBLE
                             textErrorUserName.text = getString(R.string.username_not_available)
                         } else {
                             textErrorUserName.visibility = View.INVISIBLE
                             // get friendlist of other user
                             for (i in 0..otherUsers.size-1){
                                 friend = otherUsers[i]
                                 // check if user is exists in other friend list
                                 for(j in 0..friend.friends.size-1){
                                     if(friend.friends[j].equals(currentUser.userName, true)) {
                                         val newfriendsListFriend = emptyList<String>().toMutableList()
                                         // copy friendlist to edit it
                                         for(k in 0..friend.friends.size-1) {
                                             newfriendsListFriend.add(k, friend.friends[k])
                                         }
                                         // update username
                                         newfriendsListFriend[j] = username
                                         // update friend
                                         friend.friends = newfriendsListFriend
                                         friend.userID.let { it1 -> DBMethods.editUserFriends(it1, friend.friends) }
                                     }
                                 }
                             }
                             editUser(uID, user)
                             val intent = Intent(this@EditProfileActivity, ProfileActivity::class.java)
                             startActivity(intent)
                         }
                     }
                 }
                 DBMethods.checkUsername(user.userName, viewUsername.text.toString(), callbackCheckUsername)

             } else {
                 textErrorUserName.visibility = View.VISIBLE
                 textErrorUserName.text = getString(R.string.please_enter_username)
             }
        }
    }
}