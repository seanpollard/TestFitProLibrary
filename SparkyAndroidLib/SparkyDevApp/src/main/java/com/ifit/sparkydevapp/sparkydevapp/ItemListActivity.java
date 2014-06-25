package com.ifit.sparkydevapp.sparkydevapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.FitProTcp;
import com.ifit.sparky.fecp.FitProUsb;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.communication.SystemStatusListener;
import com.ifit.sparky.fecp.error.ErrorEventListener;
import com.ifit.sparky.fecp.error.SystemError;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparkydevapp.sparkydevapp.connecting.ConnectionActivity;
import com.ifit.sparkydevapp.sparkydevapp.connecting.ProgressThread;
import com.ifit.sparkydevapp.sparkydevapp.fragments.BaseInfoFragment;
import com.ifit.sparkydevapp.sparkydevapp.fragments.ErrorFragment;
import com.ifit.sparkydevapp.sparkydevapp.fragments.InclineDeviceFragment;
import com.ifit.sparkydevapp.sparkydevapp.fragments.MainDeviceInfoFragment;
import com.ifit.sparkydevapp.sparkydevapp.fragments.SpeedDeviceFragment;
import com.ifit.sparkydevapp.sparkydevapp.fragments.TaskInfoFragment;
import com.ifit.sparkydevapp.sparkydevapp.fragments.UserDataFragment;
import com.ifit.sparkydevapp.sparkydevapp.listFragments.MainInfoListFragmentControl;

import java.util.ArrayList;

public class ItemListActivity extends FragmentActivity
        implements MainInfoListFragmentControl.Callbacks, SystemStatusListener, ErrorEventListener, OnCommandReceivedListener, Runnable {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private ProgressDialog mProgress;
    private FecpController mFitProCntrl;
    private boolean mConnected;
    private ProgressThread mProgressThread;
    private SystemDevice mMainDevice;
    private ArrayList<BaseInfoFragment> baseInfoFragments;
    private FecpCommand mKeepAlive;
    private Thread mServerThread = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_twopane);

        this.mConnected = false;
        //while connecting show progress bar.

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Attempting to Connect");
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.setIndeterminate(true);
        mProgress.show();
        mProgressThread = new ProgressThread(mProgress, 100, this);
        mProgressThread.start();

        //Attempt to connect to the Fecp controller
        try {
            //determine whether it is over usb or tcp
            Bundle bundleParameters = getIntent().getExtras();
            CommType comm = CommType.values()[bundleParameters.getInt("commInterface")];

            if(comm == CommType.USB)
            {
                this.mFitProCntrl = new FitProUsb(ItemListActivity.this, getIntent());
            }
            else if(comm == CommType.TCP)
            {
                String ipAddress = bundleParameters.getString("ipAddress");
                int port = bundleParameters.getInt("port");
                this.mFitProCntrl = new FitProTcp(ipAddress, port);
            }
            this.mServerThread = new Thread(this);
            this.mServerThread.start();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //Log.e("Connection Error", ex.getMessage());
            Toast.makeText(this, "Connection Failed",Toast.LENGTH_LONG).show();
            this.mProgressThread.stopProgress();
            //change intent back to the connect main menu
            Intent ConnectionActivity = new Intent(this.getApplicationContext(), ConnectionActivity.class);
            startActivity(ConnectionActivity);
            finish();
        }


    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        try {
            mFitProCntrl.initializeConnection(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Callback method from {@link MainInfoListFragmentControl.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {

        //load the fragment that we want to see. //pass a reference to the Fecp Controller

        // fragment transaction.
        Bundle arguments = new Bundle();
        BaseInfoFragment currentFrag = null;

        for (BaseInfoFragment baseInfoFragment : this.baseInfoFragments) {
            if(id == baseInfoFragment.getIdString())
            {
                currentFrag = baseInfoFragment;
            }
        }
        if(currentFrag == null)
        {
            currentFrag = new MainDeviceInfoFragment(this.mFitProCntrl);
        }

        arguments.putString(currentFrag.toString(), id);
        currentFrag.setArguments(arguments);

        getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, currentFrag);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, currentFrag)
                .commit();

    }

    /**
     * this method is called when the system is disconnected.
     */
    @Override
    public void systemDisconnected() {
        this.mConnected = false;
        Toast.makeText(this,"Connection Lost",Toast.LENGTH_LONG).show();

        Toast.makeText(this,"Connection Lost, or Failed",Toast.LENGTH_LONG).show();
        //change intent back to the connect main menu
        Intent ConnectionActivity = new Intent(this.getApplicationContext(), ConnectionActivity.class);
        startActivity(ConnectionActivity);
        finish();
    }

    /**
     * This is called after system is connected
     *
     * @param dev the System device that is connected.
     */
    @Override
    public void systemDeviceConnected(SystemDevice dev) {
        this.mConnected = true;
        mProgressThread.stopProgress();
        Toast.makeText(this,"Connection Successful",Toast.LENGTH_LONG).show();
        mMainDevice = this.mFitProCntrl.getSysDev();


        this.mFitProCntrl.addOnErrorEventListener(this);//notify users of any errors

        //get supported list of item we will be supporting
        this.baseInfoFragments = new ArrayList<BaseInfoFragment>();

        //always support main info, error, task
        baseInfoFragments.add(new MainDeviceInfoFragment(this.mFitProCntrl));
        baseInfoFragments.add(new TaskInfoFragment(this.mFitProCntrl));
        baseInfoFragments.add(new ErrorFragment(this.mFitProCntrl));
        baseInfoFragments.add(new UserDataFragment(this.mFitProCntrl));//variety of items

        if(this.mMainDevice.containsDevice(DeviceId.GRADE))
        {
            baseInfoFragments.add(new InclineDeviceFragment(this.mFitProCntrl));
        }

        if(this.mMainDevice.containsDevice(DeviceId.SPEED))
        {
            baseInfoFragments.add(new SpeedDeviceFragment(this.mFitProCntrl));
        }

        //add supported Fragments here.

        if (findViewById(R.id.item_detail_container) != null) {

            ((MainInfoListFragmentControl)getSupportFragmentManager()
                    .findFragmentById(R.id.main_device_fragment))
                    .addSupportedFragments(baseInfoFragments);


            ((MainInfoListFragmentControl) getSupportFragmentManager()
                    .findFragmentById(R.id.main_device_fragment))
                    .setActivateOnItemClick(true);
        }


    }

    /**
     * This will be called when the communication layer is connected. this is a lower level of
     * communication notification.
     */
    @Override
    public void systemCommunicationConnected() {

    }

    /**
     * This will notify anyone that an error has occurred with the system
     *
     * @param error the error that occurred.
     */
    @Override
    public void onErrorEventListener(SystemError error) {
        Toast.makeText(this,error.toString(),Toast.LENGTH_LONG).show();
    }

    /**
     * Handles the reply from the device
     *
     * @param cmd the command that was sent.
     */
    @Override
    public void onCommandReceived(Command cmd) {
        //currently do nothing
    }
}
