package com.ifit.sfit.sparky.testsdrivers;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ifit.sfit.sparky.R;
import com.ifit.sfit.sparky.TestMotor;

/**
 * Created by jc.almonte on 7/29/14.
 */
public class MotorTest extends BaseTest implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){

        Spinner spinner = (Spinner) findViewById(R.id.spinnerMotor);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.motor_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }

    @Override
    void runTest() {

        final TestMotor t = new TestMotor(fecpController, (BaseTest) context, this.mSFitSysCntrl);

        t.setUpdateResultViewListener(new TestMotor.UpdateResultView() {
            @Override
            public void onUpdate(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        testingView.setText(Html.fromHtml(msg));
                    }
                });

            }
        });

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    switch (testToRun)
                    {
                        case "Start Speed":
                        returnString = t.testStartSpeed();
                        break;
                        case "Pause Resume":
                            returnString = t.testPauseResume();
                        break;
                        case "Distance":
                            returnString = t.testDistance();
                        break;
                        case "Modes":
                            returnString = t.testModes("all");
                        break;
                        case "Speed Controller":
                            returnString = t.testSpeedController();
                        break;
                        case "PWM Overshoot":
                            returnString = t.testPwmOvershoot();
                        break;
                        case "Calories":
                            returnString = t.testCalories();
                        break;
                        case "Run All":
                            returnString = t.runAll();
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
        //try to write to the file in main from the machine control structure

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        testToRun = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
