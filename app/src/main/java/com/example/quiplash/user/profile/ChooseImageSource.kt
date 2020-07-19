package com.example.quiplash.user.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.DialogFragment
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.example.quiplash.user.UserQP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class ChooseImageSource : DialogFragment() {
    //Firebase
    private lateinit var auth: FirebaseAuth
    private var storage: FirebaseStorage? = null

    // Variables
    lateinit var currentUser: UserQP
    private val CAMERA_REQUEST_CODE = 1
    private val PICK_IMAGE_REQUEST = 71
    private lateinit var filePath: String
    private val photoPath : String = DBMethods.defaultUserImg
    private val photoPathGuest : String = DBMethods.defaultGuestImg

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.interaction_edit_profile_images, container, false)
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View elements
        val btnCamera = view.findViewById<AppCompatImageButton>(R.id.camera_button)
        val btnGallery = view.findViewById<AppCompatImageButton>(R.id.gallery_button)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        val callbackGetUser = object:
            Callback<UserQP> {
            override fun onTaskComplete(result : UserQP) {
                currentUser = result
            }
        }
        DBMethods.getUser(callbackGetUser)

        /**
         * open camera
         */
        btnCamera.setOnClickListener{
            context?.let { it1 ->
                Sounds.playClickSound(
                    it1
                )
            }
            pickFromCamera()
        }

        /**
         * open gallery
         */
        btnGallery.setOnClickListener {
            context?.let { it1 ->
                Sounds.playClickSound(
                    it1
                )
            }
            chooseImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var  setFullScreen = false
        if (arguments != null) {
            setFullScreen = requireNotNull(arguments?.getBoolean("fullScreen"))
        }
        if (setFullScreen)
            setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    /**
     * upload image after returning from selection
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK && (data?.data != null)) {
            uploadImage(data.data!!)
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && (data?.data != null)
        ) {
            uploadImage(data.data!!)
        }
    }

    /**
     * open camera to take a picture
     */
    private fun pickFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
    }

    /**
     * open gallery to choose a picture
     */
    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        val mimeTypes =
            arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    /**
     * update image path entry in database
     */
    private fun saveImage(picpath:String) {
        val callbackUpdateImage = object:
            Callback<Boolean> {
            override fun onTaskComplete(result:Boolean) {
                if(result){
                    val intent = Intent(context, EditProfileActivity::class.java)
                    startActivity(intent)
                } else{
                    Toast.makeText(context, "Failed ", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        DBMethods.updateUserImage(auth.currentUser?.uid.toString(), picpath, callbackUpdateImage)
    }

    /**
     * upload new picture to database and remove old one
     */
    private fun uploadImage(userImage:Uri) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        if(!currentUser.photo.equals(photoPath) && !currentUser.photo.equals(photoPathGuest)){
            val fotostorage = FirebaseStorage.getInstance()
            val storageRef = fotostorage.reference
            val spaceRef = storageRef.child(currentUser.photo.toString())
            // Delete the file
            spaceRef.delete()
        }

        filePath = "images/${UUID.randomUUID()}"
        storage!!.reference.child(filePath)
            .putFile(userImage)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d("CAMERA /", "path: "+it.storage.path)
                storage!!.reference.child(filePath).downloadUrl
                    .addOnSuccessListener { uri ->
                        saveImage(filePath)
                    }
            }.addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(context, "Failed ", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}