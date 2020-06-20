package com.example.quiplash

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage

class InviteFriendsToGameListAdapter (val mCtx: Context, val layoutResId: Int, val playerList: MutableList<UserQP>) : ArrayAdapter<UserQP>(mCtx, layoutResId, playerList) {
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
            inviteBtn.isClickable = false
            inviteBtn.setBackgroundResource(R.color.colorGray)
        }

        return view
    }
}