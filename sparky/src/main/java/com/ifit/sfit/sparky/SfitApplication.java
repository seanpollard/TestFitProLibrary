package com.ifit.sfit.sparky;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ifit.sfit.sparky.fragments.BaseFragment;
import com.ifit.sfit.sparky.fragments.DebugFragment;
import com.ifit.sfit.sparky.fragments.DmkFragment;
import com.ifit.sfit.sparky.fragments.PauseFragment;
import com.ifit.sfit.sparky.fragments.ResultsFragment;
import com.ifit.sfit.sparky.idle.IdleFragment;
import com.ifit.sfit.sparky.running.TrackFragment;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.FitProTcp;
import com.ifit.sparky.fecp.FitProUsb;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.ConnectionDevice;
import com.ifit.sparky.fecp.communication.ServerDataCallback;
import com.ifit.sparky.fecp.communication.SystemStatusListener;
import com.ifit.sparky.fecp.communication.TcpConnectionDevice;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.PortalDeviceSts;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.util.List;
import java.util.TreeMap;


public class SfitApplication extends Activity implements View.OnClickListener, SystemStatusListener, OnCommandReceivedListener, CommInterface.ScanSystemListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, ServerDataCallback {

    private Button mScanButton;
    private ListView deviceListView;
    private Toast mToast;
    private FecpCommand mSystemStopCmd;
    private SFitSysCntrl mSFitSysCntrl;
    private BaseFragment mCurrentFragment;
    private List<ConnectionDevice> mConnectDevices;
    private boolean mUsbAttemptComplete = false;
    private boolean mTcpScanAttemptComplete = false;
    private boolean mEnableCommStats = false;
    private boolean mEnableServerStats = false;
    private ArrayAdapter<String> mListViewAdapter;
    private ModeId currentTabletMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connection);

        mScanButton = (Button) findViewById(R.id.buttonScan);
        this.deviceListView = (ListView)findViewById(R.id.deviceListView);
        mScanButton.setEnabled(false);
        mScanButton.setOnClickListener(this);

        this.mListViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        deviceListView.setAdapter(this.mListViewAdapter);
        deviceListView.setOnItemSelectedListener(this);
        deviceListView.setOnItemClickListener(this);
        deviceListView.setVisibility(View.VISIBLE);//enable the view of the list
        this.currentTabletMode = ModeId.UNKNOWN;

//        this.mScannerFitPro = new TcpComm();
        FitProTcp.scanForSystems(this);
//        this.mScannerFitPro.scanForSystems(this);

        try {
            FecpController tempCntrl;
            tempCntrl = new FitProUsb(getApplicationContext(), getIntent());
            this.mSFitSysCntrl = new SFitSysCntrl(tempCntrl);
            tempCntrl.initializeConnection(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Perform any final cleanup before an activity is destroyed.  This can
     * happen either because the activity is finishing (someone called
     * {@link #finish} on it, or because the system is temporarily destroying
     * this instance of the activity to save space.  You can distinguish
     * between these two scenarios with the {@link #isFinishing} method.
     * <p/>
     * <p><em>Note: do not count on this method being called as a place for
     * saving data! For example, if an activity is editing data in a content
     * provider, those edits should be committed in either {@link #onPause} or
     * {@link #onSaveInstanceState}, not here.</em> This method is usually implemented to
     * free resources like threads that are associated with an activity, so
     * that a destroyed activity does not leave such things around while the
     * rest of its application is still running.  There are situations where
     * the system will simply kill the activity's hosting process without
     * calling this method (or any others) in it, so it should not be used to
     * do things that are intended to remain around after the process goes
     * away.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onPause
     * @see #onStop
     * @see #finish
     * @see #isFinishing
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //kill all threads
        if(mToast != null)
        {
            mToast.cancel();
        }
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into
     * the background, but has not (yet) been killed.  The counterpart to
     * {@link #onResume}.
     * <p/>
     * <p>When activity B is launched in front of activity A, this callback will
     * be invoked on A.  B will not be created until A's {@link #onPause} returns,
     * so be sure to not do anything lengthy here.
     * <p/>
     * <p>This callback is mostly used for saving any persistent state the
     * activity is editing, to present a "edit in place" model to the user and
     * making sure nothing is lost if there are not enough resources to start
     * the new activity without first killing this one.  This is also a good
     * place to do things like stop animations and other things that consume a
     * noticeable amount of CPU in order to make the switch to the next activity
     * as fast as possible, or to close resources that are exclusive access
     * such as the camera.
     * <p/>
     * <p>In situations where the system needs more memory it may kill paused
     * processes to reclaim resources.  Because of this, you should be sure
     * that all of your state is saved by the time you return from
     * this function.  In general {@link #onSaveInstanceState} is used to save
     * per-instance state in the activity and this method is used to store
     * global persistent data (in content providers, files, etc.)
     * <p/>
     * <p>After receiving this call you will usually receive a following call
     * to {@link #onStop} (after the next activity has been resumed and
     * displayed), however in some cases there will be a direct call back to
     * {@link #onResume} without going through the stopped state.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onResume
     * @see #onSaveInstanceState
     * @see #onStop
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(mToast != null) {
                        mToast.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sfit_application, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.enable_comm_stats_settings) {
            if(mEnableServerStats)
            {
                mEnableServerStats = false;
            }
            mEnableCommStats = !mEnableCommStats;
            return true;
        }
        else if(id == R.id.enable_server_stats_settings) {
            if(mEnableCommStats)
            {
                mEnableCommStats = false;
            }
            mEnableServerStats = !mEnableServerStats;
            return true;
        }
        else if(id == R.id.stop_system_settings) {
            //send command to stop the treadmill
           try {
               //check which mode we are currently in
               ModeId currentMode = ((ModeConverter)this.mSFitSysCntrl.getFitProCntrl().getSysDev().getCurrentSystemData().get(BitFieldId.WORKOUT_MODE)).getMode();
               if(currentMode == ModeId.RUNNING)
               {
                   ((WriteReadDataCmd)this.mSystemStopCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);

               }
               else if(currentMode == ModeId.PAUSE || currentMode == ModeId.RESULTS)
               {
                   ((WriteReadDataCmd)this.mSystemStopCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.IDLE);

               }
               else
               {
                   return super.onOptionsItemSelected(item);
               }

               this.mSFitSysCntrl.getFitProCntrl().addCmd(this.mSystemStopCmd);

           } catch (Exception e) {
               e.printStackTrace();
           }

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        if(v == this.mScanButton)
        {
            FitProTcp.scanForSystems(this);
            this.mScanButton.setEnabled(false);
        }
    }

    /**
     * this method is called when the system is disconnected.
     */
    @Override
    public void systemDisconnected() {

        //change display back to the original screen
        this.mEnableCommStats = false;
        this.mEnableServerStats = false;
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
            if(this.mSFitSysCntrl == null || this.mSFitSysCntrl.getFitProCntrl().getCommType() == CommType.USB)//usb failed
            {
                //if only one TCP Server device try to connect to that
                mUsbAttemptComplete = true;

                if(mTcpScanAttemptComplete)
                {
                    //TODO display List of devices
                    handleScanResults();
                }
            }
            else if(this.mSFitSysCntrl.getFitProCntrl().getCommType() == CommType.TCP)//Tcp failed
            {
                //TCP connection to device failed, offer to re connect
                //Device Lost
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mScanButton.setEnabled(true);

                    }
                });

            }
            return;
        }

        this.mEnableCommStats = false;
        this.mEnableServerStats = false;
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

        if(this.mSFitSysCntrl.getFitProCntrl().getCommType() == CommType.USB)//usb passed
        {
            //if only one TCP Server device try to connect to that
            mUsbAttemptComplete = true;
        }

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
        this.mSFitSysCntrl.getInitialSysItems(this, 0, 0);
        //start the which interface we should be connecting to.
    }

    /**
     * This will be called when the communication layer is connected. this is a lower level of
     * communication notification.
     */
    @Override
    public void systemCommunicationConnected() {
        //nothing to do, implemented in fitpro layer
        this.mEnableCommStats = false;
        this.mEnableServerStats = false;
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
     * Handles the reply from the device
     *
     * @param cmd the command that was sent.
     */
    @Override
    public void onCommandReceived(final Command cmd) {

        HeaderThread tempHeaderThread = new HeaderThread(this, cmd);
        this.runOnUiThread(tempHeaderThread);
    }

    @Override
    public void onScanFinish(List<ConnectionDevice> devices) {
        //finish scanning for tcp devices
        mTcpScanAttemptComplete = true;
        mConnectDevices = devices;

        //when it finishes scanning, and the usb isn't connected
        if(mUsbAttemptComplete && !this.mSFitSysCntrl.getFitProCntrl().getIsConnected())
        {
            handleScanResults();
        }
    }


    private void handleScanResults()
    {
        //offer to re-scan
        if(mConnectDevices.size() == 0)
        {
            //no devices display re-scan button
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mScanButton.setEnabled(true);

                }
            });

        }
        else if(mConnectDevices.size() == 1)
        {
            //connect to that device if we aren't already connected to USB


            FecpController tempCntrl;
            try {
                tempCntrl = new FitProTcp(((TcpConnectionDevice)mConnectDevices.get(0)).getIpAddress());
//                    tempCntrl = new FitProTcp((TcpConnectionDevice)devices.get(0),this);
                this.mSFitSysCntrl = new SFitSysCntrl(tempCntrl);
                tempCntrl.initializeConnection(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListViewAdapter.clear();
                    //load all of the different devices
                    for (ConnectionDevice device : mConnectDevices) {
                        mListViewAdapter.add(device.toString());
                    }
                    mListViewAdapter.notifyDataSetChanged();

                }
            });
        }
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        FecpController tempCntrl;
        try {
            tempCntrl = new FitProTcp(((TcpConnectionDevice)this.mConnectDevices.get(position)).getIpAddress());
            this.mSFitSysCntrl = new SFitSysCntrl(tempCntrl);
            tempCntrl.initializeConnection(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.deviceListView.setVisibility(View.INVISIBLE);//remove it from view

    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FecpController tempCntrl;
        try {
            tempCntrl = new FitProTcp(((TcpConnectionDevice)this.mConnectDevices.get(position)).getIpAddress());
            this.mSFitSysCntrl = new SFitSysCntrl(tempCntrl);
            tempCntrl.initializeConnection(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.deviceListView.setVisibility(View.INVISIBLE);//remove it from view
    }

    /**
     * This will be called after every server message.
     * This will allaw for easier debug
     *
     * @param stats status about the Server
     */
    @Override
    public void serverStats(final String stats) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mEnableServerStats) {
                    if(null == mToast)
                    {
                        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                    }
                    mToast.setText(stats);

                    mToast.show();
                }
                else
                {
                    if(mToast != null) {
//                        mToast.cancel();
                    }
                }
            }
        });
    }

    private class HeaderThread implements Runnable
    {
        private Context mContext;
        private Command cmd;
        public HeaderThread(Context context, Command inCmd)
        {
            mContext = context;
            this.cmd = inCmd;
        }

        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run() {
            //display data stats
//            if(null == mToast || null == mToast.getView())
//            {
            if(mEnableCommStats) {
                if(null == mToast)
                {
                    mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
                mToast.setText(mSFitSysCntrl.getFitProCntrl().getCommunicationStats());

                mToast.show();
            }
            else
            {
                if(mToast != null) {
//                    mToast.cancel();
                }
            }
            //}
            //this.mToast = Toast.makeText(getApplicationContext(),mSFitSysCntrl.getFitProCntrl().getCommunicationStats(), Toast.LENGTH_LONG).show();

            //received initial items
            //todo when System Unit is done set the if metric
            //for now assuming metric
            TreeMap < BitFieldId, BitfieldDataConverter > cmdResults;
            if(cmd.getStatus().getStsId() == StatusId.FAILED)
            {
                return;
            }

            //if running mode, just join the party
            if(cmd.getCmdId() == CommandId.WRITE_READ_DATA || cmd.getCmdId() == CommandId.PORTAL_DEV_LISTEN)
            {

                if(cmd.getCmdId() == CommandId.PORTAL_DEV_LISTEN)
                {
                    cmdResults = ((PortalDeviceSts)cmd.getStatus()).getmSysDev().getCurrentSystemData();
                }
                else {
                    WriteReadDataSts sts = (WriteReadDataSts) cmd.getStatus();
                    cmdResults = sts.getResultData();
                }

                if(cmdResults.containsKey(BitFieldId.WORKOUT_MODE))
                {
                    ModeId currentMode = ((ModeConverter)cmdResults.get(BitFieldId.WORKOUT_MODE)).getMode();
                    BaseFragment fragment;

                    if(currentMode != currentTabletMode)
                    {
                        if(currentMode == ModeId.RUNNING) {
                            fragment = new TrackFragment(mSFitSysCntrl);
                        }
                        else if(currentMode == ModeId.RESULTS) {
                            fragment = new ResultsFragment(mSFitSysCntrl);
                        }
                        else if(currentMode == ModeId.PAUSE) {
                            fragment = new PauseFragment(mSFitSysCntrl);
                        }
                        else if(currentMode == ModeId.DEBUG) {
                            fragment = new DebugFragment(mSFitSysCntrl);
                        }
                        else if(currentMode == ModeId.DMK) {
                            fragment = new DmkFragment(mSFitSysCntrl);
                        }
                        else
                        {
                            //assume Idle
                            fragment = new IdleFragment(mSFitSysCntrl);
                        }
                        currentTabletMode = currentMode;

                        FragmentManager manager =  getFragmentManager();
                        FragmentTransaction fragTransaction = manager.beginTransaction();
                        if(mCurrentFragment != null) {
                            fragTransaction.remove(mCurrentFragment);
                            fragTransaction.add(R.id.container, fragment);
                            fragTransaction.commit();
                            manager.popBackStack();//remove the Previous Fragment
                        }
                        else
                        {

                            mSFitSysCntrl.populatePeriodicSysItems((SfitApplication) mContext);
                            setContentView(R.layout.activity_sfit_application);
                            new HeaderViewCntrl(mContext, mSFitSysCntrl);
                            fragTransaction.add(R.id.container, fragment);
                            fragTransaction.commit();
                        }

                        mCurrentFragment = fragment;
                    }

                }
            }
        }

    }
}
