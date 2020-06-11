package org.insbaixcamp.projectem13.activities.reservation;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.firestore.CollectionReference;

import org.insbaixcamp.projectem13.Adapter.ViewPageAdapter;
import org.insbaixcamp.projectem13.Common.Common;
import org.insbaixcamp.projectem13.R;

import dmax.dialog.SpotsDialog;


public class CalendarActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    AlertDialog dialog;
    CollectionReference physio;
    private ViewPager viewPager;
    private Button bPrevious;
    private Button bNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        viewPager = findViewById(R.id.vp_wiew_pager);
        bPrevious = findViewById(R.id.btn_previous);
        bNext = findViewById(R.id.btn_next);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver,new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        setColorButton();


        //View
        viewPager.setAdapter(new ViewPageAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    bPrevious.setEnabled(false);
                } else {
                    bPrevious.setEnabled(true);
                }

                bNext.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        loadTimeSlotOfPhysio(Common.physio);

        //Listener
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.step++;
                if (Common.currentTimeSlot != -1) {
                    confirmBooking();
                }
                viewPager.setCurrentItem(Common.step);
            }
        });

        bPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.step--;
                viewPager.setCurrentItem(Common.step);
                if (Common.step < 1) {
                    bNext.setEnabled(false);
                    bPrevious.setEnabled(false);
                    setColorButton();
                }
            }
        });
    }

    private void confirmBooking() {
        //send broadCast to fragment step 1
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSlotOfPhysio(String physio) {
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step = intent.getIntExtra(Common.KEY_STEP, 0);
            if (step == 1) {
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT, -1);
            }
            bNext.setEnabled(true);
            setColorButton();
        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    private void setColorButton() {
        if (bNext.isEnabled()) {
            bNext.setBackgroundResource(R.color.colorButton);
        } else {
            bNext.setBackgroundResource(android.R.color.darker_gray);
        }

        if (bPrevious.isEnabled()) {
            bPrevious.setBackgroundResource(R.color.colorButton);
        } else {
            bPrevious.setBackgroundResource(android.R.color.darker_gray);
        }
    }


}






