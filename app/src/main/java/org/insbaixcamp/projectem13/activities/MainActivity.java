package org.insbaixcamp.projectem13.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import org.insbaixcamp.projectem13.R;
import org.insbaixcamp.projectem13.activities.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    ImageButton ibServices;
    ImageButton ibReservation;
    ImageButton ibMap;
    ImageButton ibContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //image buttons
        ibServices = (ImageButton) findViewById(R.id.iv_services);
        ibReservation = (ImageButton) findViewById(R.id.iv_reservation);
        ibMap = (ImageButton) findViewById(R.id.iv_maps);
        ibContact = (ImageButton) findViewById(R.id.iv_contact);

        //clicks listeners
        ibServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ServicesActivity.class);
                startActivity(intent);
            }
        });

        ibReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        ibMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        ibContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

}
