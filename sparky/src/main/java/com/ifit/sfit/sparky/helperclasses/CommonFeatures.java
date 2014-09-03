package com.ifit.sfit.sparky.helperclasses;

import android.app.Activity;
import android.text.Html;
import android.widget.ScrollView;

import com.ifit.sfit.sparky.testsdrivers.BaseTest;


/**
 * Created by jc.almonte on 7/30/14.
 * This class will have the common features shared among all test classes
 */
public abstract class CommonFeatures extends Activity {
//    protected UpdateResultView listener;
//    protected String res = "";
//    public void setUpdateResultViewListener(UpdateResultView listener) {
//        this.listener = listener;
//    }
//    public interface UpdateResultView {
//        public void onUpdate(String msg);
//    }

/*
*
* This method will take care of displaying test results on screen
* @param msg --> message to be displayed on screen
*
* */

    public void appendMessage(final String msg) {
        //res += msg;
        //listener.onUpdate(msg);
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

    /*
*
* This method will be used to run all tests for each class. Declared abstract since
* every TestClass runs a different set of tests
*
* @return String --> test results
*
* */
   public abstract String runAll() throws Exception;
}
