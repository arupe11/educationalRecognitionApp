package com.example.andrea.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * Created by Andrea on 27/01/2018.
 */

public class MyDialogFragment extends DialogFragment{





    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = null;

        if (SequenceActivity.resultado == "end") {
            rootView = inflater.inflate(R.layout.fragment_dialog, container, false);
            getDialog().setTitle("¡Enhorabuena!");
        /*    button=(Button)rootView.findViewById(R.id.menu);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    getActivity().setContentView(R.layout.activity_welcome);

                }
            });*/





        }

       else if (SequenceActivity.resultado == "fail") {
            rootView = inflater.inflate(R.layout.fragmentfail_dialog, null, false);
            getDialog().setTitle("¡Vaya!");

        }

        else if (SequenceActivity.resultado == "colorIsFalse") {
            rootView = inflater.inflate(R.layout.fragment_color_fail, null, false);
            getDialog().setTitle("¡Ups!");

        }


        return rootView;
    }


}





