package com.example.quiplash.game

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
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
import com.example.quiplash.database.DBMethods.Companion.updateInvitations
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.example.quiplash.user.UserQP
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONException
import org.json.JSONObject

class InviteFriendsToGameListAdapter (val mCtx: Context, val layoutResId: Int, val playerList: MutableList<UserQP>, val gameID: String) : ArrayAdapter<UserQP>(mCtx, layoutResId, playerList) {
    // Variables
    val FCM_API = "https://fcm.googleapis.com/fcm/send"
    val serverKey =
        "key=" + "AAAAwY4EMK8:APA91bENZDch9bhf-dZG59bEc3dMU1QbH_AF4fRnKqbhOo5eoQDG9pMeA_8R07yfKZ4S7M2MP3_KN5e0kcTbOqyiNpycCdPg5Zl0elI4ZNDNqqtlXuyUiT21382W7-u1sFq-jICptB3B"
    val contentType = "application/json"
    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this.context)
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // View elements
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)
        val friendNameView = view.findViewById<TextView>(R.id.friend_username)
        val friendScoreView = view.findViewById<TextView>(R.id.friend_score)
        val imageViewUser: ImageView = view.findViewById<ImageView>(R.id.profile_image)
        val inviteBtn = view.findViewById<Button>(R.id.inviteBtn)

        // Variables
        var fotostorage = FirebaseStorage.getInstance();
        var storageRef = fotostorage.reference
        val playerPhoto: String
        val player = playerList[position]
        if (player.photo !== null) {
            playerPhoto = player.photo!!
        } else {
            playerPhoto = DBMethods.defaultGuestImg
        }
        setInviteBtn(gameID, player.userID, inviteBtn)
        friendNameView.text = player.userName
        friendScoreView.text = "Score: " + player.score.toString()

        /**
         * load profile picture
         */
        var spaceRef = storageRef.child(playerPhoto)
        spaceRef.downloadUrl
            .addOnSuccessListener(OnSuccessListener<Uri?> { uri ->
                Glide
                    .with(context)
                    .load(uri)
                    .into(imageViewUser)
            }).addOnFailureListener(OnFailureListener { Log.d("Test", " Failed!") })

        /**
         * send game invitation to selected player
         */
        inviteBtn.setOnClickListener {
            Sounds.playClickSound(context)
            inviteBtn.isClickable = false
            inviteBtn.backgroundTintList = ColorStateList.valueOf(R.color.colorGray);

            val notification = JSONObject()
            val notifcationBody = JSONObject()

            try {
                notifcationBody.put("title", "Quiplash Invitation")
                notifcationBody.put("message", "You've been invited to a new game")
                notifcationBody.put("gameID", gameID)
                notification.put("to", player.token)
                notification.put("data", notifcationBody)
                Log.e("TAG", "try")
            } catch (e: JSONException) {
                Log.e("TAG", "onCreate: " + e.message)
            }
            sendNotification(notification)
            setInvitationsInDB(gameID, player.userID)
        }

        return view
    }

    /**
     * track invitations in database to display them in ui and fade invite button out
     */
    fun setInvitationsInDB(gameID: String, userID: String) {
        var game: Game
        val callback = object : Callback<Game> {
            override fun onTaskComplete(result: Game) {
                game = result
                val invitations = game.invitations
                invitations.add(userID)
                updateInvitations(game)
            }
        }
        DBMethods.getCurrentGame(callback, gameID)
    }

    /**
     * fade invite button out if user's been invited
     */
    fun setInviteBtn(gameID: String, userID: String, inviteBtn: Button) {
        var game: Game
        val callback = object : Callback<Game> {
            override fun onTaskComplete(result: Game) {
                game = result
                val invitations = game.invitations
                if (invitations.contains(userID)) {
                    inviteBtn.isClickable = false
                    inviteBtn.setBackgroundResource(R.color.colorGray)
                }
            }
        }
        DBMethods.getCurrentGame(callback, gameID)
    }

    /**
     * set up json request object to send invitation as notification to user
     */
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