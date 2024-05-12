package com.example.webshopgyakorlas;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class NotificationJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        new NotificationHandler(getApplicationContext())
                .send("Siess mielőtt még lemaradsz!");
        return false;
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
