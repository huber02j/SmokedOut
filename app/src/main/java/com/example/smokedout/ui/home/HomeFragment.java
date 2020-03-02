package com.example.smokedout.ui.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smokedout.GoalInfo;
import com.example.smokedout.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private String dateString;
    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseGoalInfo;


    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initial Date information to display
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateString = dateFormat.format(calendar.getTime());
        databaseGoalInfo = FirebaseDatabase.getInstance().getReference("GoalInfo");
        firebaseAuth = FirebaseAuth.getInstance();

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Display date at top
        final TextView dateTimeDisplay = root.findViewById(R.id.dayDisplay);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                dateTimeDisplay.setText(dateString);
            }
        });

        // Array for all user's goals (only goal names for now)
        final ArrayList<String> allGoals = new ArrayList<String>();
        DatabaseReference databaseUserGoal = databaseGoalInfo.child(firebaseAuth.getUid());

        // Display all goals in edit text
        databaseUserGoal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Get new goal view
                final LinearLayout verticalLayout = root.findViewById(R.id.verticalGoalLayout);

                for (DataSnapshot goal : snapshot.getChildren()) {

                    // Get name of goal
                    String goalInfo = (String) goal.child("name").getValue();
                    allGoals.add(goalInfo);

                    View newGoalView = inflater.inflate(R.layout.new_goal, verticalLayout, false);

                    ProgressBar progressBar = newGoalView.findViewById(R.id.progressBarGoal);
                    progressBar.incrementProgressBy(10);

                    TextView goalText = newGoalView.findViewById(R.id.textViewGoal);
                    goalText.setText(goalInfo);

                    verticalLayout.addView(newGoalView, verticalLayout.getChildCount());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        // Add new Goal button
        /*
        final FloatingActionButton buttonAdd = root.findViewById(R.id.floatingActionButtonAdd);
        final LinearLayout verticalLayout = root.findViewById(R.id.verticalGoalLayout); // acquire vertical layout to add goals to
        buttonAdd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                View newGoalView = inflater.inflate(R.layout.new_goal, verticalLayout, false);

                ProgressBar progressBar = newGoalView.findViewById(R.id.progressBarGoal);
                progressBar.incrementProgressBy(10);

                TextView goalText = newGoalView.findViewById(R.id.textViewGoal);
                goalText.setText("Should be user entered value");

                verticalLayout.addView(newGoalView, verticalLayout.getChildCount());
            }
        });
        */

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