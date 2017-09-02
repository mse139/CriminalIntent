package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by mike on 8/21/17.
 */

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecylerView;
    private CrimeAdapter mAdapter;
    private final int ITEM_CRIME_POLICE = 0;
    private final int ITEM_CRIME =1;
    private final String TAG = "CrimeListFragment";
    private static final int REQUEST_CRIME = 1;
    private int lastClickedRow = -1;

    @Override
    //create and inflate the fragment to be returned
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);

        mCrimeRecylerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI () {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null ){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecylerView.setAdapter(mAdapter);
        } else {
            if (lastClickedRow >=0){
                mAdapter.notifyItemChanged(lastClickedRow);
                lastClickedRow = -1;
            } else
                mAdapter.notifyDataSetChanged();

        }

    }
//view holder - gets references to actual view and sets listeners.
    private class CrimeHolder extends ViewHolder implements View.OnClickListener{

        // variables for data binding
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;       // for each time a new crime is displayed
        private ImageView mSolvedImageView; // handcuff icon

        public CrimeHolder(LayoutInflater inflater,ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime,parent,false));

            mTitleTextView  = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);

            itemView.setOnClickListener(this);


        }







        // binding a crime item each time it is needed for the UI
        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(DateFormat.format("EEEE, MMMM dd, yyyy",mCrime.getDate()));
            mSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE: View.GONE);
        }
    }

    private class CrimeHolderPolice extends ViewHolder implements View.OnClickListener {

        // variables for data binding

        private Crime mCrime;       // for each time a new crime is displayed

        public CrimeHolderPolice(LayoutInflater inflater, ViewGroup parent) {
        //public CrimeHolderPolice(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_crime_police, parent, false));
            itemView.setOnClickListener(this);


        }


        //  click listener when user touches a row
        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(),
                    mCrime.getTitle() + " clicked!",
                    Toast.LENGTH_SHORT).show();
        }

        // binding a crime item each time it is needed for the UI
        public void bind(Crime crime) {
            mCrime = crime;

        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        // returns the number of views available
        // related to final int's created above
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        //determines which view to show in list
        public int getItemViewType(int position) {

            // check if police flag is true to determine
            // which layout to send
            if(mCrimes.get(position).isRequiresPolice()){
                // display the police button layout
                return ITEM_CRIME_POLICE;
            } else
                return ITEM_CRIME;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("onCreateViewHolder",Integer.toString(viewType));

           LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {

            // bind the newest record
            Crime crime=  mCrimes.get(position);// get the record
            holder.bind(crime);     // bind it
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }


    }
}
