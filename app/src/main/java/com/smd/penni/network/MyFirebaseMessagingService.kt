package com.smd.penni.network

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.smd.penni.data.FirestoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            NotificationHelper.showNotification(
                applicationContext,
                it.title ?: "Penni",
                it.body ?: ""
            )
        }
    }

    override fun onNewToken(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                FirestoreHelper().saveData("users", userId, mapOf("fcmToken" to token))
            }
        }
    }
}
