package ru.netology.nmedia.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson



class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FCMService", Gson().toJson(message))
    }

    override fun onNewToken(token: String) {
        Log.d("FCMService", "Token: $token")
    }

}
