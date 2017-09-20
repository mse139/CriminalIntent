package com.bignerdranch.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.net.URI;

/**
 * Created by mike on 9/18/17.
 */

public class ViewPhotoFragment extends DialogFragment {

    ImageView mImageView;
    private Uri uri;

    private final static String TAG = "ViewPhotoFragment";
    private final static String ARG_URI = "uri";


    @Override
    public Dialog onCreateDialog(Bundle savedInstance){

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_image,null);

        // reconstitue the uri
        uri = Uri.parse(getArguments().getString(ARG_URI));

        mImageView = (ImageView) v.findViewById(R.id.image_full);
        mImageView.setImageURI(uri);

        return new AlertDialog.Builder(getActivity()).setView(v)
                .setTitle(getString(R.string.crime_image_view_title)).create()
                ;


    }

    public static ViewPhotoFragment newInstance(Uri uri) {

        ViewPhotoFragment instance = new ViewPhotoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_URI,uri.toString());
        instance.setArguments(args);


        return instance;
    }
}
