package com.ifit.sfit.sparky.helperclasses;

import android.app.Activity;
import android.text.Html;
import android.widget.ScrollView;

import com.ifit.sfit.sparky.testsdrivers.BaseTest;


/**
 * Created by jc.almonte on 7/30/14.
 * Common features shared among all test classes
 */
public abstract class CommonFeatures extends Activity {

    /**
     * Takes care of displaying on screen the test results sent through "msg" param
     * @param msg
     */

    public void appendMessage(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
              BaseTest.testingView.append(Html.fromHtml(msg));
              BaseTest.scrollview.post(new Runnable() {
                   @Override
                   public void run() {
                       BaseTest.scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                   }
               });
            }
        });
    }

    /**
     * Used to run all tests for each class.
     * Declared abstract since every TestClass runs a different set of tests
     * @return text log of test results
     * @throws Exception
     */
   public abstract String runAll() throws Exception;
}
