package com.example.bitmproject.tourmate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by saran on 1/2/2018.
 */

public class PlacesAdapter extends RecyclerView.Adapter<com.example.bitmproject.tourmate.PlacesAdapter.PlacesViewHolder>{
    private Context MyContext;
    private List<NearByPlacesResponse.Result> places;
    ClickListener listener;

    public PlacesAdapter(Context myContext, List<NearByPlacesResponse.Result> places, ClickListener listener) {
        MyContext = myContext;
        this.places=places;
        this.listener=listener;
    }


    @Override
    public PlacesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(MyContext).inflate(R.layout.places_single_row,parent,false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesViewHolder holder, int position) {
        NearByPlacesResponse.Result place=places.get(position);
        String placeName=(position+1)+"."+place.getName();
        holder.placeNameTV.setText(placeName);
        try {
            if (place.getOpeningHours().getOpenNow())
                holder.openTV.setText("Open");
            else holder.openTV.setText("Closed");
        }catch (NullPointerException e){
        }

    }

    @Override
    public int getItemCount() {
        return places.size();
    }


    public class PlacesViewHolder extends RecyclerView.ViewHolder {
        TextView placeNameTV;
        TextView openTV;
        public PlacesViewHolder(View itemView) {
            super(itemView);
            placeNameTV=itemView.findViewById(R.id.place_nameTV);
            openTV=itemView.findViewById(R.id.openTV);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(places.get(getAdapterPosition()));
                }
            });
        }
    }

    public void Update(List<NearByPlacesResponse.Result> places){
        this.places=places;
        notifyDataSetChanged();
    }
    public interface ClickListener{
        void onClick(NearByPlacesResponse.Result place);
    }

}
