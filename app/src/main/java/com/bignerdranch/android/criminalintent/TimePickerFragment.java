package com.bignerdranch.android.criminalintent;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by mike on 9/6/17.
 */

public class TimePickerFragment extends DialogFragment {

    private static final String ARG_TIME = "time";
    private static final String TAG = "TimePickerFragment";
    public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";

    // time picker object
    private TimePicker mTimePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // get the view
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        // get the time argument
        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        // setup the time picker and set the time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        // get the time picker
        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
        mTimePicker.setIs24HourView(false);
        mTimePicker.setHour(hour);
        mTimePicker.setMinute(minute);



        return new AlertDialog.Builder(getActivity()).setView(v)
                .setTitle("Time of Crime")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int minute = mTimePicker.getMinute();
                        int hour = mTimePicker.getHour();
                        Calendar cal =  Calendar.getInstance();
                        cal.set(Calendar.HOUR ,hour);
                        cal.set(Calendar.MINUTE,minute);

                       Date date = cal.getTime();
                        Log.d(TAG,"time set is: " + date.toString());
                        sendResult(Activity.RESULT_OK,date);
                    }
                })
                .create();

    }


    // new instance
    public static TimePickerFragment newInstance(Date date) {

        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME,date);
        fragment.setArguments(args);
        Log.d(TAG,"creating with date " + date);
        return fragment;



    }

    // send back the time
    private void sendResult(int resultCode,Date date) {
        if (getTargetFragment() == null)
            return;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);

    }


}
