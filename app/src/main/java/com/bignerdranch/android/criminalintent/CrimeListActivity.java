package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by mike on 8/21/17.
 * Fragment activity that will return the CrimeListFragment adapter
 */

public class CrimeListActivity
        extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}


