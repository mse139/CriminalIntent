package com.bignerdranch.android.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewGroupCompat;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by mike on 8/20/17.
 */

public class CrimeFragment extends Fragment {

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME  = 1;
    private static final String TAG = "CrimeFragment";
    private static  SimpleDateFormat mTimeFormat;

    // oncreate bundle override
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // retrieve the crimeID from the extra data
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    // view is inflated below
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // setup the time format
        mTimeFormat = new SimpleDateFormat("h:mm a");

        View v = inflater.inflate(R.layout.fragment_crime,container,false);
        v.setPadding(16,16,16,16);
        // init the titleField and add listener
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // init the date button
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();




        // set the date button listener
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // init the time button
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        setTime();



        // set the time listener
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open the time picker
                FragmentManager fm = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
                dialog.show(fm,DIALOG_TIME);

            }
        });

        // init the solved checkbox
        mSolvedCheckBox  = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });



        return v;
    }

    //onPause override
    @Override
    public void onPause() {
        super.onPause();

        // update the crime in the db
        CrimeLab.get(getActivity()).updateCrime(mCrime);

    }

    // get the menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        menu.findItem(R.id.new_crime).setVisible(false);
        menu.findItem(R.id.show_subtitle).setVisible(false);


    }

    // options selected override
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).removeCrime(mCrime);

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setTime() {
        mTimeButton.setText(mTimeFormat.format(mCrime.getDate()));
    }

    // override of activity result to get the date from the picker
    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        // if the date is coming back
        if(requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            // set the button label
            updateDate();
        }


        // if time is coming  back
        if(requestCode == REQUEST_TIME) {

            // get the time from the intent
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
           setTime();

        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getTime() {
       return  mTimeFormat.format(mCrime.getDate());
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        // create the fragment
        CrimeFragment fragment = new CrimeFragment();
        // set teh arguments
        fragment.setArguments(args);
        // return the new fragment
        return fragment;
    }

    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK,null);
    }
}
