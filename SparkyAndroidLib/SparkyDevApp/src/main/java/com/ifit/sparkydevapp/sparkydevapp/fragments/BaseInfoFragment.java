package com.ifit.sparkydevapp.sparkydevapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.LongConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeConverter;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;
import com.ifit.sparkydevapp.sparkydevapp.R;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link com.ifit.sparkydevapp.sparkydevapp.ItemListActivity}
 * in two-pane mode (on tablets)
 */
public abstract class BaseInfoFragment extends Fragment implements OnCommandReceivedListener, Runnable {

    protected FecpController mFecpCntrl;
    private String mIdString;
    private String mDisplayString;
    private TextView mTextViewSystemInfo;
    private FecpCommand mSysInfoCmd;//info we need about the system

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BaseInfoFragment(FecpController fecpCntrl, String displayString, String itemId) {
        this.mFecpCntrl = fecpCntrl;
        this.mIdString = itemId;
        this.mDisplayString = displayString;
        this.mTextViewSystemInfo = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView;

        if(this.mIdString == SpeedDeviceFragment.ARG_ITEM_ID) {
            rootView = inflater.inflate(R.layout.speed_device, container, false);
        }
        else if(this.mIdString == InclineDeviceFragment.ARG_ITEM_ID) {
            rootView = inflater.inflate(R.layout.incline_device, container, false);
        }
        else if(this.mIdString == UserDataFragment.ARG_ITEM_ID) {
            rootView = inflater.inflate(R.layout.user_data, container, false);
        }
        else
        {
            return null;
        }

        this.mTextViewSystemInfo = ((TextView) rootView.findViewById(R.id.textViewSystemInfo));

        try {

            this.mSysInfoCmd = new FecpCommand(this.mFecpCntrl.getSysDev().getCommand(CommandId.WRITE_READ_DATA), this, 0, 1000);//every 1 second

            if(this.mFecpCntrl.getSysDev().getInfo().getSupportedBitfields().contains(BitFieldId.WORKOUT_MODE))
            {
                ((WriteReadDataCmd)this.mSysInfoCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
            }

            if(this.mFecpCntrl.getSysDev().getInfo().getSupportedBitfields().contains(BitFieldId.RUNNING_TIME))
            {
                ((WriteReadDataCmd)this.mSysInfoCmd.getCommand()).addReadBitField(BitFieldId.RUNNING_TIME);
            }

            if(this.mFecpCntrl.getSysDev().getInfo().getSupportedBitfields().contains(BitFieldId.DISTANCE))
            {
                ((WriteReadDataCmd)this.mSysInfoCmd.getCommand()).addReadBitField(BitFieldId.DISTANCE);
            }

        }
        catch (Exception ex)
        {
            Log.e("Initialize Speed Commands Failed", ex.getLocalizedMessage());
        }



        return rootView;
    }

    /**
     * Handles the reply from the device
     *
     * @param cmd the command that was sent.
     */
    @Override
    public void onCommandReceived(Command cmd) {


        if(cmd.getCmdId() == this.mSysInfoCmd.getCommand().getCmdId() && cmd.getDevId() == this.mSysInfoCmd.getCommand().getDevId()) {
            this.getActivity().runOnUiThread(new Thread(this));
        }
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        TreeMap<BitFieldId, BitfieldDataConverter> commandData;

        if(this.mSysInfoCmd.getCommand().getCmdId() != CommandId.WRITE_READ_DATA)
        {
            return;//nothing to do
        }

        commandData = ((WriteReadDataSts)this.mSysInfoCmd.getCommand().getStatus()).getResultData();
        String systemString = "System Info, ";

        //get System Information first
        if(commandData.containsKey(BitFieldId.WORKOUT_MODE))
        {
            try
            {
                systemString  += "Mode: "+((ModeConverter) commandData.get(BitFieldId.WORKOUT_MODE).getData()).getMode().getDescription();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.RUNNING_TIME))
        {
            try
            {
                int seconds = ((LongConverter) commandData.get(BitFieldId.RUNNING_TIME).getData()).getValue();
                int day = (int) TimeUnit.SECONDS.toDays(seconds);
                long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
                long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
                long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
                systemString  += " time: " +hours+":"+minute+":"+ second;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.DISTANCE))
        {
            try
            {
                int distance = ((LongConverter) commandData.get(BitFieldId.DISTANCE).getData()).getValue();

                systemString  += " distance: " +distance+" Meters";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        this.mTextViewSystemInfo.setText(systemString);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.deleteFragmentFecpCommands();
    }

    /**
     * These are the commands that we will be using on the startup
     */
    public void addFragmentFecpCommands()
    {
        try {
            this.mFecpCntrl.addCmd(this.mSysInfoCmd);
        }
        catch (Exception ex)
        {
            Log.e("Initialize Base Commands Commands Failed", ex.getLocalizedMessage());
        }
    }

    /**
     * These are the commands that well be removed when we switch fragments
     */
    public void deleteFragmentFecpCommands()
    {
        this.mFecpCntrl.removeCmd(this.mSysInfoCmd);

    }

    @Override
    public String toString() {
        return this.mDisplayString;
    }

    public String getIdString()
    {
        return this.mIdString;
    }
}
