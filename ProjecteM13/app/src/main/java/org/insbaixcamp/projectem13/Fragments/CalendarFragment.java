package org.insbaixcamp.projectem13.Fragments;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.insbaixcamp.projectem13.Adapter.MyTimeSlotAdapter;
import org.insbaixcamp.projectem13.Common.Common;
import org.insbaixcamp.projectem13.Common.SpacesItemDecoration;
import org.insbaixcamp.projectem13.Interfaces.ITimeSlotLoadListener;
import org.insbaixcamp.projectem13.Model.TimeSlot;
import org.insbaixcamp.projectem13.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;


public class CalendarFragment extends Fragment implements ITimeSlotLoadListener {

    DocumentReference physioDoc;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    AlertDialog dialog;
    Calendar selectedDate;

    LocalBroadcastManager localBroadcastManager;

    RecyclerView rvTimeSlot;
    HorizontalCalendarView calendarView;
    SimpleDateFormat simpleDateFormat;

    static CalendarFragment instance;

    BroadcastReceiver displayTimeSlot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar date = Calendar.getInstance();
            //add current date
            date.add(Calendar.DATE,0);
            loadAvailableTimeSlot(simpleDateFormat.format(date.getTime()));
        }
    };

    private void loadAvailableTimeSlot(final String bookDate) {
        dialog.show();

        physioDoc = FirebaseFirestore.getInstance()
                .collection("Booking")
                .document("Physio");

        //get information of this physio
        physioDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        //get information of booking
                        //if no created return empty
                        CollectionReference date = FirebaseFirestore.getInstance()
                                .collection("Booking")
                                .document("Physio")
                                .collection(bookDate);

                        date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    //if don't have appoiment
                                    if (querySnapshot.isEmpty())
                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                        //if have appoiment
                                    else {
                                        List<TimeSlot> timeSlots = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult())
                                            timeSlots.add(document.toObject(TimeSlot.class));
                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }

    public static CalendarFragment getInstance(){
        if (instance == null){
            instance = new CalendarFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iTimeSlotLoadListener = this;
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(displayTimeSlot,new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));

        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        selectedDate = Calendar.getInstance();
        selectedDate.add(Calendar.DATE, 0);

        Calendar date = Calendar.getInstance();
        //add current date
        date.add(Calendar.DATE,0);
        loadAvailableTimeSlot(simpleDateFormat.format(date.getTime()));

    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(displayTimeSlot);
        super.onDestroy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarView = itemView.findViewById(R.id.cv_calendar_view);
        rvTimeSlot = itemView.findViewById(R.id.rv_time_slot);

        init(itemView);

        return itemView;
    }

    public void init(View itemView) {
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        rvTimeSlot.setLayoutManager(manager);
        rvTimeSlot.setHasFixedSize(true);
        rvTimeSlot.addItemDecoration(new SpacesItemDecoration(8));

        //Calendar (start date == today, end date == today + 30days)
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE,0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE,30);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(itemView, R.id.cv_calendar_view)
                .range(startDate, endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (selectedDate.getTimeInMillis() != date.getTimeInMillis()) {
                    Common.bookingDate = date;
                    loadAvailableTimeSlot(simpleDateFormat.format(date.getTime()));
                }
            }
        });
    }


    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext(), timeSlotList);
        rvTimeSlot.setAdapter(adapter);

        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext());
        rvTimeSlot.setAdapter(adapter);

        dialog.dismiss();
    }
}
