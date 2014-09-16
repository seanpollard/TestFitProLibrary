package com.ifit.sfit.sparky.testsdrivers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.ifit.sfit.sparky.R;
import com.ifit.sfit.sparky.activities.ManageTests;
import com.ifit.sfit.sparky.helperclasses.SendEmailAsyncTask;
import com.ifit.sfit.sparky.tests.TestBitfields;
import com.ifit.sfit.sparky.tests.TestIncline;
import com.ifit.sfit.sparky.tests.TestIntegration;
import com.ifit.sfit.sparky.tests.TestMotor;

/**
 * Class to handle running all tests. In this class only the runAll method of each handle test class is run
 * Created by jc.almonte on 8/1/14.
 */
public class AllTests extends BaseTest implements View.OnClickListener, AdapterView.OnItemSelectedListener{

     TestBitfields b;
     TestMotor m;
     TestIntegration i;
     TestIncline g ;
     boolean isAlarmMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        emailAddress = "jc.almonte@iconfitness.com";
        // 1. get passed intent
        Intent intent = getIntent();

        // 2. get message value from intent
        String message = intent.getStringExtra("message");
        isAlarmMessage = message.equals("runAll");
        if(isAlarmMessage == true);
        {
            testToRun = "All Tests";
            runTest();
        }
    }

    /**
     * Set up spinner and populated it with options specific to this test class
     */
    private void init() {

        Spinner spinner = (Spinner) findViewById(R.id.spinnerMotor);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.alltests_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }

    /**
     * Run selected "runAll" test
     */
    @Override
    void runTest() {
         b = new TestBitfields(ManageTests.fecpController, (BaseTest) context, ManageTests.mSFitSysCntrl);
         m = new TestMotor(ManageTests.fecpController, (BaseTest) context, ManageTests.mSFitSysCntrl);
         i = new TestIntegration(ManageTests.fecpController, (BaseTest) context, ManageTests.mSFitSysCntrl);
         g = new TestIncline(ManageTests.fecpController, (BaseTest) context, ManageTests.mSFitSysCntrl);
        final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollView));

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (testToRun) {
                        case "Motor Tests":
                            returnString = m.runAll();
                            break;
                        case "Integration Tests":
                            returnString = i.runAll();
                            break;
                        case "Incline Tests":
                            returnString = g.runAll();
                            break;
                        case "Bitfields Tests":
                            returnString = b.runAll();
                            break;
                        case "All Tests":
                            returnString = runAll();
                            break;
                    }
                    try {
                        returnString += "\n" + systemString;

                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write((returnString).getBytes());
                        outputStream.close();
                        new SendEmailAsyncTask(emailAddress).execute();


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
                finish(); // Go back to previous activity screen
            }
        });
        th.start();
        //try to write to the file in main from the machine control structure

    }

    /**
     * Indicates test to run based on item selected
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

    /**
     * Runs all tests for each test class
     * @return text log of test results
     * @throws Exception
     */
    public String runAll() throws Exception {

        String results ="";
        results += g.runAll();
        results += m.runAll();
        results += i.runAll();
        results += b.runAll();
        return results;
    }
}