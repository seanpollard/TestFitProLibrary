/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 5/20/2014
 * @version 1
 * Details.
 */
package com.ifit.sfit.sparky.running;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ifit.sfit.sparky.R;
import com.ifit.sfit.sparky.SFitSysCntrl;
import com.ifit.sfit.sparky.fragments.BaseFragment;
import com.ifit.sfit.sparky.widgets.VerticalSeekBar;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.OnCommandReceivedListener;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.GradeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.LongConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.text.DecimalFormat;
import java.util.TreeMap;

public class TrackFragment extends BaseFragment implements OnCommandReceivedListener {

    private static final double TRACK_METERS = 400;

    private ImageView trackUnderlayImg;

    Bitmap underlayBitmap;
    private Paint maskPaint;
    private Paint xferPaint;
    private int mUnderlayWidth, mUnderlayHeight;
    private Handler handler = new Handler();

    private double distance = 0;
    private AsyncTask trackAnimator;
    private Bitmap mask;
    private Bitmap result;
    private Canvas mCanvas;
    private Canvas mTempCanvas;

    private TextView mMaxInclineTextView;
    private TextView mMinInclineTextView;
    private TextView mMaxSpeedTextView;
    private TextView mMinSpeedTextView;
    private TextView mCurrentSpeedTextView;
    private TextView mCurrentInclineTextView;

    private VerticalSeekBar mInclineSeekBar;
    private VerticalSeekBar mSpeedSeekBar;


    private double mMaxSpeed = 0;
    private double mMinSpeed = 0;
    private double mCurrentSpeed = 0;
    private double mMaxIncline = 0;
    private double mMinIncline = 0;
    private double mCurrentIncline = 0;

    private double mCurrentLapDistance = 0;

    private FecpCommand mSpeedInclineCmd;


    public TrackFragment(SFitSysCntrl fitSysCntrl)
    {
        super(fitSysCntrl, R.layout.track_fragment);

        SystemDevice sysDev = this.mSFitSysCntrl.getFitProCntrl().getSysDev();
        DeviceId dev = this.mSFitSysCntrl.getFitProCntrl().getSysDev().getInfo().getDevId();


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Called immediately after {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mInclineSeekBar = (VerticalSeekBar)view.findViewById(R.id.incline_seek_bar);
        this.mSpeedSeekBar = (VerticalSeekBar)view.findViewById(R.id.speed_seek_bar);
        this.mMaxInclineTextView = (TextView)view.findViewById(R.id.maxInclineTextView);
        this.mMinInclineTextView = (TextView)view.findViewById(R.id.minInclineTextView);
        this.mMaxSpeedTextView = (TextView)view.findViewById(R.id.maxSpeedTextView);
        this.mMinSpeedTextView = (TextView)view.findViewById(R.id.minSpeedTextView);
        this.mCurrentInclineTextView = (TextView)view.findViewById(R.id.currentInclineTextView);
        this.mCurrentSpeedTextView = (TextView)view.findViewById(R.id.currentSpeedTextView);

        //update the max and min values for incline and speed
        SystemDevice device = this.mSFitSysCntrl.getFitProCntrl().getSysDev();
        DeviceId dev = this.mSFitSysCntrl.getFitProCntrl().getSysDev().getInfo().getDevId();
        if(dev == DeviceId.TREADMILL || dev == DeviceId.INCLINE_TRAINER)
        {
            //leave to the default
            //add this to the list of on command receiver
            FecpCommand cmd = this.mSFitSysCntrl.getReadCommand(BitFieldId.DISTANCE);
            try {
                this.mSpeedInclineCmd = new FecpCommand(device.getCommand(CommandId.WRITE_READ_DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //we only need to know about the Distance in this fragment
            if(cmd != null)
            {
                cmd.addOnCommandReceived(this);
            }
        }

        TreeMap<BitFieldId, BitfieldDataConverter> supportedItems = device.getCurrentSystemData();
        if(!supportedItems.containsKey(BitFieldId.KPH))
        {
            //make the Speed items invisible
            this.mSpeedSeekBar.setVisibility(View.INVISIBLE);
            this.mMaxSpeedTextView.setVisibility(View.INVISIBLE);
            this.mMinSpeedTextView.setVisibility(View.INVISIBLE);
        }
        else
        {
            //check if we have the max and min speed
            if(supportedItems.containsKey(BitFieldId.MAX_KPH))
            {
                this.mMaxSpeed = ((SpeedConverter)supportedItems.get(BitFieldId.MAX_KPH)).getSpeed();
                this.mMaxSpeedTextView.setText( "Max " + this.mMaxSpeed);
            }

            if(supportedItems.containsKey(BitFieldId.MIN_KPH))
            {

                this.mMinSpeed = ((SpeedConverter)supportedItems.get(BitFieldId.MIN_KPH)).getSpeed();
                this.mMinSpeedTextView.setText( "Min " +this.mMinSpeed);
            }

            if(supportedItems.containsKey(BitFieldId.KPH))
            {
                this.mCurrentSpeed = ((SpeedConverter)supportedItems.get(BitFieldId.KPH)).getSpeed();
                //set the current location of the Seek Bar

                if(this.mMaxSpeed != 0) {
                    double currentProgress = (this.mCurrentSpeed - this.mMinSpeed) / (this.mMaxSpeed - this.mMinSpeed);
                    currentProgress *= 100;
                    this.mSpeedSeekBar.setProgress((int)currentProgress);//truncate the speed

                    this.mSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                            //convert the progress into speed
                            int mMinSpeedView = ((LinearLayout)mCurrentSpeedTextView.getParent()).getBottom();
                            //convert the progress into incline
                            double speedFromProg = progress;
                            speedFromProg /= 100;
                            speedFromProg = speedFromProg * (mMaxSpeed - mMinSpeed);
                            speedFromProg += mMinSpeed;//if negative it will decrease
                            DecimalFormat speedFormat = new DecimalFormat("##.#");
                            mCurrentSpeedTextView.setText(speedFormat.format(speedFromProg) + " kph");

                            //set the location of the Label
                            int textViewHeight = mCurrentSpeedTextView.getHeight();

                            double targetTextviewY = mMinSpeedView - ((progress * 0.01) * mMinSpeedView);
                            //check to make sure it is below the max and the min labels
                            if(mMaxSpeedTextView.getBottom() > targetTextviewY)
                            {
                                targetTextviewY = mMaxSpeedTextView.getBottom();
                            }
                            else if(mMinSpeedTextView.getTop() < (targetTextviewY+textViewHeight))
                            {
                                targetTextviewY = mMinSpeedTextView.getTop()-textViewHeight;
                            }
                            mCurrentSpeedTextView.setY((float) targetTextviewY);//convert to float
                            //set x to be at the left of the seekbar
                            mCurrentSpeedTextView.setX(seekBar.getLeft()- mCurrentSpeedTextView.getWidth());
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            mCurrentSpeedTextView.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                            mCurrentSpeedTextView.setVisibility(View.INVISIBLE);
                            //send command to power board to update the speed
                            double speedFromProg = seekBar.getProgress();
                            speedFromProg /= 100;
                            speedFromProg = speedFromProg * (mMaxSpeed - mMinSpeed);
                            speedFromProg += mMinSpeed;//if negative it will decrease
                            //convert current progress into target Speed

                            try {
                                ((WriteReadDataCmd)mSpeedInclineCmd.getCommand()).addWriteData(BitFieldId.KPH,speedFromProg);
                                mSFitSysCntrl.getFitProCntrl().addCmd(mSpeedInclineCmd);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    this.mSpeedSeekBar.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            if(event.getAction() == MotionEvent.ACTION_DOWN)
                            {
                                mCurrentSpeedTextView.setVisibility(View.VISIBLE);

                            }
                            else if(event.getAction() == MotionEvent.ACTION_UP)
                                //get current progress
                            {
                                mCurrentSpeedTextView.setVisibility(View.INVISIBLE);
                                //send command to power board to update the speed
                                double speedFromProg = mSpeedSeekBar.getProgress();
                                speedFromProg /= 100;
                                speedFromProg = speedFromProg * (mMaxSpeed - mMinSpeed);
                                speedFromProg += mMinSpeed;//if negative it will decrease
                                //convert current progress into target Speed
                                DecimalFormat formatter = new DecimalFormat("##.##");
                                speedFromProg = Double.parseDouble(formatter.format(speedFromProg));//todo make more effiecent

                                try {
                                    ((WriteReadDataCmd)mSpeedInclineCmd.getCommand()).addWriteData(BitFieldId.KPH,speedFromProg);
                                    mSFitSysCntrl.getFitProCntrl().addCmd(mSpeedInclineCmd);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return true;

                            }
                            return false;
                        }
                    });
                }
            }

        }

        if(!supportedItems.containsKey(BitFieldId.GRADE))
        {
            //make the Speed items invisible
            this.mInclineSeekBar.setVisibility(View.INVISIBLE);
            this.mMaxInclineTextView.setVisibility(View.INVISIBLE);
            this.mMinInclineTextView.setVisibility(View.INVISIBLE);
        }
        else
        {
            //check if we have the max and min speed
            if(supportedItems.containsKey(BitFieldId.MAX_GRADE))
            {
                this.mMaxIncline = ((GradeConverter)supportedItems.get(BitFieldId.MAX_GRADE)).getIncline();
                this.mMaxInclineTextView.setText( "Max " +this.mMaxIncline);
            }

            if(supportedItems.containsKey(BitFieldId.MIN_GRADE))
            {

                this.mMinIncline = ((GradeConverter)supportedItems.get(BitFieldId.MIN_GRADE)).getIncline();
                this.mMinInclineTextView.setText( "Min " +this.mMinIncline);
            }

            if(supportedItems.containsKey(BitFieldId.GRADE))
            {
                this.mCurrentIncline = ((GradeConverter)supportedItems.get(BitFieldId.GRADE)).getIncline();
                //set the current location of the Seek Bar

                if(this.mMaxIncline != 0) {
                    double currentProgress = (this.mCurrentIncline - this.mMinIncline) / (this.mMaxIncline - this.mMinIncline);//since min is neg
                    currentProgress *= 100;
                    this.mInclineSeekBar.setProgress((int)currentProgress);//truncate the incline


                    this.mInclineSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            //nothing needed
                            //nice feature is to display the target next to the bar
                            //update the Display

                            int mMinInclineView = ((LinearLayout)mCurrentInclineTextView.getParent()).getBottom();
                            //convert the progress into incline
                            double inclineFromProg = progress;
                            inclineFromProg /= 100;
                            inclineFromProg = inclineFromProg * (mMaxIncline - mMinIncline);
                            inclineFromProg += mMinIncline;//if negative it will decrease
                            DecimalFormat inclineFormat = new DecimalFormat("##.#");
                            mCurrentInclineTextView.setText(inclineFormat.format(inclineFromProg) + " %");

                            //set the location of the Label
                            int textViewHeight = mCurrentInclineTextView.getHeight();

                            double targetTextviewY = mMinInclineView - ((progress * 0.01) * mMinInclineView);
                            //check to make sure it is below the max and the min labels
                            if(mMaxInclineTextView.getBottom() > targetTextviewY)
                            {
                                targetTextviewY = mMaxInclineTextView.getBottom();
                            }
                            else if(mMinInclineTextView.getTop() < (targetTextviewY+textViewHeight))
                            {
                                targetTextviewY = mMinInclineTextView.getTop()-textViewHeight;
                            }
                            mCurrentInclineTextView.setY((float) targetTextviewY);//convert to float

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            //nothing needed
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            //send command to power board to update the speed
                            double inclineFromProg = seekBar.getProgress();
                            inclineFromProg /= 100;
                            inclineFromProg = inclineFromProg * (mMaxIncline - mMinIncline);
                            inclineFromProg += mMinIncline;//if negative it will decrease

                            //convert current progress into target Speed
                            //send command to send down the new incline

                            try {
                                ((WriteReadDataCmd)mSpeedInclineCmd.getCommand()).addWriteData(BitFieldId.GRADE,inclineFromProg);
                                mSFitSysCntrl.getFitProCntrl().addCmd(mSpeedInclineCmd);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    this.mInclineSeekBar.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN)
                            //get current progress
                            {
                                mCurrentInclineTextView.setVisibility(View.VISIBLE);

                            }
                            else if(event.getAction() == MotionEvent.ACTION_UP)
                            //get current progress
                            {
                                double inclineFromProg = mInclineSeekBar.getProgress();
                                inclineFromProg /= 100;
                                inclineFromProg = inclineFromProg * (mMaxIncline - mMinIncline);
                                inclineFromProg += mMinIncline;//if negative it will decrease

                                DecimalFormat formatter = new DecimalFormat("##.#");
                                inclineFromProg = Double.parseDouble(formatter.format(inclineFromProg));//todo make more effiecent
                                //convert current progress into target Speed
                                //send command to send down the new incline

                                try {
                                    ((WriteReadDataCmd)mSpeedInclineCmd.getCommand()).addWriteData(BitFieldId.GRADE,inclineFromProg);
                                    mSFitSysCntrl.getFitProCntrl().addCmd(mSpeedInclineCmd);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                mCurrentInclineTextView.setVisibility(View.INVISIBLE);
                                return true;

                            }
                            return false;
                        }
                    });
                }
            }

        }

        //assign resources
        trackUnderlayImg = (ImageView) view.findViewById(R.id.trackUnderlayImg);

        mUnderlayHeight = getResources().getDimensionPixelSize(R.dimen.track_image_height);
        mUnderlayWidth = getResources().getDimensionPixelSize(R.dimen.track_image_width);

        Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bg_workout_track_underlay);
        underlayBitmap = Bitmap.createScaledBitmap(temp, mUnderlayWidth, mUnderlayHeight, false);
        underlayBitmap = underlayBitmap.copy(Bitmap.Config.ARGB_8888, false);
        temp.recycle();

        xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setColor(Color.BLACK);
        maskPaint.setStrokeWidth(10);

        mask = Bitmap.createBitmap(underlayBitmap.getWidth(), underlayBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);

        mTempCanvas = new Canvas(mask);
        showTrackProgress(0);
    }



    private void createLapUiInfo(View view) {
//        mLapNumberTextView = (TextView) view.findViewById(R.id.workout_track_lap_number);
//        mLapTimeTextView = (TextView) view.findViewById(R.id.workout_track_lap_timer);
    }

    /**
     * Masks out the track underlay so that it will only show as much as you specify
     *
     * @param meters the distance to draw in meters
     */
    private void showTrackProgress(double meters) {

        if (underlayBitmap == null) return;

        mTempCanvas.drawPath(createComplexPath(mUnderlayWidth, mUnderlayHeight, meters), maskPaint);

        mCanvas = new Canvas(result);
        mCanvas.drawBitmap(underlayBitmap, 0, 0, null);
        mCanvas.drawBitmap(mask, 0, 0, xferPaint);

        trackUnderlayImg.setImageBitmap(result);
        trackUnderlayImg.setScaleType(ImageView.ScaleType.CENTER);

    }

	@Override
	public void onResume() {
		super.onResume();
		//Only log the memory every 2 seconds

//        handler.postDelayed(runnable, 2000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//        handler.removeCallbacks(runnable);

		trackUnderlayImg.setImageBitmap(null);
		mCanvas.setBitmap(null);
		trackUnderlayImg = null;
		mCanvas = null;

		underlayBitmap.recycle();
		underlayBitmap = null;
		mask.recycle();
		mask = null;
		result.recycle();
		result = null;

//        runnable = null;
		handler = null;
	}

	/**
	 * Creates a path that can be used to draw the track. This path will follow the track exactly.
	 *
	 * @param width  the width of the rectangle used as the path's bounds
	 * @param height the height of the rectangle used as the path's bounds
	 * @param meters the distance to draw in meters
	 * @return
	 */
	private Path createComplexPath(int width, int height, double meters) {

		meters = meters % TRACK_METERS;
		if (meters < mCurrentLapDistance) {
			refreshBitmaps();
		}
		mCurrentLapDistance = meters;

		Path path = new Path();

		// 0 - 1/6 of track
		float sectionPercent = (float) (meters / (1f / 6f * TRACK_METERS));
		sectionPercent = (sectionPercent > 1) ? 1 : sectionPercent;
		float rectWidth = (width / 3f) * sectionPercent;
		path.addRect(width / 3f, height / 2f, (width / 3f) + rectWidth, height, Path.Direction.CCW);

		// 1/6 - 3/6 of track
		if (meters > 1f / 6f * TRACK_METERS) {
			sectionPercent = (float) ((meters - (1f / 6f * TRACK_METERS)) / (2f / 6f * TRACK_METERS));
			sectionPercent = (sectionPercent > 1) ? 1 : sectionPercent;
			double sweepAngle = sectionPercent * -180;

			path.moveTo(2f * width / 3f, height / 2f);
			RectF rightArcRect = new RectF(width / 3f, 0, width, height);
			path.arcTo(rightArcRect, 90, (float) sweepAngle);
		}

		// 3/6 - 4/6 of track
		if (meters > 3f / 6f * TRACK_METERS) {
			sectionPercent = (float) ((meters - (3f / 6f * TRACK_METERS)) / (1f / 6f * TRACK_METERS));
			sectionPercent = (sectionPercent > 1) ? 1 : sectionPercent;
			rectWidth = (width / 3f) * sectionPercent;
			path.addRect(2f * width / 3f, 0, (2f * width / 3f) - rectWidth, height / 2f, Path.Direction.CCW);
		}

		// 4/6 - 6/6 of track
		if (meters > 4f / 6f * TRACK_METERS) {
			sectionPercent = (float) ((meters - (4f / 6f * TRACK_METERS)) / (2f / 6f * TRACK_METERS));
			sectionPercent = (sectionPercent > 1) ? 1 : sectionPercent;
			double sweepAngle = sectionPercent * -180;

			path.moveTo(width / 3f, height / 2f);
			RectF leftArcRect = new RectF(0, 0, 2f * width / 3f, height);
			path.arcTo(leftArcRect, -90, (float) sweepAngle);
		}

		return path;
	}

	private void refreshBitmaps() {
		//Clears the track and allows a new path to be created. Only occurs when the track is filled up.
		mask.recycle();
		result.recycle();
		mask = Bitmap.createBitmap(underlayBitmap.getWidth(), underlayBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);

		mTempCanvas = new Canvas(mask);

	}

    /**
     * Handles the reply from the device
     *
     * @param cmd the command that was sent.
     */
    @Override
    public void onCommandReceived(final Command cmd) {
		if (trackAnimator != null && trackAnimator.getStatus() != AsyncTask.Status.FINISHED) {
			trackAnimator.cancel(true);
		}
//if running mode, just join the party
        Activity temp = getActivity();

        if(temp == null)
        {
            return;
        }
        temp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TreeMap<BitFieldId, BitfieldDataConverter> cmdResults;
                int distanceMeters;

                if (cmd.getCmdId() == CommandId.WRITE_READ_DATA || cmd.getCmdId() == CommandId.PORTAL_DEV_LISTEN) {


                    //use the Fitpro System Device for the data
                    cmdResults = mSFitSysCntrl.getFitProCntrl().getSysDev().getCurrentSystemData();
//                    WriteReadDataSts sts = (WriteReadDataSts) cmd.getStatus();
//                    cmdResults = sts.getResultData();

                    try {

                        if (cmdResults.containsKey(BitFieldId.DISTANCE)) {

                            distanceMeters = ((LongConverter) cmdResults.get(BitFieldId.DISTANCE)).getValue();
                            if (Double.compare(distanceMeters, 0.0) == 0) {
                                distance = 0.0;
                            }
                            trackAnimator = new AnimateTrackProgressAsync(distance, distanceMeters).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            distance = distanceMeters;
                        }

                        if (cmdResults.containsKey(BitFieldId.KPH)) {


                            double speed = ((SpeedConverter) cmdResults.get(BitFieldId.KPH)).getSpeed();
                            double seekBarSpeed = (mSpeedSeekBar.getProgress()*(mMaxSpeed- mMinSpeed));


                            //if equal don't change
//                            if (Double.compare(speed, seekBarSpeed) != 0) {
                                double currentProgress = (speed - mMinSpeed) / (mMaxSpeed - mMinSpeed);
                                currentProgress *= 100;

//                            mSpeedSeekBar.setSecondaryProgress((int)currentProgress);
                        }

                        if (cmdResults.containsKey(BitFieldId.GRADE)) {


                            double incline = ((GradeConverter) cmdResults.get(BitFieldId.GRADE)).getIncline();
                            double seekBarIncline = (mInclineSeekBar.getProgress());

                            incline = (incline - mMinIncline)/(mMaxIncline - mMinIncline);
                            incline *= 100;
//                            mInclineSeekBar.setSecondaryProgress((int)incline);

                        }

                        //update the Speed and incline


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }


            }
        });


    }

    private class AnimateTrackProgressAsync extends AsyncTask<Void, Double, Void> {
        private static final int STEPS = 10;
        private double mStartDistance, mEndDistance;

        public AnimateTrackProgressAsync(double startDistance, double endDistance) {
            mStartDistance = startDistance;
            mEndDistance = endDistance;
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            showTrackProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            double increment = (mEndDistance - mStartDistance) / STEPS;

            for (int i = 0; i < STEPS; i++) {
                mStartDistance += increment;
                mStartDistance = (mStartDistance > distance) ? distance : mStartDistance;

                publishProgress(mStartDistance);

                try {
                    Thread.sleep(75);
                } catch (InterruptedException e) {
                }
            }

            return null;
        }
    }


}
