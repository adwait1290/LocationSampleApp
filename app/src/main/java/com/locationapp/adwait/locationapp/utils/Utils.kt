package com.locationapp.adwait.locationapp.utils


import android.content.Context
import android.location.Location
import android.preference.PreferenceManager
import com.locationapp.adwait.locationapp.R

import java.text.DateFormat;
import java.util.*


object Utils {

    val KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates"
    val KEY_FEATURE_STATUS = "feature_status"

    fun requestingLocationUpdates(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)
    }
    fun requestFeature(context : Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_FEATURE_STATUS, false)
    }
    fun setFeature(context : Context, status : Boolean)  {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_FEATURE_STATUS, status)
                .apply()
    }

    fun setRequestingLocationUpdates(context: Context, requestingLocationUpdates: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply()
    }


    fun getLocationText(location: Location): String {
        return if (location == null)
            "Unknown location"
        else
            "(" + location!!.getLatitude() + ", " + location!!.getLongitude() + ")"
    }

    fun getLocationTitle(context: Context): String {
        return context.getString(R.string.location_updated,
                DateFormat.getDateTimeInstance().format(Date()))
    }
}
