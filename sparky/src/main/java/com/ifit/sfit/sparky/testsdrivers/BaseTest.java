package com.ifit.sfit.sparky.testsdrivers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ifit.sfit.sparky.R;
import com.ifit.sfit.sparky.activities.ManageTests;
import com.ifit.sfit.sparky.helperclasses.SFitSysCntrl;
import com.ifit.sfit.sparky.helperclasses.SendEmailAsyncTask;
import com.ifit.sfit.sparky.tests.TestIntegration;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.communication.FecpController;

import java.io.FileOutputStream;

/**
 * Abstract Base class for all test drivers. Common features among test drivers are implemented on this class.
 * Created by jc.almonte on 7/29/14.
 */
public abstract class BaseTest extends Activity implements View.OnClickListener{


    /*
    testingView: Display test results
    resultView: Display result of test (PASS/FAIL)
    Both variables declared "static" to be able to access them from "HandleResults" class
     */
    public static TextView testingView;
    public static ScrollView scrollview;
    protected TextView resultView;

    /*
    Controller Variables to handle communication and interaction with Brainboard
    */
    protected  FecpController fecpController;
    private FecpCommand mSystemStopCmd;
    protected SFitSysCntrl mSFitSysCntrl;

    //To display pop-up messages
    public static Toast mToast; // Made static to use on email class

    /*
    passFail: holds result of test (PASS/FAIL)
    returnString: detailed test results
    */
    protected String configString = "";
    protected String returnString = "";
    protected String systemString = "";
    protected String passFail;
    protected String consoleName = "";
    protected String model = "";
    protected String emailAddress = "";
    protected String deviceInfo;

    /*
        outputStream: test result and put it in a text file
        filename: filename of the text file where results will be saved
    */
    protected FileOutputStream outputStream;
    protected String filename = "test.txt";

    // To hold application context
    protected static Context context;

    //Buttons on screen to perform tests

    protected Button allTestsButton;
    protected Button checkValsButton;
    protected Button findFailButton;
    protected Button emailButton;
    protected Button clearButton;

    /*
    Fields to enter information
    */
    protected EditText editConsoleName;
    protected EditText editModel;
    protected EditText editPartNumber;
    protected EditText editSoftwareVersion;
    protected EditText editHardwareVersion;
    protected EditText editSerialNumber;
    protected EditText editManNumber;
    protected EditText editMaxIncline;
    protected EditText editMinIncline;
    protected EditText editMaxSpeed;
    protected EditText editMinSpeed;
    protected EditText editEmail;

    /*
        Variables to hold device info
    */
    protected int partNumber = 0;
    protected int swVersion = 0;
    protected int hwVersion = 0;
    protected int serialNumber = 0;
    protected int manufactureNumber = 0;
    protected double maxIncline = 0.0;
    protected double minIncline = 0.0;
    protected double maxSpeed = 0.0;
    protected double minSpeed = 0.0;
    protected String testToRun = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      setContentView(R.layout.motor_layout);
      initLayout();

//        try {
//            fecpController = new FitProUsb(getApplicationContext(), getIntent());
//            mSFitSysCntrl = new SFitSysCntrl(fecpController);
//            fecpController.initializeConnection(this);
//            initLayout();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }

    }

    /**
     * To return application context
     * @return
     */
    public static Context getAppContext() {
        return BaseTest.context;
    }

    /**
     * Initialize the layout for the test screen
     */
    private void initLayout(){
        //initialize views
        testingView = (TextView) findViewById(R.id.testView);
        resultView = (TextView)findViewById(R.id.passFailView);
        scrollview = ((ScrollView) findViewById(R.id.scrollView));
        //Initializing buttons
        allTestsButton = (Button) findViewById(R.id.allTests);
        allTestsButton.setOnClickListener(this);
        checkValsButton = (Button) findViewById(R.id.checkValsButton);
        checkValsButton.setOnClickListener(this);
        findFailButton = (Button) findViewById(R.id.findFailButton);
        findFailButton.setOnClickListener(this);
        emailButton = (Button) findViewById(R.id.emailButton);
        emailButton.setOnClickListener(this);
        clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(this);

        //May implement more buttons later for individual tests
//            speedButton = (Button) findViewById(R.id.speedButton);
//            modeButton = (Button) findViewById(R.id.modeButton);


        editConsoleName = (EditText)findViewById(R.id.editConsoleName);
        editHardwareVersion = (EditText)findViewById(R.id.editHardwareVersion);
        editManNumber = (EditText)findViewById(R.id.editManNumber);
        editModel = (EditText)findViewById(R.id.editModelNumber);
        editPartNumber = (EditText)findViewById(R.id.editPartNumber);
        editSerialNumber = (EditText)findViewById(R.id.editSerialNumber);
        editSoftwareVersion = (EditText)findViewById(R.id.editSoftwareVersion);
        editMaxIncline = (EditText)findViewById(R.id.editMaxIncline);
        editMinIncline = (EditText)findViewById(R.id.editMinIncline);
        editMaxSpeed = (EditText)findViewById(R.id.editMaxSpeed);
        editMinSpeed = (EditText)findViewById(R.id.editMinSpeed);

        editEmail = (EditText)findViewById(R.id.editEmail);

        editConsoleName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                String inputText;
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editConsoleName.getText().toString();
                    if(inputText.isEmpty()) {
                        return false;//invalid data
                    }
                    try {
                        consoleName = inputText;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        editConsoleName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String inputText = editConsoleName.getText().toString();
                if(!hasFocus) {
                    if (inputText.isEmpty()) {
                        inputText = "";
                    }
                    consoleName = inputText;
                }

            }
        });

        editModel.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                String inputText;
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editModel.getText().toString();
                    if(inputText.isEmpty()) {
                        return false;//invalid data
                    }
                    try {
                        model = inputText;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        editModel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String inputText = editModel.getText().toString();
                if(!hasFocus) {
                    if (inputText.isEmpty()) {
                        inputText = "";
                    }
                    model = inputText;
                }

            }
        });

        editPartNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                String inputText;
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editPartNumber.getText().toString();
                    if(inputText.isEmpty()) {
                        return false;//invalid data
                    }
                    try {
                        partNumber = Integer.parseInt(inputText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        editPartNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String inputText = editPartNumber.getText().toString();
                if(!hasFocus) {
                    if (inputText.isEmpty()) {
                        inputText = "0";
                    }
                    partNumber = Integer.parseInt(inputText);
                }

            }
        });

        editSoftwareVersion.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                String inputText;
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editSoftwareVersion.getText().toString();
                    if(inputText.isEmpty()) {
                        return false;//invalid data
                    }
                    try {
                        swVersion = Integer.parseInt(inputText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        editSoftwareVersion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String inputText = editSoftwareVersion.getText().toString();
                if(!hasFocus) {
                    if (inputText.isEmpty()) {
                        inputText = "0";
                    }
                    swVersion = Integer.parseInt(inputText);
                }

            }
        });

        editHardwareVersion.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                String inputText;
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editHardwareVersion.getText().toString();
                    if(inputText.isEmpty()) {
                        return false;//invalid data
                    }
                    try {
                        hwVersion = Integer.parseInt(inputText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        editHardwareVersion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String inputText = editHardwareVersion.getText().toString();
                if(!hasFocus) {
                    if (inputText.isEmpty()) {
                        inputText = "0";
                    }
                    hwVersion = Integer.parseInt(inputText);
                }

            }
        });

        editSerialNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                String inputText;
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editSerialNumber.getText().toString();
                    if(inputText.isEmpty()) {
                        return false;//invalid data
                    }
                    try {
                        serialNumber = Integer.parseInt(inputText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        editSerialNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String inputText = editSerialNumber.getText().toString();
                if(!hasFocus) {
                    if (inputText.isEmpty()) {
                        inputText = "0";
                    }

                    serialNumber = Integer.parseInt(inputText);
                }

            }
        });

        editManNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                String inputText;
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editManNumber.getText().toString();
                    if (inputText.isEmpty()) {
                        return false;//invalid data
                    }
                    try {
                        manufactureNumber = Integer.parseInt(inputText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        editManNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String inputText = editManNumber.getText().toString();
                if(!hasFocus) {
                    if (inputText.isEmpty()) {
                        inputText = "0";
                    }

                    manufactureNumber = Integer.parseInt(inputText);
                }

            }
        });

        editMaxIncline.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                String inputText;
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editMaxIncline.getText().toString();
                    if (inputText.isEmpty()) {
                        return false;//invalid data
                    }
                    try {
                        maxIncline = Double.parseDouble(inputText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        editMaxIncline.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String inputText = editMaxIncline.getText().toString();
                if(!hasFocus) {
                    if (inputText.isEmpty()) {
                        inputText = "0";
                    }

                    maxIncline = Double.parseDouble(inputText);
                }

            }
        });

        editMinIncline.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                String inputText;
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editMinIncline.getText().toString();
                    if (inputText.isEmpty()) {
                        return false;//invalid data
                    }
                    try {
                        minIncline = Double.parseDouble(inputText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        editMinIncline.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String inputText = editMinIncline.getText().toString();
                if(!hasFocus) {
                    if (inputText.isEmpty()) {
                        inputText = "0";
                    }

                    minIncline = Double.parseDouble(inputText);
                }

            }
        });

        editMaxSpeed.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                String inputText;
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editMaxSpeed.getText().toString();
                    if (inputText.isEmpty()) {
                        return false;//invalid data
                    }
                    try {
                        maxSpeed = Double.parseDouble(inputText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        editMaxSpeed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String inputText = editMaxSpeed.getText().toString();
                if(!hasFocus) {
                    if (inputText.isEmpty()) {
                        inputText = "0";
                    }

                    maxSpeed = Double.parseDouble(inputText);
                }

            }
        });

        editMinSpeed.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                String inputText;
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editMinSpeed.getText().toString();
                    if (inputText.isEmpty()) {
                        return false;//invalid data
                    }
                    try {
                        minSpeed = Double.parseDouble(inputText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        editMinSpeed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String inputText = editMinSpeed.getText().toString();
                if(!hasFocus) {
                    if (inputText.isEmpty()) {
                        inputText = "0";
                    }

                    minSpeed = Double.parseDouble(inputText);
                }

            }
        });
        editEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                String inputText;
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER) || emailButton.isPressed()) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editEmail.getText().toString();
                    if (inputText.isEmpty()) {
                        return false;//invalid data
                    }
                    try {
                        emailAddress = inputText;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    return true;
                }
                return false;
            }
        });

        editEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String inputText = editEmail.getText().toString();
                if(!hasFocus) {
                    if (inputText.isEmpty()) {
                        inputText = "";
                        Toast.makeText(BaseTest.this, "Please enter an email", Toast.LENGTH_LONG).show();
                    }
                    emailAddress = inputText;
                }

            }
        });


    }

    @Override
    public void onClick(View v)
    {

        //Press this button to check the values entered by the user
        if(v == checkValsButton){
            //TODO: Move this test to Integration Test Layout
            configString = "";      //clear out Configuration results for each time the Check Values button is pressed
            //This is on TestIntegration class in SystemConfiguration method

            systemString = "Console Name: \""+consoleName+"\"\nModel Number: \""+model+"\"\nPart Number: \""+
                    partNumber+"\"\nSoftware Version: \""+swVersion+"\"\nHardware Version: \""+hwVersion+
                    "\"\nSerial Number: \""+ serialNumber+"\"\nManufacturing Number: \""+manufactureNumber+
                    "\"\nMax Incline: \""+maxIncline+"\"\nMin. Incline: \""+minIncline+"\"\nMax Speed: \""+maxSpeed+"\"\n" +
                    "Min Speed: \""+minSpeed+"\"\n";
//
            final TestIntegration t = new TestIntegration(ManageTests.fecpController, (BaseTest) context, ManageTests.mSFitSysCntrl);
            final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollView));

//            t.setUpdateResultViewListener(new TestIntegration.UpdateResultView() {
//                @Override
//                public void onUpdate(final String msg) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            testingView.setText(Html.fromHtml(msg));
//                            scrollview.fullScroll(ScrollView.FOCUS_DOWN);
//                        }
//                    });
//
//
//                }
//            });

            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                      returnString = t.testSystemConfiguration(systemString);
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
        }
        else if(v==allTestsButton)
        {
            resultView.setText(" ");
            testingView.setText(" ");
            runTest();
        }
        if(v == findFailButton) {
            try{
                String resultsString = testingView.getText().toString();
                final String FAIL = "* FAIL *";
                int failIndex = resultsString.indexOf(FAIL, 0);
                Spannable WordToSpan = new SpannableString(testingView.getText());

                for(int i = 0; i < resultsString.length() && failIndex != -1; i = failIndex+1) {
                    failIndex = resultsString.indexOf(FAIL, i);
                    if(failIndex == -1){
                        break;
                    }
                    else{
                        WordToSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00), failIndex, failIndex+FAIL.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        testingView.setText(WordToSpan, TextView.BufferType.SPANNABLE);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(v == emailButton) {

                if(editEmail.getText().toString().isEmpty()){
                    Toast.makeText(this, "Please enter an email!", Toast.LENGTH_LONG).show();
                }
                else {
                    new SendEmailAsyncTask(emailAddress).execute();
                }
        }

        if(v == clearButton) {
            try {
                editEmail.setText("");
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * Abstract method to run test specific to each test class
     */
    abstract void runTest();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //kill all threads
        if(mToast != null)
        {
            mToast.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mToast != null) {
            mToast.cancel();
        }
    }

    /**
     * this method is called when the system is disconnected.
     */

}
