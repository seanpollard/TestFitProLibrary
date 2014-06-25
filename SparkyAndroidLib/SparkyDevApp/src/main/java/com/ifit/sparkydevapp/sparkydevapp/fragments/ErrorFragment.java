package com.ifit.sparkydevapp.sparkydevapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparky.fecp.error.ErrorEventListener;
import com.ifit.sparky.fecp.error.SystemError;
import com.ifit.sparkydevapp.sparkydevapp.R;

import java.util.LinkedList;

public class ErrorFragment extends BaseInfoFragment implements ErrorEventListener, Runnable{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "error_info_id";
    public static final String DISPLAY_STRING = "Error Device Info";

    private TextView mTextViewErrorInfo;
    private LinkedList<SystemError> mErrorList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     *
     * @param fecpCntrl
     */
    public ErrorFragment(FecpController fecpCntrl) {
        super(fecpCntrl, ErrorFragment.DISPLAY_STRING, ErrorFragment.ARG_ITEM_ID);
        this.mErrorList = new LinkedList<SystemError>();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.error_info, container, false);

        this.mTextViewErrorInfo = ((TextView) rootView.findViewById(R.id.textViewErrorInfo));
        // Show the dummy content as text in a TextView.
        this.mTextViewErrorInfo.setText("Errors ");
        this.addFragmentFecpCommands();
        return rootView;
    }

    /**
     * These are the commands that we will be using on the startup
     */
    @Override
    public void addFragmentFecpCommands() {
        //add the listener for errors that occur
        this.mFecpCntrl.addOnErrorEventListener(this);
    }

    /**
     * These are the commands that well be removed when we switch fragments
     */
    @Override
    public void deleteFragmentFecpCommands() {
        this.mFecpCntrl.removeOnErrorEventListener(this);
    }

    /**
     * This will notify anyone that an error has occurred with the system
     *
     * @param error the error that occurred.
     */
    @Override
    public void onErrorEventListener(SystemError error) {
        //update the display
        this.mErrorList.add(error);
        if(this.mErrorList.size() > 10)
        {
            this.mErrorList.removeFirst();
        }
        this.getActivity().runOnUiThread(new Thread(this));
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        //update the gui to display the new errors that occurred
        String errString = "Errors \n";

        for (SystemError error : this.mErrorList) {
            errString += error.toString();
        }

        this.mTextViewErrorInfo.setText(errString);
    }
}
