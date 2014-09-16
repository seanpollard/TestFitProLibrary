package com.ifit.sfit.sparky.testsdrivers;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ifit.sfit.sparky.R;
import com.ifit.sfit.sparky.tests.TestTreadmillKeyCodes;

/**
 * Created by jc.almonte on 8/6/14.
 */
public class TreadmillKeyCodesTest  extends BaseTest implements AdapterView.OnItemSelectedListener {

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
                R.array.treadmillkeys_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }

    /**
     * Run selected Treadmill Key Code test
     */
    @Override
    void runTest() {

        final TestTreadmillKeyCodes t = new TestTreadmillKeyCodes(fecpController, (BaseTest) context, this.mSFitSysCntrl);

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    switch (testToRun)
                    {
                        case "Stop Key":
                            returnString = t.testStopKey();
                            break;
                        case "Start Key":
                            returnString = t.testStartKey();
                            break;
                        case "Incline Up":
                            returnString = t.testInclineUpKey();
                            break;
                        case "Incline Down":
                            returnString = t.testInclineDownKey();
                            break;
                        case "Speed Up":
                            returnString = t.testSpeedUpKey();
                            break;
                        case "Speed Down":
                            returnString = t.testSpeedDownKey();
                            break;
                        case "Quick Speed":
                            returnString = t.testQuickSpeedKeys();
                            break;
                        case "Quick Incline":
                            returnString = t.testQuickInclineKeys();
                            break;
                        case "Age Up":
                            returnString = t.testAgeUpKey();
                            break;
                        case "Age Down":
                            returnString = t.testAgeDownKey();
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
