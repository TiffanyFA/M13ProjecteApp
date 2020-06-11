package org.insbaixcamp.projectem13.Fragments;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.insbaixcamp.projectem13.Common.Common;
import org.insbaixcamp.projectem13.Model.BookingInformation;
import org.insbaixcamp.projectem13.R;
import org.insbaixcamp.projectem13.activities.MainActivity;
import org.insbaixcamp.projectem13.activities.reservation.CalendarActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import dmax.dialog.SpotsDialog;


public class ConfirmFragment extends Fragment {

    private SimpleDateFormat simpleDateFormat;
    private LocalBroadcastManager localBroadcastManager;
    private AlertDialog dialog;
    private TextView tvThanks;
    private TextView tvInfoTitle;
    private TextView tvInfoTime;
    private ImageView icIconCalendar;
    private Button bConfirm;
    static ConfirmFragment instance;

    public static ConfirmFragment getInstance(){
        if (instance == null){
            instance = new ConfirmFragment();
        }
        return instance;
    }

    BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver,new IntentFilter(Common.KEY_CONFIRM_BOOKING));

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false)
                .build();


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_confirm, container, false);
        tvThanks = itemView.findViewById(R.id.tv_thanks);
        tvInfoTitle = itemView.findViewById(R.id.tv_info_title);
        tvInfoTime = itemView.findViewById(R.id.tv_info_time);
        icIconCalendar = itemView.findViewById(R.id.ic_icon_calendar);
        bConfirm = itemView.findViewById(R.id.btn_confirm);

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
                String[] convertTime = startTime.split("-");

                String[] startTimeConvert = convertTime[0].split(":");
                int startHourInt = Integer.parseInt(startTimeConvert[0].trim());
                int startMinInt = Integer.parseInt(startTimeConvert[1].trim());

                Calendar bookingDateWithoutHouse = Calendar.getInstance();
                bookingDateWithoutHouse.setTimeInMillis(Common.bookingDate.getTimeInMillis());
                bookingDateWithoutHouse.set(Calendar.HOUR_OF_DAY, startHourInt);
                bookingDateWithoutHouse.set(Calendar.MINUTE, startMinInt);

                Timestamp timestamp = new Timestamp(bookingDateWithoutHouse.getTime());

                //create booking information
                final BookingInformation bookingInformation = new BookingInformation();

                bookingInformation.setTimestamp(timestamp);
                bookingInformation.setDone(false);
                bookingInformation.setCustomerId(Common.currentUser.getAuth_id());
                bookingInformation.setCustomerName(Common.currentUser.getName());
                bookingInformation.setCustomerPhone(Common.currentUser.getPhone());
                bookingInformation.setCustomerEmail(Common.currentUser.getEmail());
                bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                        .append(" - ")
                        .append(simpleDateFormat.format(bookingDateWithoutHouse.getTime())).toString());
                bookingInformation.setSlot((long) Common.currentTimeSlot);

                //Submit to physio document
                DocumentReference bookingDate = FirebaseFirestore.getInstance()
                        .collection("Booking")
                        .document("Physio")
                        .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                        .document(String.valueOf(Common.currentTimeSlot));

                //write data
                bookingDate.set(bookingInformation)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                addToUserBooking(bookingInformation);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return itemView;
    }

    private void addToUserBooking(final BookingInformation bookingInformation) {

        final CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getAuth_id())
                .collection("Booking");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());
        userBooking
                .whereGreaterThanOrEqualTo("timestamp",toDayTimeStamp)
                .whereEqualTo("done",false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            userBooking.document()
                                    .set(bookingInformation)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (dialog.isShowing()) {
                                                dialog.dismiss();
                                            }
                                            resetStaticData();
                                            getActivity().finish();
                                            Toast.makeText(getContext(), String.valueOf(R.string.confirm_success), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if (dialog.isShowing()) {
                                                dialog.dismiss();
                                            }
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            resetStaticData();
                            getActivity().finish();
                            Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void resetStaticData() {
        Common.step = 0;
        Common.currentTimeSlot = -1;
        Common.bookingDate.add(Calendar.DATE,0);
    }

    private void setData() {
        tvInfoTime.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" => ")
                .append(simpleDateFormat.format(Common.bookingDate.getTime())));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);
    }

}
