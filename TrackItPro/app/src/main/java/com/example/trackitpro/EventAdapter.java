package com.example.trackitpro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    // interface used so activity knows when user clicks stuff
    public interface OnEventClickListener {
        void onDeleteClick(int position);
        void onItemClick(int position);
    }

    private final List<Event> events; // list of events for recycler
    private final OnEventClickListener listener; // callback to activity

    // adapter constructor
    public EventAdapter(List<Event> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    // called when recycler needs to create a new row
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    // binds event data into each row
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);

        // set text for title and date
        holder.tvTitle.setText(event.getTitle());
        holder.tvDate.setText(event.getDate());

        // delete button click
        holder.btnDelete.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();

            if (listener != null && adapterPosition != RecyclerView.NO_POSITION)
                listener.onDeleteClick(adapterPosition);

        });

        // clicking the row opens details
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();

            if (listener != null && adapterPosition != RecyclerView.NO_POSITION)
                listener.onItemClick(holder.getAdapterPosition());
        });
    }

    // how many rows recycler view should show
    @Override
    public int getItemCount() {
        return events.size();
    }

    // holds views for a single event row
    static class EventViewHolder extends RecyclerView.ViewHolder {

        final TextView tvTitle;  // event title text
        final TextView tvDate;   // event date text
        final ImageButton btnDelete; // trash button

        // setup views for row layout
        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}



