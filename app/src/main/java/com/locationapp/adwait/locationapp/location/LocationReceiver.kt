package com.locationapp.adwait.locationapp.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.Toast
import com.locationapp.adwait.locationapp.background.BackgroundService
import com.locationapp.adwait.locationapp.utils.Utils
import java.time.LocalDateTime

/**
 * Created by Ayush Jain on 8/31/17.
 */
//Location specific receiver to communicate with the background service.
//This is attached to the background service.
class LocationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val location : Location = intent.getParcelableExtra(BackgroundService.EXTRA_LOCATION)
        if (location != null)
        {
            Log.d("TAG", "LocationReceiver got a messages")
            //LocalDateTime is apparently not available in previous verions of android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("TAG", LocalDateTime.now().toString() )
            }
            Toast.makeText(context, Utils.getLocationText(location),
                    Toast.LENGTH_SHORT).show()
        }
    }
}