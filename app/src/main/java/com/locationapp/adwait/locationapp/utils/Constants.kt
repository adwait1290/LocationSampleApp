package com.locationapp.adwait.locationapp.utils




class Constants {


    interface ACTION {
        companion object {

            val STARTBACKGROUND_ACTION = "com.locationapp.adwait.locationapp.jobmanager.backgroundservice.action.stopbackground"
            val STOPBACKGROUND_ACTION = "com.locationapp.adwait.locationapp.jobmanager.backgroundservice.action.stopbackground"
            val LOCATION = "com.locationapp.adwait.locationapp.jobmanager.backgroundservice.action.location"
            val HIGH_FREQUENCY = "com.locationapp.adwait.locationapp.jobmanager.backgroundservice.action.high_frequency"
            val ACTION_BROADCAST = "com.locationapp.adwait.locationapp.locationreceiver.action.broadcast"
        }
    }


    interface NOTIFICATION_ID {
        companion object {
            val BACKGROUND_SERVICE = 101
        }
    }
}