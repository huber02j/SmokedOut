package com.example.smokedout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddGoalActivity extends AppCompatActivity implements ValueEventListener {
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessage = mFirebaseDatabase.getReference().child("New Goal");
    }

    /** Called when the user clicks the submit button */
    public void onSubmit(View view) {
        // String for temporary toast, remove later
        String str = "";

        // Get goal name
        EditText nameGoalEditText = (EditText) findViewById(R.id.nameGoalEditText);
        String goalName = nameGoalEditText.getText().toString();
        str += "Goal Name: " + goalName + " ";

        // Get goal period
        ChipGroup chg = (ChipGroup) findViewById(R.id.goalPeriodChipGroup);
        int chipId = chg.getCheckedChipId();
        Chip periodChip = (Chip) findViewById(chg.getCheckedChipId());
        String period = periodChip.getText().toString();
        str += "Period: " + period + " ";

        // Get goal
        EditText numberEditText = (EditText) findViewById(R.id.goalNumberEditText);
        Integer num = Integer.parseInt(numberEditText.getText().toString());
        str += "Frequency: " + num.toString() + " ";

        // Get selected less/more radio button
        Boolean isMore = true;
        RadioGroup rgrp = (RadioGroup) findViewById(R.id.moreLessRadioGroup);
        if (rgrp.getCheckedRadioButtonId() != R.id.moreRadioButton) {
            isMore = false;
        }
        str += "Ratio: " + isMore.toString() + " ";

        // Record selected days
        ChipGroup daysChipGroup = (ChipGroup) findViewById(R.id.daysChipGroup);
        for (int i = 0; i < daysChipGroup.getChildCount(); i++) {
            Chip dayChip = (Chip) daysChipGroup.getChildAt(i);
            if (dayChip.isChecked()) {
                str += "Selected Days: " + dayChip.getText().toString() + " ";
            }
        }

        // Get motivation string
        EditText motivationEditText = (EditText) findViewById(R.id.motivationEditText);
        String motivation = motivationEditText.getText().toString();
        str += "Motivation: " + motivation;
        // ADD TO DATABASE HERE (show toast for success/failure)
        mMessage.push().setValue(str);
        // Just show message for now
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();

        // Redirect to main page
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}