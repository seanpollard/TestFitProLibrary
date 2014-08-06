package com.ifit.sfit.sparky.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ifit.sfit.sparky.R;
import com.ifit.sfit.sparky.testsdrivers.AllTests;
import com.ifit.sfit.sparky.testsdrivers.BitfieldsTest;
import com.ifit.sfit.sparky.testsdrivers.InclineTest;
import com.ifit.sfit.sparky.testsdrivers.IntegrationTest;
import com.ifit.sfit.sparky.testsdrivers.MotorTest;
import com.ifit.sfit.sparky.testsdrivers.TreadmillKeyCodesTest;

/**
 * Created by jc.almonte on 7/29/14.
 */
public class ManageTests extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init()
    {
        findViewById(R.id.bMotor).setOnClickListener(this);
        findViewById(R.id.bBitfields).setOnClickListener(this);
        findViewById(R.id.bIncline).setOnClickListener(this);
        findViewById(R.id.bIntegration).setOnClickListener(this);
        findViewById(R.id.bTreadmilllKeys).setOnClickListener(this);
        findViewById(R.id.bAllTests).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

            case R.id.bMotor:
                Intent motorTest = new Intent(ManageTests.this, MotorTest.class);
                startActivity(motorTest);
            break;

            case R.id.bIncline:
            Intent inclineTest = new Intent(ManageTests.this, InclineTest.class);
            startActivity(inclineTest);
            break;

            case R.id.bIntegration:
                Intent integrationTest = new Intent(ManageTests.this, IntegrationTest.class);
                startActivity(integrationTest);
            break;

            case R.id.bBitfields:
                Intent bitfieldsTest = new Intent(ManageTests.this, BitfieldsTest.class);
                startActivity(bitfieldsTest);
            break;

            case R.id.bTreadmilllKeys:
                Intent treadmillKeysTest = new Intent(ManageTests.this, TreadmillKeyCodesTest.class);
                startActivity(treadmillKeysTest);
            break;

            case R.id.bAllTests:
                Intent allTests = new Intent(ManageTests.this, AllTests.class);
                startActivity(allTests);
            break;

        }
    }
}
