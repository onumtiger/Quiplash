package com.example.quiplash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class Delete_Account : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.interaction_delete_account, container, false)
    }
    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnDelete = view.findViewById<TextView>(R.id.interaction_delete_btn)
        val btnCancel = view.findViewById<TextView>(R.id.interaction_cancel_btn)

        btnDelete.setOnClickListener(){
            // TO DO: Delete Account
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