package com.example.quiplash

import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.bumptech.glide.Glide
import com.example.quiplash.DBMethods.DBCalls.Companion.editUser
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*


class Edit_ProfileActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 200
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    lateinit var current_User: UserQP
    lateinit var friend: UserQP
    lateinit var otherUsers: ArrayList<UserQP>



    //Firebase
    //private var auth: FirebaseAuth? = null
    private lateinit var auth: FirebaseAuth
    private var authListener: FirebaseAuth.AuthStateListener? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.acitvity_edit_profile)

        auth = FirebaseAuth.getInstance()

        storage = FirebaseStorage.getInstance();
        var fotostorage = FirebaseStorage.getInstance();
        var storageRef = fotostorage.reference
        var photoPath : String = "images/default-user.png"
        var score = 0
        var friends = emptyList<String>()


        var viewProfilePic: ImageView = findViewById(R.id.imageView)
        val btnBack = findViewById<AppCompatImageButton>(R.id.profile_game_go_back_arrow)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnEditPicture = findViewById<Button>(R.id.btnPrrofilePic)
        val btnChangeRest = findViewById<Button>(R.id.edit_rest)
        var viewUsername : EditText = findViewById(R.id.usernameFieldGuest)

        lateinit var test: ArrayList<UserQP>

        val callback = object: Callback<UserQP> {
            override fun onTaskComplete(result :UserQP) {
                current_User = result
                if (current_User.userName.toString() == "User") {
                    // display default info if fetching data fails
                    viewUsername.hint = "Username"
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
                    photoPath = current_User.photo.toString()
                    viewUsername.hint = "Username"
                    viewUsername.setText(current_User.userName.toString())
                    score = current_User.score!!
                    friends = current_User.friends
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
        DBMethods.DBCalls.getUser(callback)

        // get all other users
        val callbackGetUsers = object: Callback<ArrayList<UserQP>> {
            override fun onTaskComplete(result: ArrayList<UserQP>) {
                otherUsers = result
            }
        }
        DBMethods.DBCalls.getUsers(callbackGetUsers)

        btnBack.setOnClickListener() {
            Sounds.playClickSound(this)

            val intent = Intent(this, ProfileActivity::class.java);
            startActivity(intent);
        }

        btnEditPicture.setOnClickListener(){
            Sounds.playClickSound(this)

            // pickFromCamera()
            //chooseImage()

            val dialogFragment = ChooseImageSource()
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("Choose")
            if (prev != null)
            {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, "delete")
        }

        btnChangeRest.setOnClickListener() {
            Sounds.playClickSound(this)

            val intent = Intent(this, Edit_PW_Mail_Activity::class.java);
            startActivity(intent);


/*
            val fm = FirebaseMessaging.getInstance()
            fm.send(
                RemoteMessage.Builder(current_User.userID.toString()+"@fcm.googleapis.com")
                .setMessageId(Integer.toString(1))
                .addData("my_message", "Hello World")
                .addData("my_action", "SAY_HELLO")
                .build())

            val fm = FirebaseMessaging.getInstance()

            fm.send(
                RemoteMessage.Builder(current_User.userID.toString() + "@gcm.googleapis.com")
                    .setMessageId(getMsgId())
                    .addData("key1", "a value")
                    .addData("key2", "another value")
                    .build()
            )

 */
        }

        btnSave.setOnClickListener() {
            Sounds.playClickSound(this)

            var uploadPath = uploadImage()
            if (uploadPath != ""){
                photoPath = uploadPath
            }

            val username = viewUsername.text.toString()
            val ID = auth.currentUser?.uid.toString()

           /* Log.d("friends", friends.toString())
            Log.d("id", ID)
            Log.d("users", test[1].userName.toString())*/

            val user = UserQP(ID, username, false, score, photoPath, friends, current_User.token)
             if (username.isEmpty() == false) {
                 if (ID != null) {
                     // get friendlist of other user
                     for (i in 0..otherUsers.size-1){
                         friend = otherUsers[i]
                         // check if user is exists in other friend list
                         for(j in 0..friend.friends.size-1){
                             if(friend.friends[j].equals(current_User.userName, true)) {
                                 var newfriendsListFriend = emptyList<String>().toMutableList()
                                 // copy friendlist to edit it
                                 for(k in 0..friend.friends.size-1) {
                                     newfriendsListFriend.add(k, friend.friends[k])
                                 }
                                 // update username
                                 newfriendsListFriend[j] = username
                                 // update friend
                                 friend.friends = newfriendsListFriend
                                 friend.userID?.let { it1 -> DBMethods.DBCalls.editUser(it1, friend) }
                             }
                         }
                     }


                     // hier muss die Friendslist der anderen geupdated werden
                     editUser(ID, user)
                     val intent = Intent(this, ProfileActivity::class.java);
                     startActivity(intent);
                 }
             } else {
                 Toast.makeText(this, "please tip in a new username", Toast.LENGTH_LONG).show()
             }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var viewProfilePic: ImageView = findViewById(R.id.imageView)

        // pick from camera
        if (requestCode === CAMERA_REQUEST_CODE && resultCode === Activity.RESULT_OK) {
            val extras: Bundle? = data?.extras
            val imageBitmap = extras?.get("data") as Bitmap?
            viewProfilePic.setImageBitmap(imageBitmap)
        }

        // choose image
        if (requestCode === PICK_IMAGE_REQUEST && resultCode === Activity.RESULT_OK && attr.data != null && data?.data != null
        ) {
            filePath = data?.data!!
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                viewProfilePic.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun pickFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        val mimeTypes =
            arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun uploadImage(): String {
        var photoPath: String = ""
        if (filePath != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            storageReference = storage!!.getReference();
            photoPath = "images/" + UUID.randomUUID().toString()
            val ref =
                storageReference!!.child(photoPath)
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@Edit_ProfileActivity, "Uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this@Edit_ProfileActivity, "Failed ", Toast.LENGTH_SHORT)
                        .show()
                }
        }
        return photoPath
    }
}
