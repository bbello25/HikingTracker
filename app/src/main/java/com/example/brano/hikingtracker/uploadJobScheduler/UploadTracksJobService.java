package com.example.brano.hikingtracker.uploadJobScheduler;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import static com.example.brano.hikingtracker.Constants.UPLOAD_TRACkS_JOB_ID;


public class UploadTracksJobService extends JobService {
    private UploadTracksTask mUploadTracksTask = null;


    @Override
    public boolean onStartJob(final JobParameters params) {
        mUploadTracksTask = new UploadTracksTask(this) {
            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                //when job is finished release wakelock if not success reschedule
                jobFinished(params, !success);
            }
        };
        mUploadTracksTask.execute();
        return true;
    }

    //This generally happens when your job conditions are no longer being met, such as when the device has been unplugged or if WiFi is no longer available.
    @Override
    public boolean onStopJob(JobParameters params) {
        if (mUploadTracksTask != null) {
            mUploadTracksTask.cancel(true); //true == reschedule
        }
        return true;
    }

    static void scheduleFilesUpdateJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.schedule(new JobInfo.Builder(UPLOAD_TRACkS_JOB_ID,
                    new ComponentName(context, UploadTracksJobService.class))
                    .setRequiredNetworkType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                            ? JobInfo.NETWORK_TYPE_NOT_ROAMING
                            : JobInfo.NETWORK_TYPE_ANY)
                    .build());
        }
    }


}
