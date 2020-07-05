package com.example.quiplash

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage


class FriendsListAdapter (val mCtx: Context, val layoutResId: Int, val currentUser: UserQP, val playerList: MutableList<UserQP>) : ArrayAdapter<UserQP>(mCtx, layoutResId, playerList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)
        val friendNameView = view.findViewById<TextView>(R.id.friend_username)
        val friendScoreView = view.findViewById<TextView>(R.id.friend_score)
        val imageViewUser: ImageView = view.findViewById<ImageView>(R.id.profile_image)
        val imageViewSeperator: ImageView = view.findViewById<ImageView>(R.id.friends_seperator)
        var fotostorage = FirebaseStorage.getInstance();
        var storageRef = fotostorage.reference
        var deleteButton = view.findViewById<ImageButton>(R.id.friend_delete)
        val playerPhoto: String

        val friend = playerList[position]
        if (friend.photo !== null) {
            playerPhoto = friend.photo!!
        } else {
            playerPhoto = "images/default-guest.png"
        }

        friendNameView.text = friend.userName
        friendScoreView.text = "Score: " + friend.score.toString()

        imageViewSeperator.setImageResource(R.drawable.green_seperator)

        var spaceRef = storageRef.child(playerPhoto)
        spaceRef.downloadUrl
            .addOnSuccessListener(OnSuccessListener<Uri?> { uri ->
                Glide
                    .with(context)
                    .load(uri)
                    .into(imageViewUser)
            }).addOnFailureListener(OnFailureListener { Log.d("Test", " Failed!") })


        deleteButton.setOnClickListener(){
            Sounds.playClickSound(context)

            // remove current user from friend friendlist
            var newfriendsListFriend = emptyList<String>().toMutableList()
            for(k in 0..friend.friends.size-1) {
                newfriendsListFriend.add(k, friend.friends[k])
            }
            newfriendsListFriend.remove(currentUser.userName)
            friend.friends = newfriendsListFriend
            friend.userID?.let { it4 -> DBMethods.editUser(it4, friend) }

            // remove friend from current user friendlist
            var newfriendsListUser = emptyList<String>().toMutableList()
            for(k in 0..currentUser.friends.size-1) {
                newfriendsListUser.add(k, currentUser.friends[k])
            }
            newfriendsListUser.remove(friend.userName)
            currentUser.friends = newfriendsListUser
            currentUser.userID?.let { it5 -> DBMethods.editUser(it5, currentUser) }

            // restart activity
            val intent = Intent(context, FriendsActivity::class.java);
            mCtx.startActivity(intent);
        }

        return view
    }
}