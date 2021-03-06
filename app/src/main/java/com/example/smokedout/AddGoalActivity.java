package com.example.smokedout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

public class AddGoalActivity extends AppCompatActivity implements ValueEventListener {

    private SimpleDateFormat dateFormat;
    private Date date;


    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseGoalInfo;
    String goalName, period, days;
    Integer num;
    Boolean orMore;
//    String test;
    Map<String, Boolean> checks = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        databaseGoalInfo = FirebaseDatabase.getInstance().getReference("GoalInfo");

//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        mMessage = mFirebaseDatabase.getReference().child("New Goal");
        firebaseAuth = FirebaseAuth.getInstance();

    }

    /** Called when the user clicks the submit button */
    public void onSubmit(View view) {

        // Get goal name
        EditText nameGoalEditText = (EditText) findViewById(R.id.nameGoalEditText);
        goalName = nameGoalEditText.getText().toString();

        // Get goal period
        ChipGroup chg = (ChipGroup) findViewById(R.id.goalPeriodChipGroup);
        int chipId = chg.getCheckedChipId();
        Chip periodChip = (Chip) findViewById(chg.getCheckedChipId());
        period = periodChip.getText().toString();

        // Get goal
        EditText numberEditText = (EditText) findViewById(R.id.goalNumberEditText);
        num = Integer.parseInt(numberEditText.getText().toString());

        // Get selected less/more radio button
        orMore = true;
        RadioGroup rgrp = (RadioGroup) findViewById(R.id.moreLessRadioGroup);
        if (rgrp.getCheckedRadioButtonId() != R.id.moreRadioButton) {
            orMore = false;
        }

        // Record selected days (bit string: i.e. SMTWTFS --> 0011000)
        days = "";
        ChipGroup daysChipGroup = (ChipGroup) findViewById(R.id.daysChipGroup);
        for (int i = 0; i < daysChipGroup.getChildCount(); i++) {
            Chip dayChip = (Chip) daysChipGroup.getChildAt(i);
            if (dayChip.isChecked()) {
                days += "1";
            } else {
                days += "0";
            }
        }

        // Initialize the checks dict with the date
        date = new Date();
        dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String day = dateFormat.format(date);
        checks.put(day, new Boolean(false)); //Set initially to false so they can check it off


        // Add new goal
        sendUserData();

        // Redirect to main page
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Insert to database
    private void sendUserData(){

        // Collect information and key for new goal
        GoalInfo goalInfo = new GoalInfo(goalName, period, num, orMore, days, checks);
        String goalId = databaseGoalInfo.child(firebaseAuth.getUid()).push().getKey();

        // Submit
        databaseGoalInfo.child(firebaseAuth.getUid()).child(goalId).setValue(goalInfo);
        Toast.makeText(AddGoalActivity.this, "Database Updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Log.d("onDataChange called", "data change");
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        out.println("onCancelled called");

    }
}