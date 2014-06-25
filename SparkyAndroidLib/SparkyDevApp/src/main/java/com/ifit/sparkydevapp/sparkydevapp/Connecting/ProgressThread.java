/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 3/27/2014
 * @version 1
 * Details.
 */
package com.ifit.sparkydevapp.sparkydevapp.connecting;

import android.app.ProgressDialog;
import android.os.Looper;

import com.ifit.sparky.fecp.communication.SystemStatusListener;

public class ProgressThread extends Thread {

    private boolean stopThread;
    private final int totalProgressTime = 100;
    private ProgressDialog mProgress;
    private int mTimeoutCounter;
    private SystemStatusListener mSysConnectionCallback;

    public ProgressThread(ProgressDialog progressObj, int timeout, SystemStatusListener sysConnectionCallback)
    {
        super();
        stopThread = false;
        this.mTimeoutCounter = timeout;
        this.mSysConnectionCallback = sysConnectionCallback;
        mProgress = progressObj;
    }
    public  void stopProgress()
    {
        stopThread = true;
    }
    @Override
    public void run(){
        stopThread = false;
        int jumpTime = 0;
        int timeOutCount = 0;
        while(jumpTime < totalProgressTime && !stopThread){
            try {
                sleep(100);
                jumpTime += 5;
                timeOutCount++;
                if(timeOutCount > this.mTimeoutCounter)
                {
                    //connection failed
                    Looper.prepare();
                    this.mSysConnectionCallback.systemDisconnected();
                    mProgress.dismiss();
                    stopThread = true;
                    return;
                }
                if(jumpTime == totalProgressTime)
                {
                    jumpTime = 0;
                }
                mProgress.setProgress(jumpTime);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        mProgress.dismiss();

    }
}
