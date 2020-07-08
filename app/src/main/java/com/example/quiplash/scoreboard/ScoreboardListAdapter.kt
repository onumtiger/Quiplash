package com.example.quiplash.scoreboard

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.quiplash.R
import com.example.quiplash.user.UserQP
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage


class ScoreboardListAdapter(val mCtx: Context, val layoutResId: Int, val playerList: MutableList<UserQP>) : ArrayAdapter<UserQP>(mCtx, layoutResId, playerList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)
        val textViewGame = view.findViewById<TextView>(R.id.player_name)
        val textViewScore = view.findViewById<TextView>(R.id.player_score)
        val imageViewUser: ImageView = view.findViewById<ImageView>(R.id.player_image)
        var fotostorage = FirebaseStorage.getInstance();
        var storageRef = fotostorage.reference
        val playerPhoto: String

        val player = playerList[position]

        // Get string to profile photo of player
        if (player.photo !== null) {
            playerPhoto = player.photo!!
        } else {
            playerPhoto = "images/default-guest.png"
        }

        // set text & image views in listItem
        textViewScore.text = ("Score: ${player.score}")
        textViewGame.text = player.userName

        // Get profile photo of player from db to show it in listView
        var spaceRef = storageRef.child(playerPhoto)
        spaceRef.downloadUrl
            .addOnSuccessListener(OnSuccessListener<Uri?> { uri ->
                Glide
                    .with(context)
                    .load(uri)
                    .into(imageViewUser)
            }).addOnFailureListener(OnFailureListener { Log.d("Test", " Failed!") })

        return view
    }
}