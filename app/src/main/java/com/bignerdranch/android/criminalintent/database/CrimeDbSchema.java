package com.bignerdranch.android.criminalintent.database;

/**
 * Created by mike on 9/9/17.
 * Scheme for crimes table
 */

public class CrimeDbSchema {

    // table name
    public static final class CrimeTable{
        public static final String NAME = "crimes";

        // column descriptions
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
        }
    }


}
