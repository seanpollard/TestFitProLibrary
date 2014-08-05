package com.ifit.sfit.sparky.testsdrivers;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.ifit.sfit.sparky.R;
import com.ifit.sfit.sparky.TestAll;
import com.ifit.sfit.sparky.TestBitfields;
import com.ifit.sfit.sparky.TestIncline;
import com.ifit.sfit.sparky.TestIntegration;
import com.ifit.sfit.sparky.TestMotor;

/**
 * Created by jc.almonte on 8/1/14.
 */
public class AllTests extends BaseTest implements View.OnClickListener, AdapterView.OnItemSelectedListener, TestAll {

     TestBitfields b;
     TestMotor m;
     TestIntegration i;
     TestIncline g ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

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

    @Override
    void runTest() {
         b = new TestBitfields(fecpController, (BaseTest) context, this.mSFitSysCntrl);
         m = new TestMotor(fecpController, (BaseTest) context, this.mSFitSysCntrl);
         i = new TestIntegration(fecpController, (BaseTest) context, this.mSFitSysCntrl);
         g = new TestIncline(fecpController, (BaseTest) context, this.mSFitSysCntrl);
        final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollView));

        b.setUpdateResultViewListener(new TestBitfields.UpdateResultView() {
            @Override
            public void onUpdate(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        testingView.setText(Html.fromHtml(msg));
                    }
                });
                scrollview.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

            }
        });


        m.setUpdateResultViewListener(new TestMotor.UpdateResultView() {
            @Override
            public void onUpdate(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        testingView.setText(Html.fromHtml(msg));
                        scrollview.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                    }
                });

            }
        });

        i.setUpdateResultViewListener(new TestIntegration.UpdateResultView() {
            @Override
            public void onUpdate(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        testingView.setText(Html.fromHtml(msg));
                        scrollview.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                    }
                });

            }
        });

        g.setUpdateResultViewListener(new TestIncline.UpdateResultView() {
            @Override
            public void onUpdate(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        testingView.setText(Html.fromHtml(msg));
                        scrollview.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                    }
                });

            }
        });
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
                            runAll();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        testToRun = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public String runAll() throws Exception {

        String results ="";
        results += g.runAll();
        results += m.runAll();
        results += i.runAll();
        results += b.runAll();

        return results;
    }
}