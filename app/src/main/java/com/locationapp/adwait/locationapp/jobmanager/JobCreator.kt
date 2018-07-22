package com.locationapp.adwait.locationapp.jobmanager

import android.content.Context
import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.evernote.android.job.JobManager

//Job creator
class JobCreator() : JobCreator {

     override fun create(tag: String): Job? {
        when (tag) {
            JobEngine.TAG -> return JobEngine()
            else -> return null
        }
    }
    class AddReceiver: JobCreator.AddJobCreatorReceiver() {
        override fun addJobCreator(context: Context, manager: JobManager) {
            manager.addJobCreator(JobCreator());
        }
    }
}