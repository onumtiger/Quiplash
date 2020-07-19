package com.example.quiplash.user.friends

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.quiplash.database.DBMethods
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.example.quiplash.user.UserQP
import com.google.firebase.storage.FirebaseStorage


class FriendsListAdapter (val mCtx: Context, val layoutResId: Int, val currentUser: UserQP, val playerList: MutableList<UserQP>) : ArrayAdapter<UserQP>(mCtx, layoutResId, playerList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // View elements
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)
        val friendNameView = view.findViewById<TextView>(R.id.friend_username)
        val friendScoreView = view.findViewById<TextView>(R.id.friend_score)
        val imageViewUser: ImageView = view.findViewById(R.id.profile_image)
        val deleteButton = view.findViewById<ImageButton>(R.id.friend_delete)

        // Firebase
        val fotostorage = FirebaseStorage.getInstance()
        val storageRef = fotostorage.reference

        // Variables
        val friend = playerList[position]

        if (friend.photo == null) {
            friend.photo = DBMethods.defaultGuestImg
        }

        friendNameView.text = friend.userName
        friendScoreView.text = ("Score: " + friend.score.toString())

        /**
         * load profile picture
         */
        val spaceRef = storageRef.child(friend.photo!!)
        spaceRef.downloadUrl
            .addOnSuccessListener { uri ->
                Glide
                    .with(context)
                    .load(uri)
                    .into(imageViewUser)
            }.addOnFailureListener { Log.d("Test", " Failed!") }

        /**
         * remove friend from user's and friend's friends
         */
        deleteButton.setOnClickListener{
            Sounds.playClickSound(context)

            // remove current user from friend friendlist
            val newfriendsListFriend = emptyList<String>().toMutableList()
            for(k in friend.friends.indices) {
                newfriendsListFriend.add(k, friend.friends[k])
            }
            newfriendsListFriend.remove(currentUser.userName)
            friend.friends = newfriendsListFriend
            friend.userID.let { it4 -> DBMethods.editUser(it4, friend) }

            // remove friend from current user friendlist
            val newfriendsListUser = emptyList<String>().toMutableList()
            for(k in 0..currentUser.friends.size-1) {
                newfriendsListUser.add(k, currentUser.friends[k])
            }
            newfriendsListUser.remove(friend.userName)
            currentUser.friends = newfriendsListUser
            currentUser.userID.let { it5 -> DBMethods.editUser(it5, currentUser) }

            // restart activity
            val intent = Intent(context, FriendsActivity::class.java);
            mCtx.startActivity(intent);
        }

        return view
    }
}