package com.locationapp.adwait.locationapp.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.locationapp.adwait.locationapp.utils.Constants


//BroadcastReceiver for intents
class RestartServiceReceiver: BroadcastReceiver() {
    private val TAG = "RestartServiceReceiver"
    override fun onReceive(context: Context, intent: Intent) {

        if ("android.intent.action.BOOT_COMPLETED" == intent.action)
        {
            val pushIntent = Intent(context, BackgroundService::class.java)
            pushIntent.setAction(Constants.ACTION.STARTBACKGROUND_ACTION)
            context.startService(pushIntent)
//            scheduleJob(context)
        }
        else if ("android.intent.action.RESTART" == intent.action)
        {
            val pushIntent = Intent(context, BackgroundService::class.java)
            pushIntent.setAction(Constants.ACTION.STARTBACKGROUND_ACTION)
            context.startService(pushIntent)
        }
        else if ("adwait.intent.action.RESTART" == intent.action)
        {
            val pushIntent = Intent(context, BackgroundService::class.java)
            pushIntent.setAction(Constants.ACTION.STARTBACKGROUND_ACTION)
            context.startService(pushIntent)
        }
        Log.e(TAG, "onReceive")

    }
//    private fun scheduleJob(context : Context) {
//        var mDispatcher = FirebaseJobDispatcher(GooglePlayDriver(context));
//        val myJob = mDispatcher!!.newJobBuilder()
//                .setService(MyJobService::class.java)
//                .setTag("TAG")
//                .setRecurring(true)
//                .setTrigger(Trigger.executionWindow(0, 20))
//
//                .setLifetime(Lifetime.FOREVER)
//                .setReplaceCurrent(true)
//                .setConstraints(Constraint.ON_ANY_NETWORK)
//                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
//                .build()
//        mDispatcher?.mustSchedule(myJob)

//    }

}