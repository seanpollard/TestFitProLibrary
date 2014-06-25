/**
 * This will control the Top Display.
 * @author Levi.Balling
 * @date 5/22/2014
 * @version 1
 * This will specifically hold the values that are specific to the system.
 */
        package com.ifit.sfit.sparky;


        import android.app.Activity;
        import android.content.Context;
        import android.widget.TextView;

        import com.ifit.sparky.fecp.FecpCommand;
        import com.ifit.sparky.fecp.OnCommandReceivedListener;
        import com.ifit.sparky.fecp.interpreter.device.SystemConfiguration;
        import com.ifit.sparky.fecp.SystemDevice;
        import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
        import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
        import com.ifit.sparky.fecp.interpreter.bitField.converter.CaloriesConverter;
        import com.ifit.sparky.fecp.interpreter.bitField.converter.GradeConverter;
        import com.ifit.sparky.fecp.interpreter.bitField.converter.LongConverter;
        import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
        import com.ifit.sparky.fecp.interpreter.command.Command;
        import com.ifit.sparky.fecp.interpreter.command.CommandId;
        import com.ifit.sparky.fecp.interpreter.device.DeviceId;

        import java.util.TreeMap;

public class HeaderViewCntrl implements OnCommandReceivedListener{

    private SFitSysCntrl mSFitSysCntrl;
    private Context mContext;
    //First Cell
    private TextView mTopLeftLabelTextView;
    private TextView mTopLeftValueTextView;

    //Second Cell
    private TextView mSecondValueTextView;

    //Third Cell
    private TextView mThirdValueTextView;

    //Fourth Cell
    private TextView mFourthValueTextView;

    //Fifth Cell
    private TextView mTopRightLabelTextView;
    private TextView mTopRightValueTextView;

    /**
     * This Constructor should only be called when the SFitApplication is loading
     * @param context the activity with the View that holds the Header Display
     */
    public HeaderViewCntrl(Context context, SFitSysCntrl fitSysCntrl)
    {
        this.mContext = context;
        this.mSFitSysCntrl = fitSysCntrl;

        this.mTopLeftLabelTextView = (TextView)((Activity)mContext).findViewById(R.id.txt_incline);
        this.mTopLeftValueTextView = (TextView)((Activity)mContext).findViewById(R.id.txt_curr_incline);

        this.mTopRightLabelTextView = (TextView)((Activity)mContext).findViewById(R.id.txt_speed);
        this.mTopRightValueTextView = (TextView)((Activity)mContext).findViewById(R.id.txt_curr_speed);

        this.mSecondValueTextView = (TextView)((Activity)mContext).findViewById(R.id.left_statistic);

        this.mThirdValueTextView = (TextView)((Activity)mContext).findViewById(R.id.center_statistic);

        this.mFourthValueTextView = (TextView)((Activity)mContext).findViewById(R.id.right_statistic);

        //change what is displayed based on what type of machine it is

        SystemDevice sysDev = this.mSFitSysCntrl.getFitProCntrl().getSysDev();
        DeviceId dev = this.mSFitSysCntrl.getFitProCntrl().getSysDev().getInfo().getDevId();

        if(this.mSFitSysCntrl.isMetric())
        {
            //convert the labels to be metric
            this.mTopRightLabelTextView.setText("SPEED(KPH)");
        }

        if(sysDev.getSysDevInfo().getConfig() == SystemConfiguration.PORTAL_TO_MASTER || sysDev.getSysDevInfo().getConfig() == SystemConfiguration.PORTAL_TO_SLAVE)
        {
            FecpCommand cmd = this.mSFitSysCntrl.getReadCommand(BitFieldId.KPH);
            if(cmd != null)
            {
                cmd.addOnCommandReceived(this);
            }
            return;

        }

        if(dev == DeviceId.TREADMILL || dev == DeviceId.INCLINE_TRAINER)
        {
            //leave to the default
            //add this to the list of on command receiver
            FecpCommand cmd = this.mSFitSysCntrl.getReadCommand(BitFieldId.KPH);
            if(cmd != null)
            {
                cmd.addOnCommandReceived(this);
            }

            //if kph is supported then incline is supported also

            //since we setup the distance, Pulse, and time on a differenc command we need to
            //add this listener to it also
            cmd = this.mSFitSysCntrl.getReadCommand(BitFieldId.DISTANCE);
            if(cmd != null)
            {
                cmd.addOnCommandReceived(this);
            }
        }
        else if(dev == DeviceId.SPIN_BIKE)
        {
            FecpCommand cmd = this.mSFitSysCntrl.getReadCommand(BitFieldId.KPH);
            if(cmd != null)
            {
                cmd.addOnCommandReceived(this);
            }

            //if kph is supported then rpms, torque Watts has been added to the same command

            //since we setup the distance, Pulse, and time on a differenc command we need to
            //add this listener to it also
            cmd = this.mSFitSysCntrl.getReadCommand(BitFieldId.DISTANCE);
            if(cmd != null)
            {
                cmd.addOnCommandReceived(this);
            }

        }
        else if(dev == DeviceId.FITNESS_BIKE)
        {
            FecpCommand cmd = this.mSFitSysCntrl.getReadCommand(BitFieldId.KPH);
            if(cmd != null)
            {
                cmd.addOnCommandReceived(this);
            }

            //if kph is supported then rpms, torque Watts has been added to the same command

            //since we setup the distance, Pulse, and time on a differenc command we need to
            //add this listener to it also
            cmd = this.mSFitSysCntrl.getReadCommand(BitFieldId.DISTANCE);
            if(cmd != null)
            {
                cmd.addOnCommandReceived(this);
            }
        }
        else if(dev == DeviceId.ELLIPTICAL)
        {
            FecpCommand cmd = this.mSFitSysCntrl.getReadCommand(BitFieldId.KPH);
            if(cmd != null)
            {
                cmd.addOnCommandReceived(this);
            }

            //if kph is supported then rpms, torque Watts has been added to the same command

            //since we setup the distance, Pulse, and time on a differenc command we need to
            //add this listener to it also
            cmd = this.mSFitSysCntrl.getReadCommand(BitFieldId.DISTANCE);
            if(cmd != null)
            {
                cmd.addOnCommandReceived(this);
            }
        }
    }

    /**
     * Handles the reply from the device
     *
     * @param cmd the command that was sent.
     */
    @Override
    public void onCommandReceived(final Command cmd) {
        //update the display based on the data received
        //in this we need to update anything that we currently have
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //update speed, Distance, Time, Calories, Incline
                TreeMap<BitFieldId, BitfieldDataConverter> cmdResults;

                //if running mode, just join the party
                if (cmd.getCmdId() == CommandId.WRITE_READ_DATA || cmd.getCmdId() == CommandId.PORTAL_DEV_LISTEN) {


                    //use the Fitpro System Device for the data
                    cmdResults = mSFitSysCntrl.getFitProCntrl().getSysDev().getCurrentSystemData();

                    try {

                        if (cmdResults.containsKey(BitFieldId.CALORIES)) {

                            double calVal = ((CaloriesConverter)cmdResults.get(BitFieldId.CALORIES)).getCalories();
                            //= ((CaloriesConverter) cmdResults.get(BitFieldId.CALORIES).getData()).getCalories();
                            mThirdValueTextView.setText(String.format("%.2f",calVal));
                        }

                        if (cmdResults.containsKey(BitFieldId.KPH)) {
                            double kph = ((SpeedConverter) cmdResults.get(BitFieldId.KPH)).getSpeed();

                            if (mSFitSysCntrl.isMetric()) {
                                mTopRightValueTextView.setText(String.format("%.2f",kph));//should do formatting
                            } else {
                                //convert to MPH
                                //TODO MPH handling
                                mTopRightValueTextView.setText(String.format("%.2f",(kph / 1.60934)));
                            }
                        }

                        if (cmdResults.containsKey(BitFieldId.GRADE)) {
                            mTopLeftValueTextView.setText(((GradeConverter) cmdResults.get(BitFieldId.GRADE)).getIncline() + "");
                        }

                        if (cmdResults.containsKey(BitFieldId.DISTANCE)) {
                            int distanceMeters = ((LongConverter) cmdResults.get(BitFieldId.DISTANCE)).getValue();
                            if (mSFitSysCntrl.isMetric()) {
                                double kilometers = distanceMeters * 0.001;
                                mFourthValueTextView.setText(String.format("%.3f",kilometers));
                            } else {
                                double miles = distanceMeters * 0.00062137;//conversion to miles
                                mFourthValueTextView.setText(String.format("%.3f",miles));

                            }
                        }

                        if (cmdResults.containsKey(BitFieldId.RUNNING_TIME)) {
                            int total = ((LongConverter) cmdResults.get(BitFieldId.RUNNING_TIME)).getValue();
                            int hr  = total /3600;
                            int rem = total % 3600;
                            int mins = rem / 60;
                            int secs = rem % 60;

                            String hrStr = (hr<10 ? "0" : "")+hr;
                            String mnStr = (mins<10 ? "0" : "")+mins;
                            String secStr = (secs<10 ? "0" : "")+secs;
                            mSecondValueTextView.setText(hrStr +":"+ mnStr + ":" + secStr);

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
