package com.bignerdranch.android.criminalintent;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
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
    private static final String ARG_CONTACT_ID = "contact_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME  = 1;
    private static final String TAG = "CrimeFragment";
    private static  SimpleDateFormat mTimeFormat;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private String mContactID;

    // oncreate bundle override
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // retrieve the crimeID from the extra data
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        String contactID = getArguments().getString(ARG_CONTACT_ID);
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

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        final Intent callContact = new Intent(Intent.ACTION_DIAL);

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

        // init and set listener on crime report button
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create the intent to send the report using the builder
                ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(getActivity());

                builder.setText(getCrimeReport()).setType("text/plain")
                        .setSubject(getString(R.string.send_report))
                .createChooserIntent();

                startActivity(builder.getIntent());
            }
        });

        // init and set listener on contact button
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);

        // check to see if there is a contacts app and disable button if there isn't
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY) == null){
            mCallButton.setEnabled(false);
            mSuspectButton.setEnabled(false);
        }

        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        // setup the call button
        mCallButton = (Button)v.findViewById(R.id.crime_call_suspect);
        if(packageManager.resolveActivity(callContact,PackageManager.MATCH_DEFAULT_ONLY) == null){
            mCallButton.setEnabled(false);
        }

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mContactID == null)
                    mContactID  = searchContacts();
                // get the phone number and set the dialer
                Uri number = Uri.parse("tel:"+getPhoneNumber());
               callContact.setData(number);

                startActivity(callContact);


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
        // getting a contact
        if(requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // specify fields we want returned
            String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,};

            // perform the query
            Cursor c = getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);

            try {
                // double check to see if we got results
                if(c.getCount() == 0) {
                    return;

                }

                // pull out the first column of the first row
                c.moveToFirst();
                String suspect = c.getString(0);
                mContactID = c.getString(2);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();

            }
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

    // creates crime report
    private String getCrimeReport() {
        String solvedString = null;
        if(mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();

        if(suspect == null ) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect);
        }

        String report = getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);

        return report;
    }

    private String getPhoneNumber() {
        String result = new String();
        // query the phone data to get phone number
        String queryFields[] = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String whereClause = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + "=?";
        String whereArgs[] = {mContactID};
        ContentResolver cr = getActivity().getContentResolver();

        // run the query
        Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                queryFields, whereClause,whereArgs,null,null);

        // process the results
        try {
            if (c.getCount() == 0)
                return null;

            c.moveToFirst();
            result = c.getString(0);
            Log.d(TAG,"getPhone got phone: " + result);

        } finally {
            c.close();
        }



        return result;
    }

    private String searchContacts() {
        String id = new String();

        // search the contacts by name
        String[] queryFields = {ContactsContract.Contacts.LOOKUP_KEY};
        String whereClause = ContactsContract.Contacts.DISPLAY_NAME + "=?";

        String[] whereArgs = {mCrime.getSuspect()};

        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,queryFields,whereClause,whereArgs,null);
        // we will assume only 1 contact returned and only use the first result
        try {
            if(cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();
            id = cursor.getString(0);
            Log.d(TAG,"serach result id is " + id);
        } finally {
            cursor.close();
        }

        return id;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        savedInstance.putSerializable(ARG_CONTACT_ID,mContactID);
    }
}
