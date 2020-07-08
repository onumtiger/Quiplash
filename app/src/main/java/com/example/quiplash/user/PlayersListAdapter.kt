package com.example.quiplash.user

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
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage


class PlayersListAdapter(val mCtx: Context, val layoutResId: Int, val playerList: MutableList<UserQP>) : ArrayAdapter<UserQP>(mCtx, layoutResId, playerList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)
        val textViewGame = view.findViewById<TextView>(R.id.textRoundWinnerName)
        val imageViewUser: ImageView = view.findViewById(R.id.players_star)
        var fotostorage = FirebaseStorage.getInstance()
        var storageRef = fotostorage.reference
        val playerPhoto: String

        val player = playerList[position]

        // Get string to profile photo of player
        if (player.photo !== null) {
            playerPhoto = player.photo!!
        } else {
            playerPhoto = "images/default-guest.png"
        }

        textViewGame.text = player.userName

        // Get profile photo of player from db to show it in listView
        val spaceRef = storageRef.child(playerPhoto)
        spaceRef.downloadUrl
            .addOnSuccessListener { uri ->
                Glide
                    .with(context)
                    .load(uri)
                    .into(imageViewUser)
            }.addOnFailureListener { Log.d("Test", " Failed!") }

        return view
    }
}