package com.locationapp.adwait.locationapp

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.util.Log
import android.widget.Toast
import android.widget.ToggleButton
import com.evernote.android.job.JobManager
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessaging
import com.locationapp.adwait.locationapp.background.BackgroundService
import com.locationapp.adwait.locationapp.jobmanager.JobEngine.Companion.scheduleJob
import com.locationapp.adwait.locationapp.jobmanager.JobEngine.Companion.schedulePeriodicJob
import com.locationapp.adwait.locationapp.location.LocationReceiver
import com.locationapp.adwait.locationapp.utils.Constants
import com.locationapp.adwait.locationapp.utils.Utils


class MainActivity : AppCompatActivity() {
    protected val LAST_JOB_ID = "LAST_JOB_ID"
    protected val TAG = MainActivity::class.java.simpleName
    private lateinit var context : Context
    protected val REQUEST_PERMISSIONS_REQUEST_CODE = 1001
    private var havePermissions = false
    private lateinit var mJobManager : JobManager
    private lateinit var myReceiver: LocationReceiver
    private var mLastJobId: Int = 0
    private lateinit var toggleButton: ToggleButton
    internal fun getContext(): Context {
        return context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate hit")
        context = this
        //instantiate JobManger
        mJobManager = JobManager.instance()
        //Check for previous Jobs if app was running.
        if (savedInstanceState != null) {
            mLastJobId = savedInstanceState.getInt(LAST_JOB_ID, 0);
        }
        setContentView(R.layout.activity_main)
        //Check Permissions for location
        if(!checkPermission())
        {
            havePermissions = false
            requestPermissions();
        }
        havePermissions = true
        //Ui Shenanigans
        val helloWorldTextView : TextView = findViewById<TextView>(R.id.id_text_view)
        val helloWorldText = helloWorldTextView.text
        val ss = SpannableString(helloWorldText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                getString(R.string.task_completed).toast(getContext())
                mLastJobId = schedulePeriodicJob()
            Log.i(TAG, "Clickable Span OnClick")
            }
        }
        ss.setSpan(clickableSpan, 0, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        helloWorldTextView.setMovementMethod(LinkMovementMethod.getInstance());
        helloWorldTextView.setText(ss, TextView.BufferType.SPANNABLE)
        var mtogglebutton = findViewById(R.id.togglebutton) as ToggleButton
        val secretMode = Utils.requestFeature(this)

        mtogglebutton.isChecked = secretMode
        toggleButton = mtogglebutton

        //Setup Service
        val service = Intent(this@MainActivity, BackgroundService::class.java)
        service.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        if (!BackgroundService.IS_SERVICE_RUNNING) {
            service.setAction(Constants.ACTION.STARTBACKGROUND_ACTION)
            BackgroundService.IS_SERVICE_RUNNING = true
        } else {
            service.setAction(Constants.ACTION.STOPBACKGROUND_ACTION)
            BackgroundService.IS_SERVICE_RUNNING = false
        }
        startService(service)

    }
    override fun onSaveInstanceState(outState : Bundle) {
        super.onSaveInstanceState(outState);
        outState.putInt(LAST_JOB_ID, mLastJobId);
    }

    private fun checkPermission(): Boolean {
        Log.d(TAG, "Checking Permissions.")
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }
    private fun requestPermissions() {
        Log.d(TAG, "Requesting Permissions.")
        var shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        if(shouldProvideRationale)
        {
            Snackbar.make(
                    findViewById(R.id.id_text_view),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, View.OnClickListener {
                         fun onClick(view: View) {
                            // Request permission
                            ActivityCompat.requestPermissions(this@MainActivity,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    REQUEST_PERMISSIONS_REQUEST_CODE)
                        }
                    })
                    .show();
        }
        else
            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
    }
    fun onToggleClicked(v:View) {
        if (toggleButton.isChecked())
        {
            Toast.makeText(getApplicationContext(), "HIGH_FREQUENCY HAS BEEN TURNED ON.", Toast.LENGTH_LONG).show()
            Utils.setFeature(this, true)
        }
        else
        {
            Toast.makeText(getApplicationContext(), "HIGH_FREQUENCY HAS BEEN TURNED OFF.", Toast.LENGTH_LONG).show()
            Utils.setFeature(this, false)
        }
    }
    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause hit")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy hit")

    }

    override fun onResume() {
        super.onResume()



        Log.i(TAG, "onResume hit")
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val result = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (result != ConnectionResult.SUCCESS && result != ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            Toast.makeText(this, "Are you running in Emulator ? try a real device.", Toast.LENGTH_SHORT).show()
        }
        //Instantiate LocationReceiver
        myReceiver = LocationReceiver()
        //Instantiate FCM
        var fcmClient : FirebaseMessaging = FirebaseMessaging.getInstance()
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(object:OnCompleteListener<InstanceIdResult> {
                    override fun onComplete(task:Task<InstanceIdResult>) {
                        if (!task.isSuccessful())
                        {
                            Log.w(TAG, "getInstanceId failed", task.getException())
                            return
                        }
                        // Get new Instance ID token
                        val token = task.getResult().getToken()

                    }
                })
        fcmClient.setAutoInitEnabled(true);
        fcmClient.subscribeToTopic("location_updates")
                .addOnCompleteListener { task ->
                    var msg = "Subscribed"
                    if (!task.isSuccessful) {
                        msg = "Failed"
                    }
                    Log.d(TAG, msg)
                }

    }

    override fun onRestart() {
        Log.i(TAG, "onRestart hit")

        super.onRestart()
    }
    override fun onStop() {
        Log.i(TAG, "onStop hit")
        super.onStop()
    }
    override fun onStart() {
        Log.i(TAG, "onStart hit")
        super.onStart()
    }
    fun Any.toast(context: Context, duration: Int = Toast.LENGTH_SHORT): Toast {
        return Toast.makeText(context, this.toString(), duration).apply { show() }
    }

    override fun onRequestPermissionsResult(requestCode:Int,permissions:Array<String>,grantResults:IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> if (grantResults.size <= 0)
            {
                // Permission was not granted.
            }
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // Permission was granted.
                havePermissions = true
            }
            else
            {
                // Permission denied.
                havePermissions = false
                Snackbar.make(
                        findViewById(R.id.id_text_view),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, object:View.OnClickListener {
                            override fun onClick(view:View) {
                                // Build intent that displays the App settings screen.
                                val intent = Intent()
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null)
                                intent.setData(uri)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        })
                        .show()
            }
        }
    }



}
