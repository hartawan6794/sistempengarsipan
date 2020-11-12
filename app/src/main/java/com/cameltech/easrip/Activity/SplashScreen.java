package com.cameltech.easrip.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.cameltech.easrip.Model.User;
import com.cameltech.easrip.R;
import com.cameltech.easrip.data.SettingsAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    Handler handler = new Handler();

    SettingsAPI set;
    private FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

            firebaseAuth = FirebaseAuth.getInstance();
            set = new SettingsAPI(this);
            if (firebaseAuth.getCurrentUser() != null) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String tvEmail;
                tvEmail = user.getEmail();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Users").orderByChild("email").equalTo(tvEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        set.deleteAllSettings();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            User user = ds.getValue(User.class);
                            final String usrNme = user.getUsername();
                            final String usrId = user.getKey();
                            set.addUpdateSettings("myId", usrId);
                            set.addUpdateSettings("myName", usrNme);

                            startActivity(new Intent(getApplicationContext(), DashboardAdmin.class));
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent mainIntent = new Intent(SplashScreen.this, LoginActivity.class);
                        SplashScreen.this.startActivity(mainIntent);
                        SplashScreen.this.finish();
                    }
                }, SPLASH_DISPLAY_LENGTH);

        }


    }
}
