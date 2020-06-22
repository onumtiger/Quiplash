package com.example.quiplash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.util.*

class FriendsActivity : AppCompatActivity() {
    lateinit var current_User: UserQP
    lateinit var friend : UserQP
    lateinit var otherUsers: ArrayList<UserQP>

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_friends)

        val btnBack = findViewById<AppCompatImageButton>(R.id.friends_go_back_arrow)
        val btnFriend = findViewById<AppCompatImageButton>(R.id.add_friend_btn)
        val friendsListView = findViewById<ListView>(R.id.friends)
        val noFriendsAdded = findViewById<TextView>(R.id.no_friends)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.friends_swiperefresh)

        noFriendsAdded.visibility = View.INVISIBLE
        // fetch friends from db
        getFriendsList(friendsListView)

        btnBack.setOnClickListener() {
            super.onBackPressed();
        }

        btnFriend.setOnClickListener(){
            val dialogFragment = Add_Player()
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("add")
            if (prev != null)
            {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, "delete")
        }

        refreshLayout.setOnRefreshListener {
            getFriendsList(friendsListView)
            refreshLayout.isRefreshing = false
        }
    }

    fun getFriendsList (friendsListView: ListView) {
        val noFriendsAdded = findViewById<TextView>(R.id.no_friends)
        var friendsListCurrentUser = emptyList<String>()
        val friendsUserList = mutableListOf<UserQP>()

        val callbackGetUsers = object: Callback<ArrayList<UserQP>> {
            override fun onTaskComplete(result: ArrayList<UserQP>) {
                otherUsers = result

                val callbackGetUser = object: Callback<UserQP> {
                    override fun onTaskComplete(result :UserQP) {
                        current_User = result
                        friendsListCurrentUser = current_User.friends

                        for(i in 0 .. friendsListCurrentUser.size-1) {
                            for(j in 0 .. otherUsers.size-1) {
                                // get friend information
                                if(friendsListCurrentUser[i] == otherUsers[j].userName.toString()) {
                                    friend = otherUsers[j]
                                    friendsUserList.add(friend)
                                    break
                                }
                            }
                        }

                        if (friendsUserList.isNullOrEmpty()) {
                            noFriendsAdded.visibility = View.VISIBLE
                        }
                        val adapter = FriendsListAdapter(
                            applicationContext,
                            R.layout.friends_list_item,
                            friendsUserList
                        )
                        friendsListView.adapter = adapter
                    }
                }
                DBMethods.DBCalls.getUser(callbackGetUser)
            }
        }
        DBMethods.DBCalls.getUsers(callbackGetUsers)
    }
}
