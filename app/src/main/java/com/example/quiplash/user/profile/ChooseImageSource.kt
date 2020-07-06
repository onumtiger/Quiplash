package com.example.quiplash.user.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.example.quiplash.BuildConfig
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.example.quiplash.user.UserQP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ChooseImageSource : DialogFragment() {
    lateinit var current_User: UserQP
    private val CAMERA_REQUEST_CODE = 1
    private val PICK_IMAGE_REQUEST = 71
    private lateinit var filePath: String
    var photoPath : String = "images/default-user.png"
    private var imageUri: Uri? = null

    //Firebase
    private lateinit var auth: FirebaseAuth
    private var storage: FirebaseStorage? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.interaction_edit_profile_images, container, false)
    }
    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val btnCamera = view.findViewById<AppCompatImageButton>(R.id.camera_button)
        val btnGallery = view.findViewById<AppCompatImageButton>(R.id.gallery_button)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        val callbackGetUser = object:
            Callback<UserQP> {
            override fun onTaskComplete(result : UserQP) {
                current_User = result
            }
        }
        DBMethods.getUser(callbackGetUser)

        btnCamera.setOnClickListener{
            context?.let { it1 ->
                Sounds.playClickSound(
                    it1
                )
            }
            pickFromCamera()
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // pick from camera
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK && (data?.data != null)) {
            //imageUri = data.data
            uploadImage(data.data!!)
        }

        // choose image
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && (data?.data != null)
        ) {
            //imageUri = data.data!!
            uploadImage(data.data!!)
        }

    }


    private fun pickFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //getImageUri()
        //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        /*var outputImg : File

        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).also {

            outputImg = File(it, "CameraContentDemo.jpeg")
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImg))*/

        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
    }

    private fun getImageUri() {
        val m_file: File
        try {
            val m_sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
            var m_curentDateandTime = m_sdf.format(Date())
            var path = Environment.getExternalStorageDirectory()
            path.mkdir()
            var m_imagePath = path
                .absolutePath + File.separator + m_curentDateandTime + ".jpg"
            m_file = File(m_imagePath)
            m_imagePath
            imageUri = context?.let {
                FileProvider.getUriForFile(
                    it,
                    BuildConfig.APPLICATION_ID + ".provider",
                    m_file)
            };
        } catch (e: IOException) {
            e.printStackTrace()
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

    private fun saveImage(picpath:String) {

        val callbackUpdateImage = object:
            Callback<Boolean> {
            override fun onTaskComplete(result:Boolean) {
                if(result){
                    val intent = Intent(context, Edit_ProfileActivity::class.java)
                    startActivity(intent)
                } else{
                    Toast.makeText(context, "Failed ", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        DBMethods.updateUserImage(auth.currentUser?.uid.toString(), picpath, callbackUpdateImage)
    }

    private fun uploadImage(userImage:Uri) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()
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