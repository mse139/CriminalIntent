package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);



        mCrimeRecylerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI () {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecylerView.setAdapter(mAdapter);
    }

    private class CrimeHolder extends ViewHolder implements View.OnClickListener{

        // variables for data binding
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;       // for each time a new crime is displayed

        public CrimeHolder(View view) {
            //  public CrimeHolder(LayoutInflater inflater,ViewGroup parent) {
            super(view);
            // super(inflater.inflate(R.layout.list_item_crime,parent,false));

            mTitleTextView  = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);

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
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
        }
    }

    private class CrimeHolderPolice extends ViewHolder implements View.OnClickListener {

        // variables for data binding

        private Crime mCrime;       // for each time a new crime is displayed

        // public CrimeHolderPolice(LayoutInflater inflater, ViewGroup parent) {
        public CrimeHolderPolice(View view) {
            super(view);
            // super(inflater.inflate(R.layout.list_item_crime_police, parent, false));
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

    private class CrimeAdapter extends RecyclerView.Adapter<ViewHolder> {
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("onCreateViewHolder",Integer.toString(viewType));
            View view;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            if(viewType == ITEM_CRIME_POLICE) {

                view = inflater.inflate(R.layout.list_item_crime_police, parent, false);
                return new CrimeHolderPolice(view);
            } else
            if (viewType == ITEM_CRIME) {

                view = inflater.inflate(R.layout.list_item_crime, parent, false);
                return new CrimeHolder(view);
            }

            return null;// takes care of return error


        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            final int itemType = getItemViewType(position); // UI type
            // bind the newest record
            Crime crime=  mCrimes.get(position);// get the record
            if(itemType == ITEM_CRIME) {
                ((CrimeHolder)holder).bind(crime);
            } else if (itemType == ITEM_CRIME_POLICE)
                ((CrimeHolderPolice)holder).bind(crime);


        }



        @Override
        public int getItemCount() {
            return mCrimes.size();
        }


    }
}
