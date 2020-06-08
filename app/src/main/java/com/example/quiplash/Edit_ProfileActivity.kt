package com.example.quiplash

import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.quiplash.DBMethods.DBCalls.Companion.editUser
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*


class Edit_ProfileActivity : AppCompatActivity() {
    //FirebaseAuth object
    private var auth: FirebaseAuth? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null
    private val GALLERY_REQUEST_CODE = 100
    private val CAMERA_REQUEST_CODE = 200
    private val PICK_IMAGE_REQUEST = 71
    private lateinit var filePath: Uri

    //Firebase
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_edit_profile)

        auth = FirebaseAuth.getInstance()

        storage = FirebaseStorage.getInstance();
        // storageReference = storage!!.getReference();
        storageReference  = FirebaseStorage.getInstance().getReference().child("images/default-guest.png")

        var viewProfilePic: ImageView = findViewById(R.id.imageView)

        storageReference!!.getDownloadUrl()
            .addOnSuccessListener(OnSuccessListener<Uri?> { uri ->
                Log.d("Uri", uri.toString())
                viewProfilePic.setImageURI(null)
                viewProfilePic.setImageURI(uri)
                Log.d("Test", " Success!")
            }).addOnFailureListener(OnFailureListener { Log.d("Test", " Failed!") })

        val btnBack = findViewById<AppCompatImageButton>(R.id.profile_game_go_back_arrow)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnEditPicture = findViewById<Button>(R.id.btnPrrofilePic)
        // TO DO: load userinformation
        var viewUsername : EditText = findViewById(R.id.usernameFieldGuest)
        var viewEmail: EditText = findViewById(R.id.email)
        var viewPassword: EditText = findViewById(R.id.password)

        val userinfo = getUserInfo()
        viewUsername.hint = userinfo[0]
        viewEmail.hint = userinfo[1]
        viewPassword.hint = userinfo[2]

        btnBack.setOnClickListener() {
            val intent = Intent(this, Profile_RegisteredActivity::class.java);
            startActivity(intent);
        }

        btnEditPicture.setOnClickListener(){
            // pickFromGallery()
            // pickFromCamera()
            chooseImage()
       }

        btnSave.setOnClickListener() {
            val username = viewUsername.text.toString()
            val email = viewEmail.text.toString()
            val password = viewPassword.text.toString()

            authListener = FirebaseAuth.AuthStateListener { firebaseAuth: FirebaseAuth ->
                val ID = firebaseAuth.currentUser?.uid
                val user = User(ID, username, false, 0)
                if (ID != null) {
                    editUser(ID, user)
                }
            }

            // TO DO: Save user data to firebase
            setUserInfo(username, email, password)

            uploadImage()
            val intent = Intent(this, Profile_RegisteredActivity::class.java);
            startActivity(intent);
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

    // TO DO: GET USER INFO
    fun getUserInfo(): Array<String> {
        var username: String = "No Username found"
        var email: String = "No Email found"
        var password: String = "••••••••••••"

        val userinfo = arrayOf(
            username,
            email,
            password
        )

        return userinfo
    }

    // TO DO: SET USER INFO
    fun setUserInfo(username: String, email: String, password: String) {

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

    private fun uploadImage() {
        if (filePath != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            val ref =
                storageReference!!.child("images/" + UUID.randomUUID().toString())
            ref.putFile(filePath)
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
    }

}