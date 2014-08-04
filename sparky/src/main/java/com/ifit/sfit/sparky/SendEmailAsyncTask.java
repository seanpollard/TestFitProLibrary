package com.ifit.sfit.sparky;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ifit.sfit.sparky.testsdrivers.BaseTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

/**
 * Created by jc.almonte on 7/10/14.
 */
public class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
    //Set up default email with username and password
    Mail m = new Mail("fitprotesting.icon@gmail.com", "fitprotest2014");
    DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM hh:mm:ss a");

    public SendEmailAsyncTask(String toString) {
        if (BuildConfig.DEBUG)
            Log.v(SendEmailAsyncTask.class.getName(), "SendEmailAsyncTask()");
        //Add the email addresses of anyone the email should be sent to
        String[] toArr = {toString};
        m.setTo(toArr);
        //Who the email is from
        m.setFrom("fitprotesting.icon@gmail.com");
        //Email subject
        m.setSubject("FitPro Automation Test Results: " + dateFormat.format(Calendar.getInstance().getTime()));
//        m.setBody("see attachment");
        try {
            //Add attachment at the filename listed
            m.addAttachment("data/data/com.ifit.sfit.sparky/files/test.txt");

            if(m.send()) {
                for(int i = 0; i < toArr.length; i++){
                    Toast.makeText(BaseTest.getAppContext(), "Email was sent successfully to " + toArr[i], Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(BaseTest.getAppContext(), "Email was not sent.", Toast.LENGTH_LONG).show();
            }
        } catch(Exception e) {
            //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
            Log.e("FitPro Test App", "Could not send email", e);
        }

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (BuildConfig.DEBUG) Log.v(SendEmailAsyncTask.class.getName(), "doInBackground()");
        try {
            m.send();
            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
//                Log.e(SendEmailAsyncTask.class.getName(), m.getTo(null) + "failed");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
