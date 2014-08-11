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
import android.widget.TextView;
import android.widget.Toast;

import com.ifit.sfit.sparky.R;
import com.ifit.sfit.sparky.SFitSysCntrl;
import com.ifit.sfit.sparky.SendEmailAsyncTask;
import com.ifit.sfit.sparky.TestIntegration;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.FitProUsb;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.communication.SystemStatusListener;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.io.FileOutputStream;

/**
 * Created by jc.almonte on 7/29/14.
 */
public abstract class BaseTest extends Activity implements View.OnClickListener, SystemStatusListener {


    /*
    testingView: Display test results
    resultView: Display result of test (PASS/FAIL)
     */
    protected TextView testingView;
    protected TextView resultView;

    /*
    Controller Variables to handle communication and interaction with Brainboard
    */
    protected  FecpController fecpController;
    private FecpCommand mSystemStopCmd;
    protected SFitSysCntrl mSFitSysCntrl;

    //To display pop-up messages
    protected Toast mToast;

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
    Fields to enter information (kinda like text-boxes in C#)
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

        try {
            fecpController = new FitProUsb(getApplicationContext(), getIntent());
            mSFitSysCntrl = new SFitSysCntrl(fecpController);
            fecpController.initializeConnection(this);
            initLayout();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    //To return application context
    public static Context getAppContext() {
        return BaseTest.context;
    }

    //Initialize the application layout
    private void initLayout(){
        //initialize views
        testingView = (TextView) findViewById(R.id.testView);
        resultView = (TextView)findViewById(R.id.passFailView);
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
                        maxIncline = Integer.parseInt(inputText);
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

                    maxIncline = Integer.parseInt(inputText);
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
                        minIncline = Integer.parseInt(inputText);
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

                    minIncline = Integer.parseInt(inputText);
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
                        maxSpeed = Integer.parseInt(inputText);
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

                    maxSpeed = Integer.parseInt(inputText);
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
                        minSpeed = Integer.parseInt(inputText);
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

                    minSpeed = Integer.parseInt(inputText);
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

            try{
                TestIntegration system = new TestIntegration(this.fecpController,this,this.mSFitSysCntrl);
                configString += system.testSystemConfiguration(systemString);

                //try to write to the file in main from the machine control structure
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write((configString).getBytes());
                outputStream.close();
            } catch(Exception e){
                e.printStackTrace();
            }
            testingView.setText(configString);
            if(configString.contains("FAIL")) {
                passFail = "<font color = #ff0000>FAIL </font>";
            }
            else {
                passFail = "<font color = #00ff00>PASS </font>";
            }
            resultView.setText(Html.fromHtml(passFail));
        }
        else if(v==allTestsButton)
        {
            resultView.setText(" ");
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
            try {
                if(editEmail.getText().toString().isEmpty()){
                    Toast.makeText(this, "Please enter an email!", Toast.LENGTH_LONG).show();
                }
                else {
                    new SendEmailAsyncTask(emailAddress).execute();
                }
            }catch(Exception e){
                Toast.makeText(this, "Email was not sent.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }

        if(v == clearButton) {
            try {
                editEmail.setText("");
            } catch (Exception e){
                e.printStackTrace();
            }

        }


        //Clear out the system device string to re-enter new values for next test
        systemString = "";
        //clear out print string to recheck values if needed from a typo
        returnString = "";
    }

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

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        boolean result = false;
//        switch (keyCode)
//        {
//            case KeyEvent.KEYCODE_BACK:
//            Intent myIntent = new Intent(BaseTest.this, ManageTests.class);
//            startActivity(myIntent);
//            result = false;
//        }
//        return result;
//    }

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
    @Override
    public void systemDisconnected() {

        //change display back to the original screen
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null == mToast)
                {
                    mToast = Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_LONG);
                }
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.setText("Connection Lost");

                mToast.show();
//                Toast.makeText(getApplicationContext(),"Connection Lost", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This is called after system is connected
     *
     * @param dev the System device that is connected.
     */
    @Override
    public void systemDeviceConnected(final SystemDevice dev) {

        //if successful the dev won't be null
        //system is connected used this in the rest of the system.

        if(dev == null || dev.getInfo().getDevId() == DeviceId.NONE)
        {
            Toast.makeText(this,"Connection Failed",Toast.LENGTH_LONG).show();
            return;
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null == mToast)
                {
                    mToast = Toast.makeText(getApplicationContext(), "Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getSysDevInfo().getConsoleName(), Toast.LENGTH_LONG);
                }
//                Toast.makeText(getApplicationContext(),"Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getConsoleName() , Toast.LENGTH_LONG).show();
                mToast.setText("Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getSysDevInfo().getConsoleName() );
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.show();
            }
        });

        //check if treadmill or incline trainer
        if(dev.getInfo().getDevId() == DeviceId.INCLINE_TRAINER || dev.getInfo().getDevId() == DeviceId.TREADMILL)
        {
            if(dev.getInfo().getSupportedBitfields().contains(BitFieldId.WORKOUT_MODE)) {

                try {

                    this.mSystemStopCmd = new FecpCommand(dev.getCommand(CommandId.WRITE_READ_DATA));
                    ((WriteReadDataCmd)this.mSystemStopCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This will be called when the communication layer is connected. this is a lower level of
     * communication notification.
     */
    @Override
    public void systemCommunicationConnected() {
        //nothing to do, implemented in fitpro layer
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == mToast) {
                    mToast = Toast.makeText(getApplicationContext(), "Connected ", Toast.LENGTH_LONG);
                }
//                Toast.makeText(getApplicationContext(),"Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getConsoleName() , Toast.LENGTH_LONG).show();
                mToast.setText("Connected ");
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.show();
            }
        });
    }

    @Override
    public void systemSecurityValidated() {

//        //system is validated you may control the system
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (null == mToast) {
//                    mToast = Toast.makeText(getApplicationContext(), "System is Validated", Toast.LENGTH_LONG);
//                }
////                Toast.makeText(getApplicationContext(),"Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getConsoleName() , Toast.LENGTH_LONG).show();
//                mToast.setText("System is Validated");
//                mToast.setDuration(Toast.LENGTH_LONG);
//                mToast.show();
//            }
//        });
//        this.mSFitSysCntrl.getInitialSysItems(this, 0, 0);
    }
}
