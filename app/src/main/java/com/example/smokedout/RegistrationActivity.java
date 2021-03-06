package com.example.smokedout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class RegistrationActivity extends AppCompatActivity {

     public EditText userName, userPassword, userEmail, userAge;
     Button regButton;
     TextView userLogin;
     String email, name, age, password;
     private FirebaseAuth firebaseAuth;
     private static final String TAG = "LoginActivity";
     DatabaseReference databaseUserProfile, databaseMilestones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseUserProfile = FirebaseDatabase.getInstance().getReference("UserProfile");
        databaseMilestones = FirebaseDatabase.getInstance().getReference("Milestones");

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateLogin()){
                    //update data to database
                    final String user_email = userEmail.getText().toString().trim();
                    final String user_password = userPassword.getText().toString().trim();
                    final String user_name = userName.getText().toString().trim();
                    final Integer user_age = Integer.parseInt(userAge.getText().toString().trim());
                    final ArrayList<String> friends = new ArrayList<String>();

                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                                // Add user information
                                friends.add(firebaseAuth.getUid());
                                UserProfile userProfile = new UserProfile(user_email, user_name, user_age, friends);
                                databaseUserProfile.child(firebaseAuth.getUid()).setValue(userProfile);

                                // Add first milestone (added the app!)
                                Milestone milestone = new Milestone(user_name, "Joined SmokedOut! :)", System.currentTimeMillis());
                                String milestoneID = databaseMilestones.child(firebaseAuth.getUid()).push().getKey();
                                databaseMilestones.child(firebaseAuth.getUid()).child(milestoneID).setValue(milestone);

                                // Redirect to login
                                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG,"Error",e);
                        }
                    });

                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });
    }

    //Assigns Variables to Layout Components
    private void setupUIViews(){
        userName = (EditText)findViewById(R.id.etUserName);
        userPassword = (EditText)findViewById(R.id.etUserPassword);
        userEmail = (EditText)findViewById(R.id.etUserEmail);
        regButton = (Button)findViewById(R.id.Register);
        userLogin = (TextView)findViewById(R.id.tvUserLogin);
        userAge = (EditText)findViewById(R.id.etThisAge);
    }

    private Boolean validateLogin(){
        Boolean result = false;

         name = userName.getText().toString();
         password = userPassword.getText().toString();
         email = userEmail.getText().toString();
         age = userAge.getText().toString();

        if(name.isEmpty() || password.isEmpty() || email.isEmpty() || password.length() < 6 || age.isEmpty()){
            Toast.makeText(this, "Please enter all the details or password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }
    /** Send user information to database **/
    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        ArrayList<String> friends = new ArrayList<String>();
        UserProfile userProfile = new UserProfile(email, name, Integer.parseInt(age), new ArrayList<String>());
        myRef.setValue(userProfile);
    }

}
