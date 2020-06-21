package com.example.quiplash

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.quiplash.SenNotificationPack.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class AddQuestion : AppCompatActivity() {

    private lateinit var send: Button
    private lateinit var UserTB: EditText
    private lateinit var apiService: APIService



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_question)



//failed send pushup testd


        UserTB=findViewById(R.id.UserID)
        var Title = "titel"
        var Message= "OMG"
        send=findViewById(R.id.button)
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)
        send.setOnClickListener(View.OnClickListener {

            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.d("Emma", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    var token = task.result?.token

                    // Log and toast
                    Log.d("Mein Token", token)
                    Toast.makeText(baseContext, token, Toast.LENGTH_LONG).show()
                    if (token != null) {
                        token = "eufApg1mPuQ:APA91bEsTQ5rzd52D-pNORCwXPTSo3dO9cRADz4pPDQQz725GkWSbALgYqD7ktvu8pqmVcusx49Gn634fy9Uqp4tYIHRwmfzfEwnjb1su1oU5KhOXPWBXJFGWknYCvbVvkbE_9rEyYr1"
                        token = "flyyNgvUoP8:APA91bF6Lp60PgFpO9QXoWeKYRAp98RqQ1zi_vxVW83LEykz7WUoqs-pO0FV-RQCxgdYb1U_ctXJyACQ8xInzgwhBuBDp2WUs-PHKjDuGtJ7XRNGBSlN-Zks6YiUNDi1qt0kFK_VgytO"
                        sendNotification(token, Title.toString().trim(),Message.toString().trim())
                    }

                })

            Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()
            FirebaseDatabase.getInstance().getReference().child("users").child("weaRb4lMiIZYXFCtea4ZnHetsZA2").child("token").addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var usertoken:String=dataSnapshot.getValue(String::class.java).toString()
                    usertoken = "eufApg1mPuQ:APA91bEsTQ5rzd52D-pNORCwXPTSo3dO9cRADz4pPDQQz725GkWSbALgYqD7ktvu8pqmVcusx49Gn634fy9Uqp4tYIHRwmfzfEwnjb1su1oU5KhOXPWBXJFGWknYCvbVvkbE_9rEyYr1"
                    //Toast.makeText(this@AddQuestion , usertoken, Toast.LENGTH_LONG).show()

                    sendNotification(usertoken, Title.toString().trim(),Message.toString().trim())
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        })
        UpdateToken()


    }

    private fun UpdateToken(){
        var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        var refreshToken:String= FirebaseInstanceId.getInstance().getToken().toString()
        var token:Token= Token(refreshToken)
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser()!!.getUid()).setValue(token)
    }


    private fun sendNotification(usertoken:String,title: String,message: String){
        var data= Data(title,message)
        var sender:NotificationSender= NotificationSender(data,usertoken)
        Toast.makeText(this, usertoken, Toast.LENGTH_LONG).show()
        apiService.sendNotifcation(sender)!!.enqueue(object : Callback<MyResponse?> {

            override fun onResponse(call: Call<MyResponse?>, response: Response<MyResponse?>) {
                if (response.code() === 200) {
                    if (response.body()!!.success !== 1) {
                        Toast.makeText(this@AddQuestion, "Failed ", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<MyResponse?>, t: Throwable?) {

            }
        })
    }
}