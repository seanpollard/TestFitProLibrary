package com.ifit.sparkydevapp.sparkydevapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ifit.sparky.fecp.communication.FecpController;
import com.ifit.sparkydevapp.sparkydevapp.R;


public class TaskInfoFragment extends BaseInfoFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "task_info_id";
    public static final String DISPLAY_STRING = "Task Info";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     *
     * @param fecpCntrl
     */
    public TaskInfoFragment(FecpController fecpCntrl) {
        super(fecpCntrl, TaskInfoFragment.DISPLAY_STRING, TaskInfoFragment.ARG_ITEM_ID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.system_task_info, container, false);

        // Show the dummy content as text in a TextView.
        ((TextView) rootView.findViewById(R.id.textViewSystemTask)).setText("Tasks: " + this.mFecpCntrl.getSysDev().getSysDevInfo().getNumberOfTasks());

        return rootView;
    }

    /**
     * These are the commands that we will be using on the startup
     */
    @Override
    public void addFragmentFecpCommands() {

    }

    /**
     * These are the commands that well be removed when we switch fragments
     */
    @Override
    public void deleteFragmentFecpCommands() {

    }
}
