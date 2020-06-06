package com.example.quiplash

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class PlayersListAdapter(val mCtx: Context, val layoutResId: Int, val playerList: MutableList<String>) : ArrayAdapter<String>(mCtx, layoutResId, playerList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)
        val textViewGame = view.findViewById<TextView>(R.id.active_player)
        val imageViewStar: ImageView = view.findViewById<ImageView>(R.id.players_star)
        val imageViewSeperator: ImageView = view.findViewById<ImageView>(R.id.players_seperator)

        val player = playerList[position]
        textViewGame.text = player
        imageViewStar.setImageResource(R.drawable.join_game_star)
        imageViewSeperator.setImageResource(R.drawable.green_seperator)

        return view
    }
}