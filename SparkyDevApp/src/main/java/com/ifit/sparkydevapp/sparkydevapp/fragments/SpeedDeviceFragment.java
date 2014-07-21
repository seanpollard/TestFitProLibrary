package com.ifit.sparkydevapp.sparkydevapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;
import com.ifit.sparkydevapp.sparkydevapp.R;

import java.util.Set;
import java.util.TreeMap;


public class SpeedDeviceFragment extends BaseInfoFragment implements OnCommandReceivedListener, Runnable, View.OnKeyListener, View.OnFocusChangeListener{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "speed_dev_id";
    public static final String DISPLAY_STRING = "Speed Device";

    private Device mSpeedDev;

    private FecpCommand mSpeedInfoCmd;
    private FecpCommand mSpeedSetCmd;//only when it changes
    private FecpCommand mSpeedCalibrateCmd;

    private TextView mTextViewSpeedValues;
    private TextView mTextViewSpeedDevice;
    private TextView mTextViewSpeedDetails;
    private EditText mEditSpeedText;
    private double mTargetSpeed;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     *
     * @param fecpCntrl
     */
    public SpeedDeviceFragment(FecpController fecpCntrl) {
        super(fecpCntrl, SpeedDeviceFragment.DISPLAY_STRING, SpeedDeviceFragment.ARG_ITEM_ID);
        this.mTargetSpeed = 0.0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        //rootView = inflater.inflate(R.layout.speed_device, container, false);
        //assign all of the textviews and values we need
        this.mTextViewSpeedDevice = ((TextView) rootView.findViewById(R.id.textViewSpeedDevice));
        this.mTextViewSpeedValues = ((TextView) rootView.findViewById(R.id.textViewSpeedValues));
        this.mTextViewSpeedDetails = ((TextView) rootView.findViewById(R.id.textViewSpeedDetails));

        this.mEditSpeedText = ((EditText) rootView.findViewById(R.id.editSpeedText));
        this.mEditSpeedText.setOnKeyListener(this);
        this.mEditSpeedText.setOnFocusChangeListener(this);

        this.mSpeedDev = this.mFecpCntrl.getSysDev().getSubDevice(DeviceId.SPEED);
        Set<BitFieldId> supportedBitfields;
        try {

            this.mSpeedInfoCmd = new FecpCommand(this.mSpeedDev.getCommand(CommandId.WRITE_READ_DATA), this, 0, 1000);//every 1 second
            this.mSpeedSetCmd = new FecpCommand(this.mSpeedDev.getCommand(CommandId.WRITE_READ_DATA));//every speed change
            //check which bitfields are supported
            supportedBitfields = this.mSpeedDev.getInfo().getSupportedBitfields();
            if(supportedBitfields.contains(BitFieldId.KPH))
            {
                ((WriteReadDataCmd)this.mSpeedInfoCmd.getCommand()).addReadBitField(BitFieldId.KPH);
                ((WriteReadDataCmd)this.mSpeedSetCmd.getCommand()).addWriteData(BitFieldId.KPH, this.mTargetSpeed);
            }

            if(supportedBitfields.contains(BitFieldId.MAX_KPH))
            {
                ((WriteReadDataCmd)this.mSpeedInfoCmd.getCommand()).addReadBitField(BitFieldId.MAX_KPH);
            }

            if(supportedBitfields.contains(BitFieldId.MIN_KPH))
            {
                ((WriteReadDataCmd)this.mSpeedInfoCmd.getCommand()).addReadBitField(BitFieldId.MIN_KPH);
            }

            if(supportedBitfields.contains(BitFieldId.ACTUAL_KPH))
            {
                ((WriteReadDataCmd)this.mSpeedInfoCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_KPH);
            }


        }
        catch (Exception ex)
        {
            Log.e("Initialize Speed Commands Failed", ex.getLocalizedMessage());
        }

                // Show the dummy content as text in a TextView.
        this.mTextViewSpeedDevice.setText(this.mSpeedDev.toString());
        this.addFragmentFecpCommands();
        return rootView;
    }


    /**
     * These are the commands that we will be using on the startup
     */
    @Override
    public void addFragmentFecpCommands() {
        super.addFragmentFecpCommands();
        try {
            this.mFecpCntrl.addCmd(this.mSpeedInfoCmd);
        }
        catch (Exception ex)
        {
            Log.e("Initialize Speed Commands Failed", ex.getLocalizedMessage());
        }
    }

    /**
     * These are the commands that well be removed when we switch fragments
     */
    @Override
    public void deleteFragmentFecpCommands() {

        super.deleteFragmentFecpCommands();
        this.mFecpCntrl.removeCmd(this.mSpeedInfoCmd);
    }

    /**
     * Handles the reply from the device
     *
     * @param cmd the command that was sent.
     */
    @Override
    public void onCommandReceived(Command cmd) {
        super.onCommandReceived(cmd);
        this.getActivity().runOnUiThread(new Thread(this));
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        super.run();
        TreeMap<BitFieldId, BitfieldDataConverter> commandData;

        commandData = ((WriteReadDataSts)this.mSpeedInfoCmd.getCommand().getStatus()).getResultData();
        String valueString = "Current Speed= ";
        String detailString = "Details, ";

        commandData = ((WriteReadDataSts)this.mSpeedInfoCmd.getCommand().getStatus()).getResultData();
        if(commandData.containsKey(BitFieldId.KPH))
        {

            try
            {
                double tempSpeed = ((SpeedConverter) commandData.get(BitFieldId.KPH).getData()).getSpeed();
                this.mTargetSpeed =tempSpeed;
                valueString += tempSpeed + " kph\n";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.ACTUAL_KPH))
        {

            try
            {
                valueString += "Actual Speed= " +((SpeedConverter) commandData.get(BitFieldId.ACTUAL_KPH).getData()).getSpeed() + " kph\n";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.MAX_KPH))
        {

            try
            {
                detailString += "Max= " +((SpeedConverter) commandData.get(BitFieldId.MAX_KPH).getData()).getSpeed() + " kph ";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.MIN_KPH))
        {

            try
            {
                detailString += "Min= " +((SpeedConverter) commandData.get(BitFieldId.MIN_KPH).getData()).getSpeed() + " kph ";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        //set the display values
        this.mTextViewSpeedDetails.setText(detailString);
        this.mTextViewSpeedValues.setText(valueString);
    }

    /**
     * Called when a hardware key is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     * <p>Key presses in software keyboards will generally NOT trigger this method,
     * although some may elect to do so in some situations. Do not assume a
     * software input method has to be key-based; even if it is, it may use key presses
     * in a different way than you expect, so there is no way to reliably catch soft
     * input key presses.
     *
     * @param v       The view the key has been dispatched to.
     * @param keyCode The code for the physical key that was pressed
     * @param event   The KeyEvent object containing full information about
     *                the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            try {
                String inputStr = "";
                inputStr = this.mEditSpeedText.getText().toString();
                if (inputStr.isEmpty()) {
                    return false;
                }
                this.mTargetSpeed = Double.parseDouble(inputStr);

                ((WriteReadDataCmd)this.mSpeedSetCmd.getCommand()).addWriteData(BitFieldId.KPH, this.mTargetSpeed);
                this.mFecpCntrl.addCmd(this.mSpeedSetCmd);//send the new speed down
            }
            catch (Exception numEx) {
                numEx.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**
     * Called when the focus state of a view has changed.
     *
     * @param v        The view whose state has changed.
     * @param hasFocus The new focus state of v.
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus)
        {
            try{

                ((WriteReadDataCmd)this.mSpeedSetCmd.getCommand()).addWriteData(BitFieldId.KPH, this.mTargetSpeed);
                this.mFecpCntrl.addCmd(this.mSpeedSetCmd);//send the new speed down
            }
            catch (Exception ex)
            {
                Log.e("Update Target KPH Commands Failed", ex.getLocalizedMessage());

            }
        }
    }
}
