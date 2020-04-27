package com.example.smokedout;
import java.util.ArrayList;

public class UserProfile {
    public String userEmail;
    public String userName;
    public Integer userAge;
    public ArrayList<String> friends;


    public UserProfile(String userEmail, String userName, Integer userAge,  ArrayList<String> friends) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userAge = userAge;
        this.friends = friends;
    }
}
