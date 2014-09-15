package com.ifit.sfit.sparky.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ifit.sfit.sparky.R;
import com.ifit.sfit.sparky.helperclasses.SFitSysCntrl;
import com.ifit.sfit.sparky.testsdrivers.AllTests;
import com.ifit.sfit.sparky.testsdrivers.BitfieldsTest;
import com.ifit.sfit.sparky.testsdrivers.InclineTest;
import com.ifit.sfit.sparky.testsdrivers.IntegrationTest;
import com.ifit.sfit.sparky.testsdrivers.MotorTest;
import com.ifit.sfit.sparky.testsdrivers.TreadmillKeyCodesTest;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.FitProUsb;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.communication.SystemStatusListener;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

/**
 * This class holds the code for the Main Screen. From here we choose which test we want to run
 * It also sets the Alarm to run all tests automatically at the specified time everyday
 * Created by jc.almonte on 7/29/14.
 */
public class ManageTests extends Activity implements View.OnClickListener, SystemStatusListener {
    public static FecpController fecpController;
    private FecpCommand mSystemStopCmd;
    public static SFitSysCntrl mSFitSysCntrl;
    public static Toast mToast; // Made static to use on email class
    SampleAlarmReceiver alarm = new SampleAlarmReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    /**
     * Initialize menu
     * @param menu the menu
     * @return true when menu created
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Menu options to set and cancel the alarm.
     * @param item the menu item
     * @return true if item selected, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // When the user clicks START ALARM, set the alarm.
//            case R.id.start_action:
//                alarm.setAlarm(this);
//                return true;
            // When the user clicks CANCEL ALARM, cancel the alarm.
            case R.id.cancel_action:
                alarm.cancelAlarm(this);
                return true;
        }
        return false;
    }

    /**
     * Initializes the Controlller for the System Communication with Brainbaord
     * Initializes the Alarm to run tests automatically at a specified time
     * Initializes the UI elements on screen
     */
    public void init()
    {

        try {
            fecpController = new FitProUsb(getApplicationContext(), getIntent());
            mSFitSysCntrl = new SFitSysCntrl(fecpController);
            fecpController.initializeConnection(this);
            alarm.setAlarm(this);

        } catch (Exception e) {
            e.printStackTrace();

        }
        findViewById(R.id.bMotor).setOnClickListener(this);
        findViewById(R.id.bBitfields).setOnClickListener(this);
        findViewById(R.id.bIncline).setOnClickListener(this);
        findViewById(R.id.bIntegration).setOnClickListener(this);
        findViewById(R.id.bTreadmilllKeys).setOnClickListener(this);
        findViewById(R.id.bAllTests).setOnClickListener(this);
    }

    /**
     * Handles the click event on the current view
     * @param view the view clicked
     */
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

            case R.id.bMotor:
                Intent motorTest = new Intent(ManageTests.this, MotorTest.class);
                startActivity(motorTest);
            break;

            case R.id.bIncline:
            Intent inclineTest = new Intent(ManageTests.this, InclineTest.class);
            startActivity(inclineTest);
            break;

            case R.id.bIntegration:
                Intent integrationTest = new Intent(ManageTests.this, IntegrationTest.class);
                startActivity(integrationTest);
            break;

            case R.id.bBitfields:
                Intent bitfieldsTest = new Intent(ManageTests.this, BitfieldsTest.class);
                startActivity(bitfieldsTest);
            break;

            case R.id.bTreadmilllKeys:
                Intent treadmillKeysTest = new Intent(ManageTests.this, TreadmillKeyCodesTest.class);
                startActivity(treadmillKeysTest);
            break;

            case R.id.bAllTests:
                Intent allTests = new Intent(ManageTests.this, AllTests.class);
                allTests.putExtra("message", new String("none"));
                startActivity(allTests);
            break;

        }
    }

    /**
     * this method is called when the system is disconnected. this is the same as the Communications disconnect
     */
    @Override
    public void systemDisconnected() {

        //change display back to the original screen
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null == mToast)
                {
                    mToast = Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_LONG);
                }
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.setText("Connection Lost");

                mToast.show();
//                Toast.makeText(getApplicationContext(),"Connection Lost", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This is called after system is connected
     *
     * @param dev the System device that is connected.
     */
    @Override
    public void systemDeviceConnected(final SystemDevice dev) {

        //if successful the dev won't be null
        //system is connected used this in the rest of the system.

        if(dev == null || dev.getInfo().getDevId() == DeviceId.NONE)
        {
            Toast.makeText(this,"Connection Failed",Toast.LENGTH_LONG).show();
            return;
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null == mToast)
                {
                    mToast = Toast.makeText(getApplicationContext(), "Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getSysDevInfo().getConsoleName(), Toast.LENGTH_LONG);
                }
//                Toast.makeText(getApplicationContext(),"Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getConsoleName() , Toast.LENGTH_LONG).show();
                mToast.setText("Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getSysDevInfo().getConsoleName() );
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.show();
            }
        });

        //check if treadmill or incline trainer
        if(dev.getInfo().getDevId() == DeviceId.INCLINE_TRAINER || dev.getInfo().getDevId() == DeviceId.TREADMILL)
        {
            if(dev.getInfo().getSupportedBitfields().contains(BitFieldId.WORKOUT_MODE)) {

                try {

                    this.mSystemStopCmd = new FecpCommand(dev.getCommand(CommandId.WRITE_READ_DATA));
                    ((WriteReadDataCmd)this.mSystemStopCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This will be called when the communication layer is connected. this is a lower level of
     * communication notification.
     */
    @Override
    public void systemCommunicationConnected() {
        //nothing to do, implemented in fitpro layer
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == mToast) {
                    mToast = Toast.makeText(getApplicationContext(), "Connected ", Toast.LENGTH_LONG);
                }
//                Toast.makeText(getApplicationContext(),"Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getConsoleName() , Toast.LENGTH_LONG).show();
                mToast.setText("Connected ");
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.show();
            }
        });
    }

    /**
     * This will be called when the system has been validated
     */
    @Override
    public void systemSecurityValidated() {

//        //system is validated you may control the system
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (null == mToast) {
//                    mToast = Toast.makeText(getApplicationContext(), "System is Validated", Toast.LENGTH_LONG);
//                }
////                Toast.makeText(getApplicationContext(),"Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getConsoleName() , Toast.LENGTH_LONG).show();
//                mToast.setText("System is Validated");
//                mToast.setDuration(Toast.LENGTH_LONG);
//                mToast.show();
//            }
//        });
//        this.mSFitSysCntrl.getInitialSysItems(this, 0, 0);
    }
}
