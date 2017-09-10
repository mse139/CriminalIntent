package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mike on 8/21/17.
 */

public  class CrimeLab {

    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static CrimeLab get(Context context) {
        if (sCrimeLab == null ){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {

        mContext = context.getApplicationContext();
        // get a reference to the database
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

        /*
        for (int i = 0; i < 100; i++) {
                Crime crime = new Crime();
                crime.setTitle("Crime #" + i);
                crime.setSolved(i%2 == 0);  // every other one is solved
                crime.setRequiresPolice(i%2 > 0);
                mCrimes.add(crime);
            }
            */
    }

    public List<Crime> getCrimes() {

        List<Crime> crimes = new ArrayList<>();

        // query the database
        CrimeCursorWrapper cursor = queryCrimes(null,null);

        // iterate thru the cursor to load the list
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }  finally {
            cursor.close();
        }


        return crimes;
    }

    public Crime getCrime(UUID id) {

        // query with UUId

        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + "=?" ,
                new String[] {id.toString()}
                );

        // try to retrieve the results
        try {
            if(cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();

        } finally {
            cursor.close();
        }

    }

    public void addCrime(Crime c) {

        // buld the content values
        ContentValues values = getContentValues(c);

        // insert the db
        mDatabase.insert(CrimeTable.NAME,null,values);

    }

    public void removeCrime(Crime c) {

    }

    // build contentvalue obj from a crime for the db
    private static ContentValues getContentValues(Crime crime) {

        ContentValues values = new ContentValues();
        // build the bundle
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE,crime.getTitle());
        values.put(CrimeTable.Cols.DATE,crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED,crime.isSolved() ? 1: 0);


        return values;
    }

    // update database rows
    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();

        ContentValues values = getContentValues(crime);

        // update the db, using the uuid string as where clause
        mDatabase.update(CrimeTable.NAME,values,CrimeTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }

    // read db rows
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // all columns - null performs select *
                whereClause,
                whereArgs,
                null,
                null,
                null);

        // return a wrapper with the cursor to get teh values easily
        return new CrimeCursorWrapper(cursor);

    }

    // delete rows from the db
    public void deleteCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID + "=?",new String[]{uuidString});
    }
}
