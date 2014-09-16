package com.ifit.sfit.sparky.testsdrivers;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ifit.sfit.sparky.R;
import com.ifit.sfit.sparky.activities.ManageTests;
import com.ifit.sfit.sparky.tests.TestIncline;

/**
 * Incline test driver
 * Created by jc.almonte on 7/30/14.
 */
public class InclineTest extends BaseTest implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    /**
     * Set up spinner and populated it with options specific to this test class
     */
    private void init(){

        Spinner spinner = (Spinner) findViewById(R.id.spinnerMotor);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.incline_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }

    /**
     * Run selected Incline test
     */
    @Override
    void runTest() {

        final TestIncline t = new TestIncline(ManageTests.fecpController, (BaseTest) context, ManageTests.mSFitSysCntrl);

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    switch (testToRun)
                    {
                        case "Stop Incline":
                            returnString = t.testStopIncline();
                            break;
                        case "Retained Incline":
                            returnString = t.testRetainedIncline();
                            break;
                        case "Speed Incline Limits":
                            returnString = t.testSpeedInclineLimit();
                            break;
                        case "DMK Incline Retention/Recall":
                            returnString = t.testInclineRetentionDmkRecall();
                            break;
                        case "Incline Controller":
                            returnString = t.testInclineController();
                            break;
                        case "400 ms Pause":
                            returnString = t.testIncline400msPause();
                            break;
                        case "Calibrate":
                            returnString = t.testInclineCalibration();
                            break;
                        case "Run All":
                            returnString = t.runAll();
                            break;
                    }
                    try {
                        returnString += "\n" + systemString;

                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write((returnString).getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (returnString.isEmpty()) {
                        passFail = "<font color = #ff0000>ERROR</font>";
                    } else if (returnString.contains("FAIL")) {
                        passFail = "<font color = #ff0000>FAIL</font>";
                    } else {
                        passFail = "<font color = #00ff00>PASS</font>";
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultView.setText(Html.fromHtml(passFail));
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
        //try to write to the file in main from the machine control structure

    }

    /**
     * Indicates test to run based item selected
     * @param parent the parent adapter view
     * @param view current view
     * @param pos position of selected item
     * @param id selected item id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        testToRun = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
