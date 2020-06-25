package com.example.quiplash

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONException
import org.json.JSONObject

class InviteFriendsToGameListAdapter (val mCtx: Context, val layoutResId: Int, val playerList: MutableList<UserQP>) : ArrayAdapter<UserQP>(mCtx, layoutResId, playerList) {
    val FCM_API = "https://fcm.googleapis.com/fcm/send"
    val serverKey =
        "key=" + "AAAAwY4EMK8:APA91bENZDch9bhf-dZG59bEc3dMU1QbH_AF4fRnKqbhOo5eoQDG9pMeA_8R07yfKZ4S7M2MP3_KN5e0kcTbOqyiNpycCdPg5Zl0elI4ZNDNqqtlXuyUiT21382W7-u1sFq-jICptB3B"
    val contentType = "application/json"

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this.context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)
        val friendNameView = view.findViewById<TextView>(R.id.friend_username)
        val friendScoreView = view.findViewById<TextView>(R.id.friend_score)
        val imageViewUser: ImageView = view.findViewById<ImageView>(R.id.profile_image)
        val imageViewSeperator: ImageView = view.findViewById<ImageView>(R.id.friends_seperator)
        var fotostorage = FirebaseStorage.getInstance();
        var storageRef = fotostorage.reference
        val playerPhoto: String
        val inviteBtn = view.findViewById<Button>(R.id.inviteBtn)

        val player = playerList[position]
        if (player.photo !== null) {
            playerPhoto = player.photo!!
        } else {
            playerPhoto = "images/default-guest.png"
        }

        friendNameView.text = player.userName
        friendScoreView.text = "Score: " + player.score.toString()

        imageViewSeperator.setImageResource(R.drawable.green_seperator)

        var spaceRef = storageRef.child(playerPhoto)
        spaceRef.downloadUrl
            .addOnSuccessListener(OnSuccessListener<Uri?> { uri ->
                Glide
                    .with(context)
                    .load(uri)
                    .into(imageViewUser)
            }).addOnFailureListener(OnFailureListener { Log.d("Test", " Failed!") })


        inviteBtn.setOnClickListener {
            Sounds.playClickSound(context)
            inviteBtn.isClickable = false
            inviteBtn.setBackgroundResource(R.color.colorGray)

            val notification = JSONObject()
            val notifcationBody = JSONObject()

            try {
                notifcationBody.put("title", "Quiplash Invitation")
                notifcationBody.put("message", "You've been invited to a new game")   //Enter your notification message
                notification.put("to", "dk4a6NfETqixJ2RzWnvdCA:APA91bGidSPYN-0rDyaJLHv8i8jXdmwhYYO_YukWaLrQIz1mjUW3UFgrrl9Ju6dcbKPVN8HN0Okgy8RDltoGtScbEPJqtLTjVvU7OLnCqhxHtC_1P0akocBkZhRAvqRQr1ftjJXBNxwf")
                //notification.put("to", player.token)
                notification.put("data", notifcationBody)
                Log.e("TAG", "try")
            } catch (e: JSONException) {
                Log.e("TAG", "onCreate: " + e.message)
            }

            // delete after testing
            val handler = Handler()
            handler.postDelayed(Runnable {
                sendNotification(notification)
            }, 5000)
        }

        return view
    }

    fun sendNotification(notification: JSONObject) {
        Log.e("TAG", "sendNotification")
        Log.d("notification", notification.toString())
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")
            },
            Response.ErrorListener {
                Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show()
                Log.i("TAG", "onErrorResponse: Didn't work")
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }
}