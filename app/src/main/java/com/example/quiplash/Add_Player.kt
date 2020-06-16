package com.example.quiplash

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import java.util.ArrayList
import kotlin.math.log

class Add_Player : DialogFragment() {
    lateinit var current_User: UserQP
    lateinit var friend : UserQP


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.interaction_add_player, container, false)
    }
    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val btnAdd = view.findViewById<TextView>(R.id.interaction_add_btn)
        val btnCancel = view.findViewById<TextView>(R.id.interaction_cancel_btn)
        val viewUsername: EditText = view.findViewById(R.id.interaction_username_add)

        lateinit var otherUsers: ArrayList<UserQP>
        var friendsListCurrentUser = emptyList<String>()
        var friendsListFriend = emptyList<String>()

        val callbackGetUsers = object: Callback<ArrayList<UserQP>> {
            override fun onTaskComplete(result: ArrayList<UserQP>) {
                otherUsers = result
            }
        }
        DBMethods.DBCalls.getUsers(callbackGetUsers)

        val callbackGetUser = object: Callback<UserQP> {
            override fun onTaskComplete(result :UserQP) {
                current_User = result
                friendsListCurrentUser = current_User.friends
                Log.d("friends current user", friendsListCurrentUser.toString())
            }
        }
        DBMethods.DBCalls.getUser(callbackGetUser)

        btnAdd.setOnClickListener(){
            var usernameFriend = viewUsername.text.toString()
            var alreadyfriends = false

            // check if empty
            if (!usernameFriend.isEmpty()) {
                // check if already friends
                for (i in 0..friendsListCurrentUser.size - 1) {
                    if (friendsListCurrentUser[i].toLowerCase() == usernameFriend.toLowerCase()) {
                        Log.d("already friends", "Failed!")
                        alreadyfriends = true
                        break
                    }
                }
                if (alreadyfriends) {
                    dismiss()
                }
                else {
                    // check if input = username
                    if (usernameFriend.toLowerCase() == current_User.userName.toString().toLowerCase()) {
                        Log.d("Can't add yourself", "Failed!")
                        dismiss()
                    } else {
                        // check if friend user exists and get other user and its friendlist
                        for (i in 0..otherUsers.size - 1) {
                            if (otherUsers[i].userName.toString().toLowerCase() == usernameFriend.toLowerCase()) {
                                friend = otherUsers[i]
                                friendsListFriend = friend.friends
                                Log.d("friend fround", friend.userName.toString())
                            }
                        }
                        if (friendsListFriend.isEmpty()){
                            Log.d("user not found", "Failed!")
                            dismiss()
                        }
                        else {
                            var newfriendsListFriend = emptyList<String>().toMutableList()
                            for(i in 0..friendsListFriend.size-1) {
                                newfriendsListFriend.add(i, friendsListFriend[i])
                            }
                            newfriendsListFriend.add(0, current_User.userName.toString())
                            friend.friends = newfriendsListFriend

                            var newfriendsListCurrentUser = emptyList<String>().toMutableList()
                            for(i in 0..friendsListCurrentUser.size-1) {
                                newfriendsListCurrentUser.add(i,friendsListCurrentUser[i])
                            }
                            newfriendsListCurrentUser.add(0, friend.userName.toString())
                            current_User.friends = newfriendsListCurrentUser

                            // update users
                            current_User.userID?.let { it1 -> DBMethods.DBCalls.editUser(it1, current_User) }
                            friend.userID?.let { it1 -> DBMethods.DBCalls.editUser(it1, friend) }
                        }
                    }
                }
            }
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var  setFullScreen = false
        if (arguments != null) {
            setFullScreen = requireNotNull(arguments?.getBoolean("fullScreen"))
        }
        if (setFullScreen)
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }
}