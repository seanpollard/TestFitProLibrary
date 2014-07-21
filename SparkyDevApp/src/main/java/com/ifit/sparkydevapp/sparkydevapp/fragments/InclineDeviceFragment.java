package com.ifit.sparkydevapp.sparkydevapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.GradeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ShortConverter;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;
import com.ifit.sparkydevapp.sparkydevapp.R;

import java.util.Set;
import java.util.TreeMap;


public class InclineDeviceFragment extends BaseInfoFragment implements OnCommandReceivedListener, Runnable, View.OnKeyListener, View.OnFocusChangeListener, View.OnClickListener{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "incline_dev_id";
    public static final String DISPLAY_STRING = "Incline Device";

    private Device mInclineDev;

    private FecpCommand mInclineInfoCmd;
    private FecpCommand mSetInclineCmd;
    private FecpCommand mInclineCalibrateCmd;

    private TextView mTextViewInclineValues;
    private TextView mTextViewInclineDevice;
    private TextView mTextViewInclineDetails;
    private TextView mCalStsTextView;
    private EditText mEditInclineText;
    private Button mCalibrateButton;
    private double mTargetIncline;
    private boolean mCalibrate;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     *
     * @param fecpCntrl
     */
    public InclineDeviceFragment(FecpController fecpCntrl) {
        super(fecpCntrl, InclineDeviceFragment.DISPLAY_STRING, InclineDeviceFragment.ARG_ITEM_ID);
        this.mTargetIncline = 0.0;
        this.mCalibrate = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        //assign all of the textviews and values we need
        this.mTextViewInclineDevice = ((TextView) rootView.findViewById(R.id.textViewInclineDevice));
        this.mTextViewInclineValues = ((TextView) rootView.findViewById(R.id.textViewInclineValues));
        this.mTextViewInclineDetails = ((TextView) rootView.findViewById(R.id.textViewInclineDetails));
        this.mCalStsTextView = ((TextView) rootView.findViewById(R.id.calStsTextView));
        this.mEditInclineText = ((EditText) rootView.findViewById(R.id.editInclineText));
        this.mCalibrateButton = ((Button) rootView.findViewById(R.id.calibrateButton));

        this.mEditInclineText.setOnKeyListener(this);
        this.mEditInclineText.setOnFocusChangeListener(this);
        this.mCalibrateButton.setOnClickListener(this);

        this.mInclineDev = this.mFecpCntrl.getSysDev().getSubDevice(DeviceId.GRADE);
        Set<BitFieldId> supportedBitfields;
        try {

            this.mInclineInfoCmd = new FecpCommand(this.mInclineDev.getCommand(CommandId.WRITE_READ_DATA), this, 0, 1000);//every 1 second
            this.mSetInclineCmd = new FecpCommand(this.mInclineDev.getCommand(CommandId.WRITE_READ_DATA));

            //check which bitfields are supported
            supportedBitfields = this.mInclineDev.getInfo().getSupportedBitfields();
            if(supportedBitfields.contains(BitFieldId.GRADE))
            {
                ((WriteReadDataCmd)this.mInclineInfoCmd.getCommand()).addReadBitField(BitFieldId.GRADE);
                ((WriteReadDataCmd)this.mSetInclineCmd.getCommand()).addWriteData(BitFieldId.GRADE, this.mTargetIncline);
            }

            if(supportedBitfields.contains(BitFieldId.MAX_GRADE))
            {
                ((WriteReadDataCmd)this.mInclineInfoCmd.getCommand()).addReadBitField(BitFieldId.MAX_GRADE);
            }

            if(supportedBitfields.contains(BitFieldId.MIN_GRADE))
            {
                ((WriteReadDataCmd)this.mInclineInfoCmd.getCommand()).addReadBitField(BitFieldId.MIN_GRADE);
            }

            if(supportedBitfields.contains(BitFieldId.TRANS_MAX))
            {
                ((WriteReadDataCmd)this.mInclineInfoCmd.getCommand()).addReadBitField(BitFieldId.TRANS_MAX);
            }

            if(supportedBitfields.contains(BitFieldId.ACTUAL_INCLINE))
            {
                ((WriteReadDataCmd)this.mInclineInfoCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_INCLINE);
            }

            if(this.mInclineDev.getCommandSet().containsKey(CommandId.CALIBRATE)) {

                this.mInclineCalibrateCmd = new FecpCommand(this.mInclineDev.getCommand(CommandId.CALIBRATE), this,0, 1000);//every 1 second

            }
        }
        catch (Exception ex)
        {
            Log.e("Initialize Incline Commands Failed", ex.getLocalizedMessage());
        }

                // Show the dummy content as text in a TextView.
        this.mTextViewInclineDevice.setText(this.mInclineDev.toString());
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
            this.mFecpCntrl.addCmd(this.mInclineInfoCmd);
        }
        catch (Exception ex)
        {
            Log.e("Initialize Incline Commands Failed", ex.getLocalizedMessage());
        }
    }

    /**
     * These are the commands that well be removed when we switch fragments
     */
    @Override
    public void deleteFragmentFecpCommands() {

        super.deleteFragmentFecpCommands();
        this.mFecpCntrl.removeCmd(this.mInclineInfoCmd);
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
        String valueString = "Current Incline= %";
        String detailString = "Details, ";
        String calStsString = "Calibration Sts:";



        commandData = ((WriteReadDataSts)this.mInclineInfoCmd.getCommand().getStatus()).getResultData();

        if(commandData.containsKey(BitFieldId.GRADE))
        {

            try
            {
                valueString += ((GradeConverter) commandData.get(BitFieldId.GRADE).getData()).getIncline() + "\n";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.ACTUAL_INCLINE))
        {

            try
            {
                valueString += "Actual Incline= %" +((GradeConverter) commandData.get(BitFieldId.ACTUAL_INCLINE).getData()).getIncline() + "\n";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        if(commandData.containsKey(BitFieldId.TRANS_MAX))
        {

            try
            {
                valueString += "Trans Max= " +((ShortConverter) commandData.get(BitFieldId.TRANS_MAX).getData()).getValue() + "\n";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            valueString += "Trans Max= " + "\n";
        }

        if(commandData.containsKey(BitFieldId.MAX_GRADE))
        {

            try
            {
                detailString += "Max= %" +((GradeConverter) commandData.get(BitFieldId.MAX_GRADE).getData()).getIncline() + " ";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.MIN_GRADE))
        {

            try
            {
                detailString += "Min= %" +((GradeConverter) commandData.get(BitFieldId.MIN_GRADE).getData()).getIncline();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        //update the calibration info
        if(this.mCalibrate && this.mInclineCalibrateCmd != null) {
            if (this.mInclineCalibrateCmd.getCommand().getStatus().getStsId() == StatusId.DONE) {
                //passed
                //remove command from the system.
                this.mFecpCntrl.removeCmd(this.mInclineCalibrateCmd);
                this.mCalibrate = false;
                calStsString += " Passed";

            } else if (this.mInclineCalibrateCmd.getCommand().getStatus().getStsId() == StatusId.IN_PROGRESS) {
                // in progress
                calStsString += " In Progress";
            } else if (this.mInclineCalibrateCmd.getCommand().getStatus().getStsId() == StatusId.FAILED) {
                //failed
                this.mFecpCntrl.removeCmd(this.mInclineCalibrateCmd);//may have issues with sending it multiple times
                this.mCalibrate = false;
                calStsString += " Failed";
            }
            this.mCalStsTextView.setText(calStsString);
        }
        //set the display values
        this.mTextViewInclineDetails.setText(detailString);
        this.mTextViewInclineValues.setText(valueString);
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
                inputStr = this.mEditInclineText.getText().toString();
                if (inputStr.isEmpty()) {
                    return false;
                }
                this.mTargetIncline = Double.parseDouble(inputStr);

                ((WriteReadDataCmd)this.mSetInclineCmd.getCommand()).addWriteData(BitFieldId.GRADE, this.mTargetIncline);
                this.mFecpCntrl.addCmd(this.mSetInclineCmd);//send the new speed down
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

                ((WriteReadDataCmd)this.mSetInclineCmd.getCommand()).addWriteData(BitFieldId.GRADE, this.mTargetIncline);
                this.mFecpCntrl.addCmd(this.mSetInclineCmd);
            }
            catch (Exception ex)
            {
                Log.e("Update Target Incline Commands Failed", ex.getLocalizedMessage());

            }
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //update the calibrate command
        if(this.mCalibrate)//already calibrating
        {
            //don't do anything with the button
        }
        else
        {
            //add command to system.
            this.mCalibrate = true;
            try {
                this.mFecpCntrl.addCmd(this.mInclineCalibrateCmd);
                this.mCalStsTextView.setText("Calibration Sts:");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
