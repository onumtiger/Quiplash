package com.example.quiplash.user.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.quiplash.R
import com.example.quiplash.Sounds

/**This Modal informs the Guest-User about the restrictions of a Guest in general.
 * This Modal offers to dismiss the modal or to register.**/
class ModalGuestInfo : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.modal_guest_info, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnCancel = view.findViewById<TextView>(R.id.buttonModalGuestClose)
        val btnSignup = view.findViewById<TextView>(R.id.buttonModalRegister)

        btnCancel.setOnClickListener {
            context?.let { it1 ->
                Sounds.playClickSound(
                    it1
                )
            }
            dismiss()
        }

        btnSignup.setOnClickListener {
            context?.let { it1 ->
                Sounds.playClickSound(
                    it1
                )
            }
            dismiss()
            val intent = Intent(context, RegisterGuestActivity::class.java)
            startActivity(intent)
        }

    }

}