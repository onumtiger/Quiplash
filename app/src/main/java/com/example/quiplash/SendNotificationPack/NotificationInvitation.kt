package com.example.quiplash.SendNotificationPack

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.quiplash.database.DBMethods
import com.example.quiplash.game.Game
import com.example.quiplash.game.GameManager
import com.example.quiplash.game.WaitingActivity
import com.example.quiplash.R
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


class NotificationInvitation : FirebaseMessagingService()  {
    private val ADMIN_CHANNEL_ID = "admin_channel"
    lateinit var db: CollectionReference
    private val dbGamesPath = DBMethods.gamesPath

    /**
     * send invite notification to a player
     * if player clicks on notification he/she'll enter the join game view of the game
     */
    @SuppressLint("ResourceAsColor")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        remoteMessage.data["gameID"]?.let {
            db.document(it).get()
                .addOnSuccessListener { documentSnapshot ->
                    try {
                        GameManager.game = documentSnapshot.toObject(Game::class.java)!!
                    } finally {
                        val intent = Intent(this, WaitingActivity::class.java)
                        intent.putExtra("gameID", remoteMessage.data["gameID"])

                        val notificationManager =
                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        //Setting up Notification channels for android O and above
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            setupChannels(notificationManager)
                        }

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        val pendingIntent = PendingIntent.getActivity(
                            applicationContext, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                        val largeIcon = BitmapFactory.decodeResource(
                            resources,
                            R.drawable.ic_launcher_round
                        )
                        val notificationId = Random().nextInt(60000)
                        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val notificationBuilder =
                            NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                                .setSmallIcon(R.drawable.logo)
                                .setLargeIcon(largeIcon)
                                .setContentTitle(
                                    remoteMessage.data["title"]
                                )
                                .setContentText(remoteMessage.data["message"])
                                .setAutoCancel(true) //dismisses the notification on click
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent)
                        notificationManager.notify(
                            notificationId,
                            notificationBuilder.build()
                        )
                    }

                }
        }
    }

    /**
     * set up notification style
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName = "New notification"
        val adminChannelDescription = "Device to device notification"

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = R.color.colorRed
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)
    }
}
