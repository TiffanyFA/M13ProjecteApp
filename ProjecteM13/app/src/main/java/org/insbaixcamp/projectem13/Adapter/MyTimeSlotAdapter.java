package org.insbaixcamp.projectem13.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import org.insbaixcamp.projectem13.Common.Common;
import org.insbaixcamp.projectem13.Interfaces.IRecyclerItemSelectedListener;
import org.insbaixcamp.projectem13.Model.TimeSlot;
import org.insbaixcamp.projectem13.R;

import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {

    Context context;
    List<TimeSlot> timeSlotList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context){
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.tvTimeSlot.setText(new StringBuilder(Common.convertTimeSlotToString(position)).toString());

        //if all position is available
        if (timeSlotList.size() == 0){
            holder.cvTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.tvState.setText(context.getText(R.string.calendar_state_free));
            holder.tvState.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.tvTimeSlot.setTextColor(context.getResources().getColor(android.R.color.black));

        //if position is full
        } else {
            for(TimeSlot slotValue:timeSlotList) {

                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if (slot == position) {
                    holder.cvTimeSlot.setTag(Common.DISABLE_TAG);
                    holder.cvTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                    holder.tvState.setText(context.getText(R.string.calendar_state_full));
                    holder.tvState.setTextColor(context.getResources().getColor(android.R.color.white));
                    holder.tvTimeSlot.setTextColor(context.getResources().getColor(android.R.color.black));
                }
            }
        }
        //add all card to list
        if(!cardViewList.contains(holder.cvTimeSlot)){
            cardViewList.add(holder.cvTimeSlot);
        }

        //check if card time slot is available
        if (!timeSlotList.contains(position)) {
            holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {

                @Override
                public void onItemSelectedListener(View view, int position) {
                    for (CardView cardView : cardViewList) {
                        if (cardView.getTag() == null) {
                            cardView.setCardBackgroundColor(context.getResources()
                                    .getColor(android.R.color.white));
                        }
                    }

                    //selected card will be change color
                    holder.cvTimeSlot.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.holo_orange_dark));

                    Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                    //put index of time slot we have selected
                    intent.putExtra(Common.KEY_TIME_SLOT, position);
                    //go to step 1
                    intent.putExtra(Common.KEY_STEP,1 );
                    localBroadcastManager.sendBroadcast(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTimeSlot;
        TextView tvState;
        CardView cvTimeSlot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cvTimeSlot = itemView.findViewById(R.id.cv_time_slot);
            tvTimeSlot = itemView.findViewById(R.id.tv_time_slot);
            tvState = itemView.findViewById(R.id.tv_state);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            iRecyclerItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }
}
