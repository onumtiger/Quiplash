package com.example.quiplash

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class GameListAdapter(val mCtx: Context, val layoutResId: Int, val gameList: List<Game>) : ArrayAdapter<Game>(mCtx, layoutResId, gameList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)
        val textViewGame = view.findViewById<TextView>(R.id.active_game)
        val textViewCategory = view.findViewById<TextView>(R.id.join_active_game_category)
        val textViewPlayers: TextView = view.findViewById<TextView>(R.id.join_active_game_players)
        val imageViewStar: ImageView = view.findViewById<ImageView>(R.id.imageViewStar)
        val imageViewSeperator: ImageView = view.findViewById<ImageView>(R.id.imageViewSeperator)

        val game = gameList[position]
        textViewGame.text = game.game_id
        textViewCategory.text = "Category: " + game.category
        textViewPlayers.text =  game.users.size.toString() + " / " + game.player_number.toString()
        imageViewStar.setImageResource(R.drawable.join_game_star)
        imageViewSeperator.setImageResource(R.drawable.green_seperator)

        return view
    }
}