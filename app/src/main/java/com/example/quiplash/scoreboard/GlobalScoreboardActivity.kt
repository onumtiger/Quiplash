package com.example.quiplash.scoreboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.quiplash.database.DBMethods.Companion.getUsers
import com.example.quiplash.database.Callback
import com.example.quiplash.LandingActivity
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.example.quiplash.user.UserQP
import java.util.ArrayList

class GlobalScoreboardActivity : AppCompatActivity() {
    lateinit var users: ArrayList<UserQP>

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_global_scoreboard)

        val btnBack = findViewById<AppCompatImageButton>(R.id.scoreboard_go_back_arrow)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefreshScoreboard)

        //set clickListener for back button & refreshListener for view
        btnBack.setOnClickListener{
            Sounds.playClickSound(this)
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        }

        refreshLayout.setOnRefreshListener {
            Sounds.playRefreshSound(this)
            getScores()
            refreshLayout.isRefreshing = false
        }

        val scoreboardList = findViewById<ListView>(R.id.scoreboard_list)
        scoreboardList.viewTreeObserver.addOnScrollChangedListener {
            if (!scoreboardList.canScrollVertically(1)) {
                // Bottom of scroll view, disable refreshLayout
                refreshLayout.isEnabled = false
            }
            if (!scoreboardList.canScrollVertically(-1)) {
                // Top of scroll view, enable refreshLayout
                refreshLayout.isEnabled = true
            }
        }
        getScores()
    }

    /**
     * get all users and scores, sort scores descendingly,
     * show users list with scores
     */
    fun getScores() {
        val scoreboardList = findViewById<ListView>(R.id.scoreboard_list)

        val callback = object:
            Callback<ArrayList<UserQP>> {
            override fun onTaskComplete(result: ArrayList<UserQP>) {
                users = result
                users.sortWith(Comparator { s1: UserQP, s2: UserQP -> s2.score - s1.score })
                val adapter =
                    ScoreboardListAdapter(
                        applicationContext,
                        R.layout.scoreboard_list_item,
                        users
                    )
                scoreboardList.adapter = adapter
            }
        }
        getUsers(callback)
    }

}
