/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 5/20/2014
 * @version 1
 * Details.
 */
package com.ifit.sfit.sparky.idle;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ifit.sfit.sparky.R;
import com.ifit.sfit.sparky.SFitSysCntrl;
import com.ifit.sfit.sparky.fragments.BaseFragment;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;

import java.util.ArrayList;

public class IdleFragment extends BaseFragment implements View.OnClickListener {


    private GestureOverlayView gestureOverlay;
    private GestureLibrary mLibrary;
    private Button mStartManualButton;


    public IdleFragment(SFitSysCntrl fitSysCntrl)
    {
        super(fitSysCntrl, R.layout.idle_fragment);
        //add listeners if they are available
    }
    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(android.os.Bundle)} and {@link #onActivityCreated(android.os.Bundle)}.
     * <p/>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {


        View view = super.onCreateView(inflater, container, savedInstanceState);


        this.mStartManualButton = (Button)view.findViewById(R.id.startButton);

        //add events
        this.mStartManualButton.setOnClickListener(this);

        mLibrary = GestureLibraries.fromRawResource(view.getContext(), R.raw.gestures);
        mLibrary.load();

        gestureOverlay = (GestureOverlayView) view.findViewById(R.id.gesture_overlay);
        gestureOverlay.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
                try {
                    ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
                    if (predictions.size() > 0) {
                        Prediction prediction = predictions.get(0);
                        if (prediction.score > 1.0) {
                            //go to Settings
                            //FragmentTransaction ft = getFragmentManager().beginTransaction();
                            //ft.replace(R.id.container, new SettingFragment());
                            //ft.commit();
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //move to running lap screen fragment
        //send command down to brain board to change modes from idle to running
        SystemDevice sysDev = this.mSFitSysCntrl.getFitProCntrl().getSysDev();
        FecpCommand cmd;
        try {
            cmd = new FecpCommand(sysDev.getCommand(CommandId.WRITE_READ_DATA));

            if(sysDev.getInfo().getSupportedBitfields().contains(BitFieldId.WORKOUT_MODE))
            {
                ((WriteReadDataCmd)cmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.RUNNING);//change mode to be running
                this.mSFitSysCntrl.getFitProCntrl().addCmd(cmd);//add a single command to send the mode to running
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.container, new TrackFragment(this.mSFitSysCntrl));
//        ft.commit();

    }
}
