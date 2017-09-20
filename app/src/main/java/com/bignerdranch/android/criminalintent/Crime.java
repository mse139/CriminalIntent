package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by mike on 8/20/17.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private String mSuspect;
    private Date mDate;
    private boolean mSolved;
    private boolean mRequiresPolice;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public Crime() {
       this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }


    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }

    // gets the filename of the photo
    public String getPhotoFileName() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
