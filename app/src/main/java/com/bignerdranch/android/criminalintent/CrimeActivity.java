package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CrimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime);

        // fragment manager has collection of all fragments
        FragmentManager fm = getSupportFragmentManager();
        // get teh crimeFragment
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        // create it if necessary
        if (fragment == null) {
            fragment = new CrimeFragment();
            fm.beginTransaction().add(R.id.fragment_container,fragment).commit();
        }
    }
}
