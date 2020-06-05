package com.example.quiplash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class Invite_Player : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.interaction_invite_player, container, false)
    }
    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnInvite = view.findViewById<TextView>(R.id.interaction_invite_btn)
        val btnCancel = view.findViewById<TextView>(R.id.interaction_cancel_btn)
        val viewUsername: EditText = view.findViewById(R.id.interaction_username)

        var username = viewUsername.text.toString()

        btnInvite.setOnClickListener(){
            // TO DO: Invite Player to game
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