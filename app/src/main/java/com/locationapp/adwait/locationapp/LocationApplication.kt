package com.locationapp.adwait.locationapp

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.multidex.MultiDex
import com.evernote.android.job.JobManager
import com.google.firebase.messaging.FirebaseMessaging
import android.widget.Toast
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener


class LocationApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        //multiDex helps compile/build process
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this


        appContext = applicationContext
        JobManager.create(this)



    }
    companion object {
        var instance: Application? = null
        private val TAG = "LocationApplication"
        lateinit var appContext: Context
        //unused
        var lastTimeScreenOnMillis = System.currentTimeMillis()
            private set

        private val screenStateReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_SCREEN_ON) {
                    lastTimeScreenOnMillis = System.currentTimeMillis()
                }
            }

        }
    }


}
