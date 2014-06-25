/**
 * All Fragments will inherit from this fragment.
 * @author Levi.Balling
 * @date 5/20/2014
 * @version 1
 * Details.
 */
package com.ifit.sfit.sparky.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ifit.sfit.sparky.R;
import com.ifit.sfit.sparky.SFitSysCntrl;
import com.ifit.sparky.fecp.FecpCommand;

import java.util.ArrayList;
import java.util.Collection;

public class BaseFragment extends Fragment {


    protected SFitSysCntrl mSFitSysCntrl;
    protected int mFragmentId;
    protected ArrayList<FecpCommand> mFitProPeriodicCommands;// Periodic Commands specific to the Fragment

    public BaseFragment(SFitSysCntrl fitSysCntrl, int fragmentId)
    {
        this.mSFitSysCntrl = fitSysCntrl;
        this.mFragmentId = fragmentId;
        this.mFitProPeriodicCommands = new ArrayList<FecpCommand>();
    }

    public BaseFragment(SFitSysCntrl fitSysCntrl, int fragmentId, Collection<FecpCommand> cmds)
    {
        this.mSFitSysCntrl = fitSysCntrl;
        this.mFragmentId = fragmentId;
        this.mFitProPeriodicCommands = new ArrayList<FecpCommand>();
        this.mFitProPeriodicCommands.addAll(cmds);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(android.os.Bundle)} and {@link #onActivityCreated(android.os.Bundle)}.
     * <p/>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        if(this.mSFitSysCntrl == null)
        {
            view = inflater.inflate(R.layout.idle_fragment, container, false);//default fragment
        }
        else {
            view = inflater.inflate(this.mFragmentId, container, false);
        }
        return view;
    }


    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after {@link #onStop()} and before {@link #onDestroy()}.  It is called
     * <em>regardless</em> of whether {@link #onCreateView} returned a
     * non-null view.  Internally it is called after the view's state has
     * been saved but before it has been removed from its parent.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //remove and commands
        this.removePeriodicCommands();//may do nothing
    }

    /**
     * Adds commands to the FitPro Controller
     */
    public void addPeriodicCommands()
    {

    }

    /**
     * Removes all of the commands that are in the list.
     */
    public void removePeriodicCommands()
    {


    }

}
