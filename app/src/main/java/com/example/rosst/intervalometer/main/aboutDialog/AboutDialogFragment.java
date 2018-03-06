package com.example.rosst.intervalometer.main.aboutDialog;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rosst.intervalometer.R;


public class AboutDialogFragment extends DialogFragment{

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_dialog, container, false);

        onActivityCreated(savedInstanceState);
        return v;
    }
}
