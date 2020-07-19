package com.example.quiplash.SenNotificationPack

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService


class MyFirebaseIdService:FirebaseMessagingService(){
    /**
     * Set new token
     */
    override fun onNewToken(s:String){
        super.onNewToken(s)
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var refreshToken:String = FirebaseInstanceId.getInstance().token.toString()
        if(firebaseUser!=null){
            updateToken(refreshToken)
        }
    }

    /**
     * update user token
     */
    private fun updateToken(refreshToken:String){
        var token= Token(refreshToken)
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(token)
    }
}