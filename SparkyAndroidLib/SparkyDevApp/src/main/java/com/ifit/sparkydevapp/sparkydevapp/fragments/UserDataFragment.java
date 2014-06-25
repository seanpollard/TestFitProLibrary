package com.ifit.sparkydevapp.sparkydevapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.AudioSourceConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.AudioSourceId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ByteConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.CaloriesConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WeightConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WorkoutConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.WorkoutId;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;
import com.ifit.sparkydevapp.sparkydevapp.R;

import java.util.Set;
import java.util.TreeMap;


public class UserDataFragment extends BaseInfoFragment implements OnCommandReceivedListener, Runnable{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "user_data_id";
    public static final String DISPLAY_STRING = "User Data";

    private FecpCommand mUserDataCmd;

    private TextView mTextViewUserData;
    private TextView mTextViewAge;
    private TextView mTextViewFan;
    private TextView mTextViewVolume;
    private TextView mTextViewPulse;
    private TextView mTextViewCalories;
    private TextView mTextViewAudioSource;
    private TextView mTextViewWorkout;
    private TextView mTextViewWeight;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     *
     * @param fecpCntrl
     */
    public UserDataFragment(FecpController fecpCntrl) {
        super(fecpCntrl, UserDataFragment.DISPLAY_STRING, UserDataFragment.ARG_ITEM_ID);
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
        this.mTextViewUserData = ((TextView) rootView.findViewById(R.id.textViewUserData));
        this.mTextViewAge = ((TextView) rootView.findViewById(R.id.textViewAge));
        this.mTextViewFan = ((TextView) rootView.findViewById(R.id.textViewFan));
        this.mTextViewVolume = ((TextView) rootView.findViewById(R.id.textViewVolume));
        this.mTextViewPulse = ((TextView) rootView.findViewById(R.id.textViewPulse));
        this.mTextViewCalories = ((TextView) rootView.findViewById(R.id.textViewCalories));
        this.mTextViewAudioSource = ((TextView) rootView.findViewById(R.id.textViewAudioSource));
        this.mTextViewWorkout = ((TextView) rootView.findViewById(R.id.textViewWorkout));
        this.mTextViewWeight = ((TextView) rootView.findViewById(R.id.textViewWeight));





        Set<BitFieldId> supportedBitfields;
        try {

            this.mUserDataCmd = new FecpCommand(this.mFecpCntrl.getSysDev().getCommand(CommandId.WRITE_READ_DATA), this, 0, 1000);//every 1 second

            //check which bitfields are supported
            supportedBitfields = this.mFecpCntrl.getSysDev().getInfo().getSupportedBitfields();
            if(supportedBitfields.contains(BitFieldId.AGE))
            {
                ((WriteReadDataCmd)this.mUserDataCmd.getCommand()).addReadBitField(BitFieldId.AGE);
            }
            else
            {
                this.mTextViewAge.setText("Age: N/A");
            }

            if(supportedBitfields.contains(BitFieldId.FAN_SPEED))
            {
                ((WriteReadDataCmd)this.mUserDataCmd.getCommand()).addReadBitField(BitFieldId.FAN_SPEED);
            }
            else
            {
                this.mTextViewFan.setText("Fan: N/A");
            }

            if(supportedBitfields.contains(BitFieldId.VOLUME))
            {
                ((WriteReadDataCmd)this.mUserDataCmd.getCommand()).addReadBitField(BitFieldId.VOLUME);
            }
            else
            {
                this.mTextViewVolume.setText("Volume: N/A");
            }

            if(supportedBitfields.contains(BitFieldId.PULSE))
            {
                ((WriteReadDataCmd)this.mUserDataCmd.getCommand()).addReadBitField(BitFieldId.PULSE);
            }
            else
            {
                this.mTextViewPulse.setText("Pulse: N/A");
            }

            if(supportedBitfields.contains(BitFieldId.CALORIES))
            {
                ((WriteReadDataCmd)this.mUserDataCmd.getCommand()).addReadBitField(BitFieldId.CALORIES);
            }
            else
            {
                this.mTextViewCalories.setText("Calories: N/A");
            }

            if(supportedBitfields.contains(BitFieldId.AUDIO_SOURCE))
            {
                ((WriteReadDataCmd)this.mUserDataCmd.getCommand()).addReadBitField(BitFieldId.AUDIO_SOURCE);
            }
            else
            {
                this.mTextViewAudioSource.setText("Audio Source: N/A");
            }

            if(supportedBitfields.contains(BitFieldId.WORKOUT))
            {
                ((WriteReadDataCmd)this.mUserDataCmd.getCommand()).addReadBitField(BitFieldId.WORKOUT);
            }
            else
            {
                this.mTextViewWorkout.setText("Workout: N/A");
            }

            if(supportedBitfields.contains(BitFieldId.WEIGHT))
            {
                ((WriteReadDataCmd)this.mUserDataCmd.getCommand()).addReadBitField(BitFieldId.WEIGHT);
            }
            else
            {
                this.mTextViewWeight.setText("Weight: N/A");
            }


        }
        catch (Exception ex)
        {
            Log.e("Initialize User Data Commands Failed", ex.getLocalizedMessage());
        }

                // Show the dummy content as text in a TextView.
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
            this.mFecpCntrl.addCmd(this.mUserDataCmd);
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
        this.mFecpCntrl.removeCmd(this.mUserDataCmd);
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

        commandData = ((WriteReadDataSts)this.mUserDataCmd.getCommand().getStatus()).getResultData();

        if(commandData.containsKey(BitFieldId.AGE))
        {

            try
            {
                int age = ((ByteConverter) commandData.get(BitFieldId.AGE).getData()).getValue();
                this.mTextViewAge.setText("Age: "+ age);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.FAN_SPEED))
        {

            try
            {
                int fanSpeed = ((ByteConverter) commandData.get(BitFieldId.FAN_SPEED).getData()).getValue();
                this.mTextViewFan.setText("Fan: %"+ fanSpeed);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.VOLUME))
        {

            try
            {
                int volume = ((ByteConverter) commandData.get(BitFieldId.VOLUME).getData()).getValue();
                this.mTextViewVolume.setText("Volume: %"+ volume);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.PULSE))
        {

            try
            {
                int pulse = ((ByteConverter) commandData.get(BitFieldId.PULSE).getData()).getValue();
                this.mTextViewPulse.setText("Pulse: "+ pulse);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.CALORIES))
        {

            try
            {
                double calories = ((CaloriesConverter) commandData.get(BitFieldId.CALORIES).getData()).getCalories();
                this.mTextViewCalories.setText("Calories: "+ calories);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.AUDIO_SOURCE))
        {

            try
            {
                AudioSourceId audioSrc = ((AudioSourceConverter) commandData.get(BitFieldId.AUDIO_SOURCE).getData()).getAudioSource();
                this.mTextViewAudioSource.setText("Audio Source: "+ audioSrc.getDescription());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.WORKOUT))
        {

            try
            {
                WorkoutId wkId = ((WorkoutConverter) commandData.get(BitFieldId.WORKOUT).getData()).getWorkout();
                this.mTextViewWorkout.setText("Workout: "+ wkId.getDescription());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.WEIGHT))
        {

            try
            {
                double weight = ((WeightConverter)commandData.get(BitFieldId.WEIGHT).getData()).getWeight();
                this.mTextViewWeight.setText("Weight: "+ weight + " kg");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

    }

}
