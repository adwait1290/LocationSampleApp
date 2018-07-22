package com.locationapp.adwait.locationapp.background

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.*
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.locationapp.adwait.locationapp.location.LocationReceiver
import com.locationapp.adwait.locationapp.utils.Constants
import com.locationapp.adwait.locationapp.utils.Constants.ACTION.Companion.ACTION_BROADCAST
import com.locationapp.adwait.locationapp.utils.Utils
import java.util.*

class BackgroundService: Service() {

    private val mBinder = LocationBinder()
    //Fused Location Provider API.


    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    // Callback for changes in location.

    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocation: Location
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var myReceiver: LocationReceiver
    private var mChangingConfiguration = false

    companion object {
        val EXTRA_LOCATION = "LOCATION_DATA"
        var IS_SERVICE_RUNNING = false
        private val UPDATE_INTERVAL_IN_MILLISECONDS:Long = 10000
        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

    private val TAG = "BACKGROUND_SERVICE"

    override fun onCreate() {


        val context = this
        super.onCreate()
        Log.i(TAG, "BACKGROUND SERVICE STARTED")
        var filter1 = IntentFilter()
        filter1.addAction("adwait.intent.action.RESTART")
        var rsr = RestartServiceReceiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(rsr,filter1)
        val filter2 = IntentFilter()
        filter2.addAction(Constants.ACTION.LOCATION)
        // init FusedLocationProviderClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Set LocationCallBack
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d(TAG, "onLocationResult got result")
                val intent = Intent(ACTION_BROADCAST)
                mLocation = locationResult.getLastLocation()
                intent.putExtra(EXTRA_LOCATION, locationResult.getLastLocation())
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent)
                return
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
                Log.d(TAG, "Location available.")
            }
        }
        // create a request for location
        createLocationRequest()
        getLastLocation()
        myReceiver = LocationReceiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                IntentFilter(ACTION_BROADCAST));
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (intent != null) {
            val data = intent.getStringExtra("data")
            if (intent.getAction() != null) {
                Log.i(TAG, "Received intent".plus(intent.action))
                if (intent.getAction().equals(Constants.ACTION.STARTBACKGROUND_ACTION)) {
                    Log.i(TAG, "Received Start Background Intent ")
//                    runTask()
                } else if (intent.getAction().equals(
                                Constants.ACTION.STOPBACKGROUND_ACTION)) {
                    Log.i(TAG, "Received Stop Background Intent")

                    stopSelf()
                } else if (intent.getAction().equals(Constants.ACTION.LOCATION)) {
                    Log.i(TAG, "Received Location Intent")
                    createLocationRequest()
                    getLastLocation()
                    requestLocationUpdates(this)
                } else if (intent.getAction().equals(Constants.ACTION.HIGH_FREQUENCY)) {
                    Log.i(TAG, "Received Location Intent")
                    var status = Utils.requestFeature(this)
                    if (status) {
                        createLocationRequest()
                        getLastLocation()
                        requestLocationUpdates(this)
                    }
                }
            }
        }
        return START_REDELIVER_INTENT


    }

    override fun onDestroy() {
        sendBroadcast(Intent("adwait.intent.action.RESTART"))
        super.onDestroy()
        Log.i(TAG, "In onDestroy broadcast sent")

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private fun getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(object : OnCompleteListener<Location> {
                        override fun onComplete(task: Task<Location>) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult()
                            } else {
                                Log.e("Error", "Failed to get location.")
                            }
                        }
                    })
        } catch (e: SecurityException) {
            Log.e("Exception", "Last location permission." + e)
        }
    }
    //not being used but should be throttled.
    fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            Utils.setRequestingLocationUpdates(this, false)
            stopSelf()
        } catch (unlikely: SecurityException) {
            Utils.setRequestingLocationUpdates(this, true)
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely)
        }
    }

    fun requestLocationUpdates(context: Context) {
        Log.i(TAG, "Requesting location updates")
        Utils.setRequestingLocationUpdates(context, true)
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            Utils.setRequestingLocationUpdates(this, false)
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely)
        }
    }
    // UnUsed
    fun runTask() {
        var t = Timer()
        var handler = Handler(Looper.getMainLooper())
        var runnable = object : Runnable {
            public override fun run() {
                //
            }
        }
        var task = object : TimerTask() {
            override fun run() {
                handler.post(runnable)

            }
        }
        t.scheduleAtFixedRate(task, 0, 5000);
        Log.d("LOG", "Task scheduled")
    }

    inner class LocationBinder : Binder() {
        val backgroundService: BackgroundService
            get() {
                return this@BackgroundService
            }
    }
}