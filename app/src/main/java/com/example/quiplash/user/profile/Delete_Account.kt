package com.example.quiplash.user.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.quiplash.R
import com.example.quiplash.Sounds

class Delete_Account : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.interaction_delete_account, container, false)
    }
    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnDelete = view.findViewById<TextView>(R.id.interaction_delete_btn)
        val btnCancel = view.findViewById<TextView>(R.id.interaction_cancel_btn)

        btnDelete.setOnClickListener(){
            context?.let { it1 ->
                Sounds.playClickSound(
                    it1
                )
            }
        }

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