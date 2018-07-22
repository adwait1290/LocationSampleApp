package com.locationapp.adwait.locationapp.jobmanager

import com.evernote.android.job.JobRequest
import com.evernote.android.job.Job
import com.evernote.android.job.util.support.PersistableBundleCompat
import android.content.Intent
import com.locationapp.adwait.locationapp.background.BackgroundService
import com.locationapp.adwait.locationapp.utils.Constants
import java.util.concurrent.TimeUnit

//JobRunner
class JobEngine() : Job() {


    override fun onRunJob(params: Params): Job.Result {
        val service = Intent(context, BackgroundService::class.java)
        service.setAction(Constants.ACTION.LOCATION)
        context.startService(service)
        return Job.Result.SUCCESS
    }

    companion object {

        val TAG = "JOB_ENGINE"

        fun scheduleJob(): Int {
            val jobId =JobRequest.Builder(JobEngine.TAG)
                    .setExecutionWindow(30_000L, 40_000L)
                    .build()
                    .schedule()
            return jobId
        }

        private fun scheduleAdvancedJob() {
            val extras = PersistableBundleCompat()
            extras.putString("key", "Hello world")

            val jobId = JobRequest.Builder(JobEngine.TAG)
                    .setExecutionWindow(30_000L, 40_000L)
                    .setBackoffCriteria(5_000L, JobRequest.BackoffPolicy.EXPONENTIAL)
                    .setRequiresCharging(true)
                    .setRequiresDeviceIdle(false)
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .setExtras(extras)
                    .setRequirementsEnforced(true)
                    .setUpdateCurrent(true)
                    .build()
                    .schedule()
        }

        fun schedulePeriodicJob(): Int {
            val jobId = JobRequest.Builder(JobEngine.TAG)
                    .setPeriodic(TimeUnit.HOURS.toMillis(1), JobRequest.MIN_FLEX)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .setRequiredNetworkType(JobRequest.NetworkType.ANY)
                    .setUpdateCurrent(true)
                    .build()
                    .schedule()
            return jobId
        }
    }


}