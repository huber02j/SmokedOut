package com.example.smokedout.ui.notifications;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smokedout.Milestone;
import com.example.smokedout.MilestoneSorter;
import com.example.smokedout.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    DataSnapshot currentDataSnapshot;
    ArrayList<String> friends = new ArrayList<>();

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Record friends and update on changes
        recordFriends();
        System.out.print(friends);

        // Display all milestones as feed
        DatabaseReference databaseMilestones = FirebaseDatabase.getInstance().getReference("Milestones");

        System.out.print("add value event listener");

        databaseMilestones.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentDataSnapshot = snapshot;
                updateMilestones(inflater, root); // Updates goals
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    private void recordFriends() {
        String UID = FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseFriends = FirebaseDatabase.getInstance().getReference("UserProfile").child(UID);

        // Store users friends, update on change
        databaseFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                friends = (ArrayList) snapshot.child("friends").getValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /* Updates milestones on data change or adding new friend */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateMilestones(@NonNull final LayoutInflater inflater, View root){
        final LinearLayout milestoneLayout = root.findViewById(R.id.milestoneLayout);
        milestoneLayout.removeAllViewsInLayout();

        ArrayList<Milestone> allMilestones = new ArrayList<>();
        for (DataSnapshot user : currentDataSnapshot.getChildren()) {

            // Get all milestones from friends
            if (friends.contains(user.getKey())) {
                for (DataSnapshot milestone : user.getChildren()) {
                    String name = (String) milestone.child("name").getValue();
                    String desc = (String) milestone.child("desc").getValue();
                    Long date = (Long) milestone.child("date").getValue();
                    Milestone m = new Milestone(name, desc, date);
                    allMilestones.add(m);
                }
            }

        }
        // Sort milestones by date
        Collections.sort(allMilestones);


        // Create and populate all milestone views
        for (Milestone m : allMilestones) {
            View newMilestoneView = inflater.inflate(R.layout.new_milestone, milestoneLayout, false);

            TextView nameText = newMilestoneView.findViewById(R.id.milestoneUserName);
            nameText.setText(m.name);
            TextView descText = newMilestoneView.findViewById(R.id.milestoneDesc);
            descText.setText(m.desc);
            TextView dateText = newMilestoneView.findViewById(R.id.milestoneDate);
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateText.setText(sfd.format(new Date(m.date)));

            milestoneLayout.addView(newMilestoneView, milestoneLayout.getChildCount());
        }
    }
}