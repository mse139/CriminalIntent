package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by mike on 8/21/17.
 */

public class CrimeListActivity
        extends SingleFragmentActivity  implements CrimeFragment.CallBacks , CrimeListFragment.Callbacks{


    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    /**
     * onCrimeSelected - determine which layout should be used
     * @param crime
     */
    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detail_fragment_container) == null) {
            // start a new activity in the foreground
            Intent intent = CrimePagerActivity.newItent(this,crime.getId());
            startActivity(intent);
        } else {    // add activity to the detail view
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container,newDetail)
                    .commit();
        }
    }

    /**
     * onCrimeUpdated - updates the crime list after a crime is updated
     * @param crime
     */
    @Override
    public void onCrimeUpdated(Crime crime) {

        // get the CrimeListFragment that is active and call updateUI()
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    @Override
    public void onCrimeRemoved(Crime crime) {
        Log.d("CrimeListAct","onCrimeRemoved called");
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}


