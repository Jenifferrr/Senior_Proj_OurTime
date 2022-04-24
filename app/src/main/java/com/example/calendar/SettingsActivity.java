package com.example.calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsActivity extends AppCompatActivity {

    //init views
    SwitchCompat postSwitch;

    //user shared preferences to save the state of switch
    SharedPreferences sp;
    SharedPreferences.Editor editor; //edit value of shared pref

    //constant for topic
    private static final String TOPIC_POST_NOTIFICATIONS = "POST"; //assign any value but use same for this notification

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        postSwitch = findViewById(R.id.postSwitch);

        //init sp
        sp= getSharedPreferences("Notification_SP", MODE_PRIVATE);
        boolean isPostEnabled = sp.getBoolean(""+TOPIC_POST_NOTIFICATIONS, false);
        //if enabled check switch, otherwise uncheck switch - by default unchecked/false
        if (isPostEnabled) {
            postSwitch.setChecked(true);
        }
        else{
            postSwitch.setChecked(false);
        }

        //implement switch change listener
        postSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //edit switch state
                editor = sp.edit();
                editor.putBoolean(""+TOPIC_POST_NOTIFICATIONS, isChecked);
                editor.apply();

                if(isChecked){
                    subscribePostNotifications();//call to subscribe
                }
                else {
                    unsubscribePostNotifications();// call to unsubscribe
                }
            }
        });

    }

    private void unsubscribePostNotifications() {
        //subscribe to a topic (POST) to disable its notifications
        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_POST_NOTIFICATIONS)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Post Notifications Disabled";
                        if (!task.isSuccessful()) {
                            msg = "Unsubscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }


                });
    }

    private void subscribePostNotifications() {
        //subscribe to a topic (POST) to enable its notifications
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_POST_NOTIFICATIONS)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Post Notifications Enabled";
                        if (!task.isSuccessful()) {
                            msg = "Subscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}