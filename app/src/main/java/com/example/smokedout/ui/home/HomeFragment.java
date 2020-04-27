package com.example.smokedout.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smokedout.AddGoalActivity;
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
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private String dateString;
    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseGoalInfo;
    private Map<String, DatabaseReference> allGoalsNamesAndReferences = new HashMap<>();

    DataSnapshot currentDataSnapshot;


    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initial Date information to display
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM-dd-yyyy");
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


        DatabaseReference databaseUserGoal = databaseGoalInfo.child(firebaseAuth.getUid());

        // Display all goals in edit text
        databaseUserGoal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentDataSnapshot = snapshot;
                updateCheckBoxes(inflater, root); // Updates goals
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Next day button
        final FloatingActionButton buttonRight = root.findViewById(R.id.floatingActionButtonRight);
        buttonRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Date currDate = null;
                try {
                    currDate = new SimpleDateFormat("MM-dd-yyyy").parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date newDate = changeDay(currDate,1);


                dateString = dateFormat.format(newDate);
                updateCheckBoxes(inflater, root); // Updates goals
                dateTimeDisplay.setText(dateString);
            }
        });

        // Previous day button
        final FloatingActionButton buttonLeft = root.findViewById(R.id.floatingActionButtonLeft);
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Date currDate = null;
                try {
                    currDate = new SimpleDateFormat("MM-dd-yyyy").parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date newDate = changeDay(currDate,0);


                dateString = dateFormat.format(newDate);
                updateCheckBoxes(inflater, root);
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

    /*Used for updating the check boxes when the screen appears or when the date is changed.*/
    private void updateCheckBoxes(@NonNull final LayoutInflater inflater, View root){
        final LinearLayout goalLayout = root.findViewById(R.id.goalLayout);
        goalLayout.removeAllViewsInLayout();

        for (DataSnapshot goal : currentDataSnapshot.getChildren()) {

            // Get name of goal
            String goalName = (String) goal.child("name").getValue();
            DatabaseReference goalRef = goal.getRef();
            allGoalsNamesAndReferences.put(goalName, goalRef); //Reference to the specific goal. Key is the goal name

            View newGoalView = inflater.inflate(R.layout.new_goal, goalLayout, false);

            TextView goalText = newGoalView.findViewById(R.id.textViewGoal);
            goalText.setText(goalName);

            // Get the checks hashmap from each goal
            Map<String, Boolean> checks = (HashMap) goal.child("checks").getValue();
            CheckBox checkBox = newGoalView.findViewById(R.id.checkBoxGoal); // Check box
            checkBox.setTag(goalName);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = v.findViewById(v.getId());
                    String goalName = checkBox.getTag().toString();
                    Log.d("goalname", goalName);
                    DatabaseReference ref = allGoalsNamesAndReferences.get(goalName);

                    //Use reference to change value
                    ref.child("checks").child(dateString).setValue(checkBox.isChecked());
                }
            });
            if (checks.containsKey(dateString)) {
                checkBox.setChecked(checks.get(dateString));
            } else {
                checkBox.setChecked(false);
            }
            Log.d("Map", String.valueOf(checks.containsKey(dateString)));

            goalLayout.addView(newGoalView, goalLayout.getChildCount());

        }
    }
}