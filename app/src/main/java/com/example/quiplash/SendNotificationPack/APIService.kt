package com.example.quiplash.SenNotificationPack


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
public interface APIService {
    @Headers(
            "Content-Type:application/json",
            "Authorization:key=AAAAwY4EMK8:APA91bENZDch9bhf-dZG59bEc3dMU1QbH_AF4fRnKqbhOo5eoQDG9pMeA_8R07yfKZ4S7M2MP3_KN5e0kcTbOqyiNpycCdPg5Zl0elI4ZNDNqqtlXuyUiT21382W7-u1sFq-jICptB3B" // Your server key refer to video for finding your server key
    )
    @POST("fcm/send")
    open fun sendNotifcation(@Body body: NotificationSender?): Call<MyResponse?>?
}