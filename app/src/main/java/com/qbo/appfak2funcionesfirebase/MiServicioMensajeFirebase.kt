package com.qbo.appfak2funcionesfirebase

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MiServicioMensajeFirebase : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        Looper.prepare()
        Handler().post {
            Toast.makeText(baseContext,
            p0.notification?.title+" - " + p0.notification?.body,
            Toast.LENGTH_LONG).show()
        }
        Looper.loop()
    }

}