package com.example.bitmproject.tourmate.AdapterClass;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bitmproject.tourmate.PosoClass.EventClass;
import com.example.bitmproject.tourmate.R;

import java.util.ArrayList;



public class EventAdapter extends RecyclerView.Adapter<EventAdapter.BookListViewHolder> {

    private Context context;
    private ArrayList<EventClass> events;
    private clickListener listener;

    public EventAdapter(Context context, ArrayList<EventClass> events,clickListener listener) {
        this.context = context;
        this.events = events;
        this.listener = listener;
    }


    @Override
    public BookListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.event_single_row,parent,false);
        return new BookListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookListViewHolder holder, int position) {
        EventClass eventClass=events.get(position);


        holder.eventNameTv.setText(eventClass.getEventName());
        holder.createdDateTv.setText("Created Date: "+eventClass.getStartedDate());
        holder.departureDateTv.setText("Starting Date: "+eventClass.getDepartureDate());
        int daysLeft = eventClass.getDaysLeft();
        if (daysLeft>0){
            holder.daysLeftTv.setText(String.valueOf(eventClass.getDaysLeft())+" days Left");
        }
        else {
            holder.daysLeftTv.setText("Event has already started");
        }

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class BookListViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTv;
        TextView createdDateTv;
        TextView departureDateTv;
        TextView endDateTv;
        TextView daysLeftTv;

        public BookListViewHolder(View itemView) {
            super(itemView);
            eventNameTv=itemView.findViewById(R.id.eventNameTv);
            createdDateTv=itemView.findViewById(R.id.createdDateTv);
            departureDateTv =itemView.findViewById(R.id.departureDateTv);
            daysLeftTv = itemView.findViewById(R.id.daysLeftTv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickEvent(events.get(getAdapterPosition()));
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    listener.onLogClickEvent(events.get(getAdapterPosition()));

                    return true;
                }
            });

        }
    }
    public void Update(ArrayList<EventClass> events){
        this.events=events;
        notifyDataSetChanged();
    }
    public interface clickListener{
        void onClickEvent(EventClass eventClass);
        void onLogClickEvent(EventClass eventClass);
    }

}
