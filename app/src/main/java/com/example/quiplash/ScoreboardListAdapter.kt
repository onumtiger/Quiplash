package com.example.quiplash

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
        val imageViewSeperator: ImageView = view.findViewById<ImageView>(R.id.imageViewSeperator)
        var fotostorage = FirebaseStorage.getInstance();
        var storageRef = fotostorage.reference
        val playerPhoto: String

        val player = playerList[position]
        if (player.photo !== null) {
            playerPhoto = player.photo!!
        } else {
            playerPhoto = "images/default-guest.png"
        }

        textViewScore.text = "Score: ${player.score}"
        textViewGame.text = player.userName
        imageViewSeperator.setImageResource(R.drawable.green_seperator)

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