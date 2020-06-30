package com.example.quiplash

import android.annotation.SuppressLint
import android.content.Context
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
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
        val imageViewSeperator: ImageView = view.findViewById<ImageView>(R.id.imageViewSeperator)
        val imageViewInvited = view.findViewById<ImageView>(R.id.imageViewInvited)

        auth = FirebaseAuth.getInstance()
        val game = gameList[position]
        textViewGame.text = "Active Game"
        textViewCategory.text = "Category: " + game.category
        textViewPlayers.text =  game.users.size.toString() + " / " + game.playerNumber.toString()
        imageViewStar.setImageResource(R.drawable.join_game_star)
        imageViewSeperator.setImageResource(R.drawable.green_seperator)

        if (!game.gameTitle.isNullOrEmpty()) {
            textViewGame.text = game.gameTitle
        }

        if (game.invitations.contains(auth.currentUser?.uid.toString())) {
            imageViewInvited.visibility = View.VISIBLE
        } else {
            imageViewInvited.visibility = View.INVISIBLE
        }

        return view
    }
}