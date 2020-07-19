package com.example.quiplash.user.friends

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.quiplash.*
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.user.UserQP
import java.util.*

class FriendsActivity : AppCompatActivity() {
    // Variables
    lateinit var currentUser: UserQP
    lateinit var friend : UserQP
    lateinit var otherUsers: ArrayList<UserQP>

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        // View elements
        val btnBack = findViewById<AppCompatImageButton>(R.id.friends_go_back_arrow)
        val btnFriend = findViewById<AppCompatImageButton>(R.id.add_friend_btn)
        val friendsListView = findViewById<ListView>(R.id.friends)
        val noFriendsAdded = findViewById<TextView>(R.id.no_friends)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.friends_swiperefresh)

        noFriendsAdded.visibility = View.INVISIBLE

        /**
         * fetch user's friend form db and display them
         */
        getFriendsList(friendsListView)

        /**
         * go back to landing view
         */
        btnBack.setOnClickListener() {
            Sounds.playClickSound(this)

            val intent = Intent(this, LandingActivity::class.java);
            startActivity(intent);
        }

        /**
         * display overlay to add a friend
         */
        btnFriend.setOnClickListener(){
            Sounds.playClickSound(this)

            val dialogFragment = AddPlayer()
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("add")
            if (prev != null)
            {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, "delete")
        }

        /**
         * refresh view after swipe input
         */
        refreshLayout.setOnRefreshListener {
            Sounds.playRefreshSound(this)
            getFriendsList(friendsListView)
            refreshLayout.isRefreshing = false
        }
    }

    /**
     * fetch user's friend form db and display them
     */
    private fun getFriendsList (friendsListView: ListView) {
        val noFriendsAdded = findViewById<TextView>(R.id.no_friends)
        var friendsListCurrentUser: List<String>
        val friendsUserList = mutableListOf<UserQP>()

        val callbackGetUsers = object:
            Callback<ArrayList<UserQP>> {
            override fun onTaskComplete(result: ArrayList<UserQP>) {
                otherUsers = result

                val callbackGetUser = object:
                    Callback<UserQP> {
                    override fun onTaskComplete(result : UserQP) {
                        currentUser = result
                        friendsListCurrentUser = currentUser.friends

                        for(element in friendsListCurrentUser) {
                            for(j in 0 until otherUsers.size) {
                                // get friend information
                                if(element == otherUsers[j].userName) {
                                    friend = otherUsers[j]
                                    friendsUserList.add(friend)
                                    break
                                }
                            }
                        }

                        if (friendsUserList.isNullOrEmpty()) {
                            noFriendsAdded.visibility = View.VISIBLE
                        }
                        val adapter =
                            FriendsListAdapter(
                                applicationContext,
                                R.layout.friends_list_item,
                                currentUser,
                                friendsUserList
                            )

                        friendsListView.adapter = adapter
                    }
                }
                DBMethods.getUser(callbackGetUser)
            }
        }
        DBMethods.getUsers(callbackGetUsers)
    }
}
