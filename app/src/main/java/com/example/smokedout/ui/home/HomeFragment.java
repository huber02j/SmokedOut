package com.example.smokedout.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smokedout.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private String dateString;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initial Date information to display
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateString = dateFormat.format(calendar.getTime());


        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Display Date at top
        final TextView dateTimeDisplay = root.findViewById(R.id.dayDisplay);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                dateTimeDisplay.setText(dateString);
            }
        });

        // Next day button
        final FloatingActionButton buttonRight = root.findViewById(R.id.floatingActionButtonRight);
        buttonRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Date currDate = null;
                try {
                    currDate = new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date newDate = changeDay(currDate,1);

                dateString = dateFormat.format(newDate);
                dateTimeDisplay.setText(dateString);
            }
        });

        // Previous day button
        final FloatingActionButton buttonLeft = root.findViewById(R.id.floatingActionButtonLeft);
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Date currDate = null;
                try {
                    currDate = new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date newDate = changeDay(currDate,0);

                dateString = dateFormat.format(newDate);
                dateTimeDisplay.setText(dateString);
            }
        });

        return root;
    }

    // Change day either forward or back: 1 = forward, 0 = backwards
    public Date changeDay(Date date, int direction) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if (direction == 1) {
            c.add(Calendar.DATE, 1);
        } else {
            c.add(Calendar.DATE, -1);
        }
        Date newDate = c.getTime();
        return newDate;
    }
}