package com.example.quiplash.game

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.quiplash.R
import com.google.firebase.auth.FirebaseAuth

class GameListAdapter(val mCtx: Context, val layoutResId: Int, val gameList: List<Game>) : ArrayAdapter<Game>(mCtx, layoutResId, gameList) {
    private lateinit var auth: FirebaseAuth
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)
        val textViewGame = view.findViewById<TextView>(R.id.active_game)
        val textViewCategory = view.findViewById<TextView>(R.id.join_active_game_category)
        val textViewPlayers: TextView = view.findViewById<TextView>(R.id.join_active_game_players)
        val imageViewStar: ImageView = view.findViewById<ImageView>(R.id.imageViewStar)
        val imageViewInvited = view.findViewById<ImageView>(R.id.imageViewInvited)

        auth = FirebaseAuth.getInstance()
        val game = gameList[position]
        textViewGame.text = "Active Game"
        textViewCategory.text = "Category: " + game.category
        textViewPlayers.text =  game.users.size.toString() + " / " + game.playerNumber.toString()
        imageViewStar.setImageResource(R.drawable.join_game_star)

        // If exists, set game title
        if (!game.gameTitle.isNullOrEmpty()) {
            textViewGame.text = game.gameTitle
        }

        // Show notification hint, if user is invited to game
        if (game.invitations.contains(auth.currentUser?.uid.toString())) {
            imageViewInvited.visibility = View.VISIBLE
        } else {
            imageViewInvited.visibility = View.INVISIBLE
        }

        return view
    }
}