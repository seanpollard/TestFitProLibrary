package com.ifit.sfit.sparky.activities;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.ifit.sfit.sparky.testsdrivers.AllTests;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SampleSchedulingService extends IntentService {
    public SampleSchedulingService() {
        super("SchedulingService");
    }
    

    @Override
    protected void onHandleIntent(Intent intent) {
        // BEGIN_INCLUDE(service_onhandle)
// 2. put key/value data
        Intent intentAll = new Intent(this, AllTests.class);

        intentAll.putExtra("message", "runAll");

        // 3. or you can add data to a bundle
        Bundle extras = new Bundle();
        extras.putString("status", "Data Received!");

        // 4. add bundle to intent
        intentAll.putExtras(extras);
        intentAll.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentAll);
        // Release the wake lock provided by the BroadcastReceiver.
        SampleAlarmReceiver.completeWakefulIntent(intent);

        // END_INCLUDE(service_onhandle)
    }
    

}
