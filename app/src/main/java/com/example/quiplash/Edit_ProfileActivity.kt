package com.example.quiplash

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.quiplash.DBMethods.DBCalls.Companion.editUser
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException
import java.io.InputStream


class Edit_ProfileActivity : AppCompatActivity() {
    //FirebaseAuth object
    private var auth: FirebaseAuth? = null
    private var authListener: FirebaseAuth.AuthStateListener? = null
    private val GALLERY_REQUEST_CODE = 100
    private val CAMERA_REQUEST_CODE = 200

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_edit_profile)

        auth = FirebaseAuth.getInstance()

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
            super.onBackPressed();
        }

        btnEditPicture.setOnClickListener(){
            // pickFromGallery()
            pickFromCamera()
       }

        btnSave.setOnClickListener() {
            val username = viewUsername.text.toString()
            val email = viewEmail.text.toString()
            val password = viewPassword.text.toString()

            authListener = FirebaseAuth.AuthStateListener { firebaseAuth: FirebaseAuth ->
                val ID = firebaseAuth.currentUser?.uid
                val user = UserQP(ID, username, false, 0)
                if (ID != null) {
                    editUser(ID, user)
                }
            }

            // TO DO: Save user data to firebase
            setUserInfo(username, email, password)

            val intent = Intent(this, Profile_RegisteredActivity::class.java);
            startActivity(intent);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var viewProfilePic: ImageView = findViewById(R.id.imageView)
        if (resultCode === Activity.RESULT_OK && requestCode === GALLERY_REQUEST_CODE) {
            try {
                val selectedImage = data?.data
                val imageStream: InputStream? = selectedImage?.let {
                    contentResolver.openInputStream(
                        it
                    )
                }
                viewProfilePic.setImageBitmap(BitmapFactory.decodeStream(imageStream))
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }

        if (requestCode === CAMERA_REQUEST_CODE && resultCode === Activity.RESULT_OK) {
            val extras: Bundle? = data?.extras
            val imageBitmap = extras?.get("data") as Bitmap?
            viewProfilePic.setImageBitmap(imageBitmap)
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

    private fun pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        val intent = Intent(Intent.ACTION_PICK)
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.type = "image/*"
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        val mimeTypes =
            arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        // Launching the Intent
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE)
    }

    private fun pickFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
        }
    }

}