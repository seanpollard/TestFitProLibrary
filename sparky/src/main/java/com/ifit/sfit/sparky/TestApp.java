package com.ifit.sfit.sparky;

import android.app.Activity;

public class TestApp extends Activity {//implements View.OnClickListener, SystemStatusListener {
//
//    /*
//    testingView: Display test results
//    resultView: Display result of test (PASS/FAIL)
//     */
//    public TextView testingView;
//    public TextView resultView;
//
//    /*
//    Controller Variables to handle communication and interaction with Brainboard
//    */
//    private FecpController fecpController;
//    private FecpCommand mSystemStopCmd;
//    private SFitSysCntrl mSFitSysCntrl;
//
//    //To display pop-up messages
//    private Toast mToast;
//
//    /*
//    passFail: holds result of test (PASS/FAIL)
//    returnString: detailed test results
//    */
//    private String configString = "";
//    protected String returnString = "";
//    private String systemString = "";
//    private String passFail;
//    private String consoleName = "";
//    private String model = "";
//    private String emailAddress = "";
//    private String deviceInfo;
//
//    /*
//        outputStream: test result and put it in a text file
//        filename: filename of the text file where results will be saved
//    */
//    private FileOutputStream outputStream;
//    private String filename = "test.txt";
//
//    // To hold application context
//    private static Context context;
//
//    //Buttons on screen to perform tests
//    private Button speedButton;
//    private Button modeButton;
//    private Button allTestsButton;
//    private Button checkValsButton;
//    private Button findFailButton;
//    private Button emailButton;
//    private Button clearButton;
//
//    /*
//    Fields to enter information (kinda like text-boxes in C#)
//    */
//    private EditText editConsoleName;
//    private EditText editModel;
//    private EditText editPartNumber;
//    private EditText editSoftwareVersion;
//    private EditText editHardwareVersion;
//    private EditText editSerialNumber;
//    private EditText editManNumber;
//    private EditText editMaxIncline;
//    private EditText editMinIncline;
//    private EditText editMaxSpeed;
//    private EditText editMinSpeed;
//    private EditText editEmail;
//
//    /*
//        Variables to hold device info
//    */
//    private int partNumber = 0;
//    private int swVersion = 0;
//    private int hwVersion = 0;
//    private int serialNumber = 0;
//    private int manufactureNumber = 0;
//    private double maxIncline = 0.0;
//    private double minIncline = 0.0;
//    private double maxSpeed = 0.0;
//    private double minSpeed = 0.0;
//
//   /**
//            * onCreate
//    * Called when the app is created.
//            * @param savedInstanceState Bundle item
//    */
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.motor_layout);
//
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
//
//    }
//
//   //To return application context
//    public static Context getAppContext() {
//        return TestApp.context;
//    }
//
//    //Initialize the application layout
//    private void initLayout(){
//        //Initializing buttons
//        allTestsButton = (Button) findViewById(R.id.allTests);
//        allTestsButton.setOnClickListener(this);
//        checkValsButton = (Button) findViewById(R.id.checkValsButton);
//        checkValsButton.setOnClickListener(this);
//        findFailButton = (Button) findViewById(R.id.findFailButton);
//        findFailButton.setOnClickListener(this);
//        emailButton = (Button) findViewById(R.id.emailButton);
//        emailButton.setOnClickListener(this);
//        clearButton = (Button) findViewById(R.id.clearButton);
//        clearButton.setOnClickListener(this);
//
//        //May implement more buttons later for individual tests
////            speedButton = (Button) findViewById(R.id.speedButton);
////            modeButton = (Button) findViewById(R.id.modeButton);
//
//
//        editConsoleName = (EditText)findViewById(R.id.editConsoleName);
//        editHardwareVersion = (EditText)findViewById(R.id.editHardwareVersion);
//        editManNumber = (EditText)findViewById(R.id.editManNumber);
//        editModel = (EditText)findViewById(R.id.editModelNumber);
//        editPartNumber = (EditText)findViewById(R.id.editPartNumber);
//        editSerialNumber = (EditText)findViewById(R.id.editSerialNumber);
//        editSoftwareVersion = (EditText)findViewById(R.id.editSoftwareVersion);
//        editMaxIncline = (EditText)findViewById(R.id.editMaxIncline);
//        editMinIncline = (EditText)findViewById(R.id.editMinIncline);
//        editMaxSpeed = (EditText)findViewById(R.id.editMaxSpeed);
//        editMinSpeed = (EditText)findViewById(R.id.editMinSpeed);
//
//        editEmail = (EditText)findViewById(R.id.editEmail);
//
//        editConsoleName.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int code, KeyEvent keyEvent) {
//                // if keydown and "enter" is pressed
//                String inputText;
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
//                    //do something
//                    //get the value from the text edit box and send it
//                    inputText = editConsoleName.getText().toString();
//                    if(inputText.isEmpty()) {
//                        return false;//invalid data
//                    }
//                    try {
//                        consoleName = inputText;
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        editConsoleName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                String inputText = editConsoleName.getText().toString();
//                if(!hasFocus) {
//                    if (inputText.isEmpty()) {
//                        inputText = "";
//                    }
//                    consoleName = inputText;
//                }
//
//            }
//        });
//
//        editModel.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int code, KeyEvent keyEvent) {
//                // if keydown and "enter" is pressed
//                String inputText;
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
//                    //do something
//                    //get the value from the text edit box and send it
//                    inputText = editModel.getText().toString();
//                    if(inputText.isEmpty()) {
//                        return false;//invalid data
//                    }
//                    try {
//                        model = inputText;
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        editModel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                String inputText = editModel.getText().toString();
//                if(!hasFocus) {
//                    if (inputText.isEmpty()) {
//                        inputText = "";
//                    }
//                    model = inputText;
//                }
//
//            }
//        });
//
//        editPartNumber.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int code, KeyEvent keyEvent) {
//                // if keydown and "enter" is pressed
//                String inputText;
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
//                    //do something
//                    //get the value from the text edit box and send it
//                    inputText = editPartNumber.getText().toString();
//                    if(inputText.isEmpty()) {
//                        return false;//invalid data
//                    }
//                    try {
//                        partNumber = Integer.parseInt(inputText);
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        editPartNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                String inputText = editPartNumber.getText().toString();
//                if(!hasFocus) {
//                    if (inputText.isEmpty()) {
//                        inputText = "0";
//                    }
//                    partNumber = Integer.parseInt(inputText);
//                }
//
//            }
//        });
//
//        editSoftwareVersion.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int code, KeyEvent keyEvent) {
//                // if keydown and "enter" is pressed
//                String inputText;
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
//                    //do something
//                    //get the value from the text edit box and send it
//                    inputText = editSoftwareVersion.getText().toString();
//                    if(inputText.isEmpty()) {
//                        return false;//invalid data
//                    }
//                    try {
//                        swVersion = Integer.parseInt(inputText);
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        editSoftwareVersion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                String inputText = editSoftwareVersion.getText().toString();
//                if(!hasFocus) {
//                    if (inputText.isEmpty()) {
//                        inputText = "0";
//                    }
//                    swVersion = Integer.parseInt(inputText);
//                }
//
//            }
//        });
//
//        editHardwareVersion.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int code, KeyEvent keyEvent) {
//                // if keydown and "enter" is pressed
//                String inputText;
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
//                    //do something
//                    //get the value from the text edit box and send it
//                    inputText = editHardwareVersion.getText().toString();
//                    if(inputText.isEmpty()) {
//                        return false;//invalid data
//                    }
//                    try {
//                        hwVersion = Integer.parseInt(inputText);
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        editHardwareVersion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                String inputText = editHardwareVersion.getText().toString();
//                if(!hasFocus) {
//                    if (inputText.isEmpty()) {
//                        inputText = "0";
//                    }
//                    hwVersion = Integer.parseInt(inputText);
//                }
//
//            }
//        });
//
//        editSerialNumber.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int code, KeyEvent keyEvent) {
//                // if keydown and "enter" is pressed
//                String inputText;
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
//                    //do something
//                    //get the value from the text edit box and send it
//                    inputText = editSerialNumber.getText().toString();
//                    if(inputText.isEmpty()) {
//                        return false;//invalid data
//                    }
//                    try {
//                        serialNumber = Integer.parseInt(inputText);
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        editSerialNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                String inputText = editSerialNumber.getText().toString();
//                if(!hasFocus) {
//                    if (inputText.isEmpty()) {
//                        inputText = "0";
//                    }
//
//                    serialNumber = Integer.parseInt(inputText);
//                }
//
//            }
//        });
//
//        editManNumber.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int code, KeyEvent keyEvent) {
//                // if keydown and "enter" is pressed
//                String inputText;
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
//                    //do something
//                    //get the value from the text edit box and send it
//                    inputText = editManNumber.getText().toString();
//                    if (inputText.isEmpty()) {
//                        return false;//invalid data
//                    }
//                    try {
//                        manufactureNumber = Integer.parseInt(inputText);
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        editManNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                String inputText = editManNumber.getText().toString();
//                if(!hasFocus) {
//                    if (inputText.isEmpty()) {
//                        inputText = "0";
//                    }
//
//                    manufactureNumber = Integer.parseInt(inputText);
//                }
//
//            }
//        });
//
//        editMaxIncline.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int code, KeyEvent keyEvent) {
//                // if keydown and "enter" is pressed
//                String inputText;
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
//                    //do something
//                    //get the value from the text edit box and send it
//                    inputText = editMaxIncline.getText().toString();
//                    if (inputText.isEmpty()) {
//                        return false;//invalid data
//                    }
//                    try {
//                        maxIncline = Integer.parseInt(inputText);
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        editMaxIncline.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                String inputText = editMaxIncline.getText().toString();
//                if(!hasFocus) {
//                    if (inputText.isEmpty()) {
//                        inputText = "0";
//                    }
//
//                    maxIncline = Integer.parseInt(inputText);
//                }
//
//            }
//        });
//
//        editMinIncline.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int code, KeyEvent keyEvent) {
//                // if keydown and "enter" is pressed
//                String inputText;
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
//                    //do something
//                    //get the value from the text edit box and send it
//                    inputText = editMinIncline.getText().toString();
//                    if (inputText.isEmpty()) {
//                        return false;//invalid data
//                    }
//                    try {
//                        minIncline = Integer.parseInt(inputText);
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        editMinIncline.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                String inputText = editMinIncline.getText().toString();
//                if(!hasFocus) {
//                    if (inputText.isEmpty()) {
//                        inputText = "0";
//                    }
//
//                    minIncline = Integer.parseInt(inputText);
//                }
//
//            }
//        });
//
//        editMaxSpeed.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int code, KeyEvent keyEvent) {
//                // if keydown and "enter" is pressed
//                String inputText;
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
//                    //do something
//                    //get the value from the text edit box and send it
//                    inputText = editMaxSpeed.getText().toString();
//                    if (inputText.isEmpty()) {
//                        return false;//invalid data
//                    }
//                    try {
//                        maxSpeed = Integer.parseInt(inputText);
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        editMaxSpeed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                String inputText = editMaxSpeed.getText().toString();
//                if(!hasFocus) {
//                    if (inputText.isEmpty()) {
//                        inputText = "0";
//                    }
//
//                    maxSpeed = Integer.parseInt(inputText);
//                }
//
//            }
//        });
//
//        editMinSpeed.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int code, KeyEvent keyEvent) {
//                // if keydown and "enter" is pressed
//                String inputText;
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        && (code == KeyEvent.KEYCODE_ENTER) || checkValsButton.isActivated()) {
//                    //do something
//                    //get the value from the text edit box and send it
//                    inputText = editMinSpeed.getText().toString();
//                    if (inputText.isEmpty()) {
//                        return false;//invalid data
//                    }
//                    try {
//                        minSpeed = Integer.parseInt(inputText);
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        editMinSpeed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                String inputText = editMinSpeed.getText().toString();
//                if(!hasFocus) {
//                    if (inputText.isEmpty()) {
//                        inputText = "0";
//                    }
//
//                    minSpeed = Integer.parseInt(inputText);
//                }
//
//            }
//        });
//        editEmail.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int code, KeyEvent keyEvent) {
//                // if keydown and "enter" is pressed
//                String inputText;
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                        && (code == KeyEvent.KEYCODE_ENTER) || emailButton.isPressed()) {
//                    //do something
//                    //get the value from the text edit box and send it
//                    inputText = editEmail.getText().toString();
//                    if (inputText.isEmpty()) {
//                        return false;//invalid data
//                    }
//                    try {
//                        emailAddress = inputText;
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        editEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                String inputText = editEmail.getText().toString();
//                if(!hasFocus) {
//                    if (inputText.isEmpty()) {
//                        inputText = "";
//                        Toast.makeText(TestApp.this, "Please enter an email", Toast.LENGTH_LONG).show();
//                    }
//                    emailAddress = inputText;
//                }
//
//            }
//        });
//
//
//    }
//
//    /**
//     * Called when a view has been clicked.
//     *
//     * @param v The view that was clicked.
//     */
//    @Override
////    public void onClick(View v)
////    {
////        testingView = (TextView) findViewById(R.id.testView);
////        resultView = (TextView)findViewById(R.id.passFailView);
////        //Press this button to check the values entered by the user
////        if(v == checkValsButton){
////
////            configString = "";      //clear out Configuration results for each time the Check Values button is pressed
////            //This is on TestIntegration class in SystemConfiguration method
////
////            systemString = "Console Name: \""+consoleName+"\"\nModel Number: \""+model+"\"\nPart Number: \""+
////                    partNumber+"\"\nSoftware Version: \""+swVersion+"\"\nHardware Version: \""+hwVersion+
////                    "\"\nSerial Number: \""+ serialNumber+"\"\nManufacturing Number: \""+manufactureNumber+
////                    "\"\nMax Incline: \""+maxIncline+"\"\nMin. Incline: \""+minIncline+"\"\nMax Speed: \""+maxSpeed+"\"\n" +
////                    "Min Speed: \""+minSpeed+"\"\n";
////
////            try{
////                TestIntegration system = new TestIntegration(this.fecpController,this,this.mSFitSysCntrl);
////               configString += system.testSystemConfiguration(systemString);
////
////                //try to write to the file in main from the machine control structure
////                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
////               outputStream.write((configString).getBytes());
////               outputStream.close();
////            } catch(Exception e){
////                e.printStackTrace();
////            }
////            testingView.setText(configString);
////            if(configString.contains("FAIL")) {
////                passFail = "<font color = #ff0000>FAIL </font>";
////            }
////            else {
////                passFail = "<font color = #00ff00>PASS </font>";
////            }
////            resultView.setText(Html.fromHtml(passFail));
////        }
////        else if(v==allTestsButton)
////        {
////
////            try{
////              final TestMotor t = new TestMotor(this.fecpController,this,this.mSFitSysCntrl);
////                //returnString = t.runAll();
////               // returnString = t.testCalories();
////                t.setUpdateResultViewListener(new TestMotor.UpdateResultView() {
////                    @Override
////                    public void onUpdate(final String msg) {
////                        runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                                testingView.setText(msg);
////                            }
////                        });
////
////                    }
////                });
////               Thread th = new Thread(new Runnable() {
////                   @Override
////                   public void run() {
////                       try {
////                           returnString = t.testStartSpeed();
////                       } catch (Exception e) {
////                           e.printStackTrace();
////                       }
////                   }
////               });
////                th.start();
////
////                //returnString= t.testSpeedController();
////               //returnString= t.testPwmOvershoot();
////               // returnString= t.testDistance();
////              //returnString= t.testModes("all");
////               //returnString= t.testPauseResume();
////              //TestIntegration ti = new TestIntegration(this.fecpController,this,this.mSFitSysCntrl);
////            //  returnString = ti.testRunningTime();
////                // returnString = ti.testAge();
////                // returnString = ti.testWeight();
////                //returnString = ti.testPauseIdleTimeout();
////                //returnString = ti.testMaxSpeedTime();
////               //TestBitfields tc = new TestBitfields(this.fecpController,this,this.mSFitSysCntrl);
////               //returnString= tc.runAll();
////                //returnString = tc.testBitfieldRdWr();
//////                returnString = tc.testBitfieldValuesValidation();
////              //TestPhysicalKeyCodes tpk = new TestPhysicalKeyCodes(this.fecpController,this,this.mSFitSysCntrl);
////              //  returnString=tpk.runAll();
////                //returnString = tpk.testStartKey();
////                //returnString = tpk.testStopKey();
////                //returnString = tpk.testQuickInclineKeys();
////                //TestTreadmillKeyCodes ttk = new TestTreadmillKeyCodes(this.fecpController,this,this.mSFitSysCntrl);
////                // returnString = ttk.testAllKeys();
////           //TestIncline tin = new TestIncline(this.fecpController,this,this.mSFitSysCntrl);
////               // returnString= tin.testInclineController();
////                //returnString= tin.testStopIncline();
////                //returnString = tin.testRetainedIncline();
////              //  returnString =tin.testSpeedInclineLimit();
////               // returnString = tin.testInclineRetentionDmkRecall();
////
////                returnString += "\n" + systemString;
////                //try to write to the file in main from the machine control structure
////                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
////                outputStream.write((returnString).getBytes());
////                outputStream.close();
////
////            } catch(Exception e){
////                e.printStackTrace();
////            }
////
////            testingView.setText(returnString);
////            if(returnString.isEmpty()){
////                passFail = "<font color = #ff0000>ERROR</font>";
////            }
////            else if(returnString.contains("FAIL")) {
////                passFail = "<font color = #ff0000>FAIL</font>";
////            }
////            else {
////                passFail = "<font color = #00ff00>PASS</font>";
////            }
////            resultView.setText(Html.fromHtml(passFail));
////        }
////        if(v == findFailButton) {
////            try{
////                String resultsString = testingView.getText().toString();
////                final String FAIL = "* FAIL *";
////                int failIndex = resultsString.indexOf(FAIL, 0);
////                Spannable WordToSpan = new SpannableString(testingView.getText());
////
////                for(int i = 0; i < resultsString.length() && failIndex != -1; i = failIndex+1) {
////                    failIndex = resultsString.indexOf(FAIL, i);
////                    if(failIndex == -1){
////                        break;
////                    }
////                    else{
////                        WordToSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00), failIndex, failIndex+FAIL.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////                        testingView.setText(WordToSpan, TextView.BufferType.SPANNABLE);
////                    }
////                }
////
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
////
////        if(v == emailButton) {
////            try {
////                if(editEmail.getText().toString().isEmpty()){
////                    Toast.makeText(this, "Please enter an email!", Toast.LENGTH_LONG).show();
////                }
////                else {
////                    new SendEmailAsyncTask(emailAddress).execute();
////                }
////            }catch(Exception e){
////                Toast.makeText(this, "Email was not sent.", Toast.LENGTH_LONG).show();
////                e.printStackTrace();
////            }
////
////        }
////
////        if(v == clearButton) {
////            try {
////                editEmail.setText("");
////            } catch (Exception e){
////                e.printStackTrace();
////            }
////
////        }
////
////        //Clear out the system device string to re-enter new values for next test
////        systemString = "";
////        //clear out print string to recheck values if needed from a typo
////        returnString = "";
////    }
//
//
//    /**
//     * Perform any final cleanup before an activity is destroyed.  This can
//     * happen either because the activity is finishing (someone called
//     * {@link #finish} on it, or because the system is temporarily destroying
//     * this instance of the activity to save space.  You can distinguish
//     * between these two scenarios with the {@link #isFinishing} method.
//     * <p/>
//     * <p><em>Note: do not count on this method being called as a place for
//     * saving data! For example, if an activity is editing data in a content
//     * provider, those edits should be committed in either {@link #onPause} or
//     * {@link #onSaveInstanceState}, not here.</em> This method is usually implemented to
//     * free resources like threads that are associated with an activity, so
//     * that a destroyed activity does not leave such things around while the
//     * rest of its application is still running.  There are situations where
//     * the system will simply kill the activity's hosting process without
//     * calling this method (or any others) in it, so it should not be used to
//     * do things that are intended to remain around after the process goes
//     * away.
//     * <p/>
//     * <p><em>Derived classes must call through to the super class's
//     * implementation of this method.  If they do not, an exception will be
//     * thrown.</em></p>
//     *
//     * @see #onPause
//     * @see #onStop
//     * @see #finish
//     * @see #isFinishing
//     */
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //kill all threads
//        if(mToast != null)
//        {
//            mToast.cancel();
//        }
//    }
//
//    /**
//     * Called as part of the activity lifecycle when an activity is going into
//     * the background, but has not (yet) been killed.  The counterpart to
//     * {@link #onResume}.
//     * <p/>
//     * <p>When activity B is launched in front of activity A, this callback will
//     * be invoked on A.  B will not be created until A's {@link #onPause} returns,
//     * so be sure to not do anything lengthy here.
//     * <p/>
//     * <p>This callback is mostly used for saving any persistent state the
//     * activity is editing, to present a "edit in place" model to the user and
//     * making sure nothing is lost if there are not enough resources to start
//     * the new activity without first killing this one.  This is also a good
//     * place to do things like stop animations and other things that consume a
//     * noticeable amount of CPU in order to make the switch to the next activity
//     * as fast as possible, or to close resources that are exclusive access
//     * such as the camera.
//     * <p/>
//     * <p>In situations where the system needs more memory it may kill paused
//     * processes to reclaim resources.  Because of this, you should be sure
//     * that all of your state is saved by the time you return from
//     * this function.  In general {@link #onSaveInstanceState} is used to save
//     * per-instance state in the activity and this method is used to store
//     * global persistent data (in content providers, files, etc.)
//     * <p/>
//     * <p>After receiving this call you will usually receive a following call
//     * to {@link #onStop} (after the next activity has been resumed and
//     * displayed), however in some cases there will be a direct call back to
//     * {@link #onResume} without going through the stopped state.
//     * <p/>
//     * <p><em>Derived classes must call through to the super class's
//     * implementation of this method.  If they do not, an exception will be
//     * thrown.</em></p>
//     *
//     * @see #onResume
//     * @see #onSaveInstanceState
//     * @see #onStop
//     */
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if(mToast != null) {
//                        mToast.cancel();
//        }
//    }
//
//    /**
//     * this method is called when the system is disconnected.
//     */
//    @Override
//    public void systemDisconnected() {
//
//        //change display back to the original screen
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if(null == mToast)
//                {
//                    mToast = Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_LONG);
//                }
//                mToast.setDuration(Toast.LENGTH_LONG);
//                mToast.setText("Connection Lost");
//
//                mToast.show();
////                Toast.makeText(getApplicationContext(),"Connection Lost", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    /**
//     * This is called after system is connected
//     *
//     * @param dev the System device that is connected.
//     */
//    @Override
//    public void systemDeviceConnected(final SystemDevice dev) {
//
//        //if successful the dev won't be null
//        //system is connected used this in the rest of the system.
//
//        if(dev == null || dev.getInfo().getDevId() == DeviceId.NONE)
//        {
//            Toast.makeText(this,"Connection Failed",Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if(null == mToast)
//                {
//                    mToast = Toast.makeText(getApplicationContext(), "Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getSysDevInfo().getConsoleName(), Toast.LENGTH_LONG);
//                }
////                Toast.makeText(getApplicationContext(),"Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getConsoleName() , Toast.LENGTH_LONG).show();
//                mToast.setText("Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getSysDevInfo().getConsoleName() );
//                mToast.setDuration(Toast.LENGTH_LONG);
//                mToast.show();
//            }
//        });
//
//        //check if treadmill or incline trainer
//        if(dev.getInfo().getDevId() == DeviceId.INCLINE_TRAINER || dev.getInfo().getDevId() == DeviceId.TREADMILL)
//        {
//            if(dev.getInfo().getSupportedBitfields().contains(BitFieldId.WORKOUT_MODE)) {
//
//                try {
//
//                    this.mSystemStopCmd = new FecpCommand(dev.getCommand(CommandId.WRITE_READ_DATA));
//                    ((WriteReadDataCmd)this.mSystemStopCmd.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, ModeId.PAUSE);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    /**
//     * This will be called when the communication layer is connected. this is a lower level of
//     * communication notification.
//     */
//    @Override
//    public void systemCommunicationConnected() {
//        //nothing to do, implemented in fitpro layer
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (null == mToast) {
//                    mToast = Toast.makeText(getApplicationContext(), "Connected ", Toast.LENGTH_LONG);
//                }
////                Toast.makeText(getApplicationContext(),"Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getConsoleName() , Toast.LENGTH_LONG).show();
//                mToast.setText("Connected ");
//                mToast.setDuration(Toast.LENGTH_LONG);
//                mToast.show();
//            }
//        });
//    }
//
//    @Override
//    public void systemSecurityValidated() {
//
////        //system is validated you may control the system
////        this.runOnUiThread(new Runnable() {
////            @Override
////            public void run() {
////                if (null == mToast) {
////                    mToast = Toast.makeText(getApplicationContext(), "System is Validated", Toast.LENGTH_LONG);
////                }
//////                Toast.makeText(getApplicationContext(),"Connected to "+ dev.getInfo().getDevId().getDescription()+ ":" + dev.getConsoleName() , Toast.LENGTH_LONG).show();
////                mToast.setText("System is Validated");
////                mToast.setDuration(Toast.LENGTH_LONG);
////                mToast.show();
////            }
////        });
////        this.mSFitSysCntrl.getInitialSysItems(this, 0, 0);
//    }

}
