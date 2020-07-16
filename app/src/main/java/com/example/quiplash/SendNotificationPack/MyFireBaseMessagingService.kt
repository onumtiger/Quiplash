package com.example.quiplash.SendNotificationPack


import android.R
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * display notification in notification bar
 */
class MyFireBaseMessagingService:FirebaseMessagingService(){
    private lateinit var title:String;
    private lateinit var message:String;
    override fun onMessageReceived(@NonNull remoteMessage:RemoteMessage){
        super.onMessageReceived(remoteMessage)
        title = remoteMessage.data["Title"].toString()
        message = remoteMessage.data["Message"].toString()
        val builder = NotificationCompat.Builder(applicationContext)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(title)
                .setContentText(message)
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder.build())
    }
}