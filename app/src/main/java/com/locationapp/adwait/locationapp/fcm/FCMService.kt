package com.locationapp.adwait.locationapp.fcm

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.locationapp.adwait.locationapp.background.BackgroundService
import com.locationapp.adwait.locationapp.utils.Constants
import org.json.JSONException

//NotificationHandler for FCM
class FCMService : FirebaseMessagingService() {

    internal var intent: Intent? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        //check if background service is running
        if(!BackgroundService.IS_SERVICE_RUNNING){
            val service = Intent(this@FCMService, BackgroundService::class.java)
            service.setAction(Constants.ACTION.STARTBACKGROUND_ACTION)
            BackgroundService.IS_SERVICE_RUNNING = true
            startService(service)
        }
        else {

            val service = Intent(this@FCMService, BackgroundService::class.java)
            service.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

            service.setAction(Constants.ACTION.HIGH_FREQUENCY)
            BackgroundService.IS_SERVICE_RUNNING = false

            startService(service)
        }
        }
//        send(remoteMessage)


    @Throws(JSONException::class)
    internal fun send(remoteMessage: RemoteMessage?) {
//        intent = Intent(this, BackgroundService::class.java)

    }


}