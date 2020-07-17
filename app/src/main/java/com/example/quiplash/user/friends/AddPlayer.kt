package com.example.quiplash.user.friends

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.example.quiplash.user.UserQP
import java.util.ArrayList

class AddPlayer : DialogFragment() {
    lateinit var currentUser: UserQP

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

        /**
         * get all other users to use them in the adding process
         */
        val callbackGetUsers = object:
            Callback<ArrayList<UserQP>> {
            override fun onTaskComplete(result: ArrayList<UserQP>) {
                otherUsers = result
            }
        }
        DBMethods.getUsers(callbackGetUsers)

        /**
         * get the user's information
         */
        val callbackGetUser = object:
            Callback<UserQP> {
            override fun onTaskComplete(result : UserQP) {
                currentUser = result
                friendsListCurrentUser = currentUser.friends
            }
        }
        DBMethods.getUser(callbackGetUser)

        /**
         * Handle the users input by checking if the input user exists
         * and if the they are already friends. If not add the input user as friend
         */
        btnAdd.setOnClickListener(){
            context?.let { it1 ->
                Sounds.playClickSound(
                    it1
                )
            }

            var usernameFriend = viewUsername.text.toString()
            var alreadyfriends = false

            // check if input is empty
            if (usernameFriend.isNotEmpty()) {
                // check if already friends
                for (element in friendsListCurrentUser) {
                    if (element.equals(usernameFriend, true)) {
                        alreadyfriends = true
                        break
                    }
                }
                if (alreadyfriends) {
                    Toast.makeText(context, "You're already friends", Toast.LENGTH_SHORT)
                        .show()
                    dismiss()
                }
                else {
                    // check if input = user's username
                    if (usernameFriend.equals(currentUser.userName.toString(), true)) {
                        Toast.makeText(context, "You can't add yourself", Toast.LENGTH_SHORT)
                            .show()
                        dismiss()
                    } else {
                        var friend = UserQP(
                            "",
                            "",
                            false,
                            -1,
                            "",
                            emptyList<String>(),
                            ""
                        )
                        // check if friend user exists and get other user and it's friendslist
                        for (i in 0..otherUsers.size - 1) {
                            if (otherUsers[i].userName.equals(usernameFriend, true)) {
                                friend = otherUsers[i]
                                friendsListFriend = friend.friends
                            }
                        }
                        if (friend.userID == ""){
                            Toast.makeText(context, "User not found", Toast.LENGTH_SHORT)
                            .show()
                            dismiss()
                        }
                        else {
                            var newfriendsListFriend = emptyList<String>().toMutableList()
                            for(i in 0..friendsListFriend.size-1) {
                                newfriendsListFriend.add(i, friendsListFriend[i])
                            }
                            newfriendsListFriend.add(0, currentUser.userName)
                            friend.friends = newfriendsListFriend

                            var newfriendsListCurrentUser = emptyList<String>().toMutableList()
                            for(i in 0..friendsListCurrentUser.size-1) {
                                newfriendsListCurrentUser.add(i,friendsListCurrentUser[i])
                            }
                            newfriendsListCurrentUser.add(0, friend.userName)
                            currentUser.friends = newfriendsListCurrentUser

                            // update users
                            currentUser.userID?.let { it1 -> DBMethods.editUser(it1, currentUser) }
                            friend.userID?.let { it1 -> DBMethods.editUser(it1, friend) }
                            Toast.makeText(context, "Successfully added!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            val intent = Intent(context, FriendsActivity::class.java);
            startActivity(intent);
        }

        /**
         * Close overlay
         */
        btnCancel.setOnClickListener {
            context?.let { it1 ->
                Sounds.playClickSound(
                    it1
                )
            }
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