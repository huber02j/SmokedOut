package com.example.smokedout.ui.dashboard;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smokedout.MainActivity;
import com.example.smokedout.NotificationPublisher;
import com.example.smokedout.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DashboardFragment extends Fragment {

    private static final String CHANNEL_ID = "1001";
    private DashboardViewModel dashboardViewModel;
    private FirebaseAuth firebaseAuth;
    DatabaseReference dataGoalInfo;
   // private Map<String, DatabaseReference> allGoalsNamesAndReferences = new HashMap<>();

    DataSnapshot dataSnapshot;


    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dataGoalInfo = FirebaseDatabase.getInstance().getReference("GoalInfo");
        firebaseAuth = FirebaseAuth.getInstance();

        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);

        DatabaseReference UserGoal = dataGoalInfo.child(firebaseAuth.getUid());

        // Display all goals in edit text
        UserGoal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                dataSnapshot = snapshot;

                final LinearLayout progressLayout = root.findViewById(R.id.progressLayout);
                progressLayout.removeAllViewsInLayout();

                for (DataSnapshot goal : dataSnapshot.getChildren()){

                    String name = (String) goal.child("name").getValue();
                 //   DatabaseReference goalref = goal.getRef();
                    View newProgressView = inflater.inflate(R.layout.activity_goal_progress, progressLayout, false);

                    TextView progressText = newProgressView.findViewById(R.id.GoalProgress);
                    progressText.setText(name);

                    ProgressBar progressBar = newProgressView.findViewById(R.id.ProgressBar);
                    Random rand = new Random();

                    int n = rand.nextInt(50);
                    progressBar.setProgress(n);

                    progressLayout.addView(newProgressView,progressLayout.getChildCount());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        return root;
    }



}