package com.ethnicindia.omkarmic.kalyanam.viewHolders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ethnicindia.omkarmic.kalyanam.EventDetails;
import com.ethnicindia.omkarmic.kalyanam.R;

public class EventViewHolder extends RecyclerView.ViewHolder {
    private TextView titleView;
    private TextView dateView;
    private TextView detailView;
    private TextView knowButton;
    private TextView regButton;
    private View mView;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        titleView = mView.findViewById(R.id.event_title);
        dateView = mView.findViewById(R.id.event_date);
        detailView = mView.findViewById(R.id.event_description);
        knowButton = mView.findViewById(R.id.event_know_more);
        regButton = mView.findViewById(R.id.event_register);
    }

    public void setTitle(String title){
        titleView.setText(title);
    }

    public void setDate(String date){
        dateView.setText(date);
    }

    public void setDetails(String details){
        detailView.setText(details);
    }

    public void setListeners(final Context context, final String id){
        knowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetails.class);
                intent.putExtra("id", id);
                context.startActivity(intent);
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
