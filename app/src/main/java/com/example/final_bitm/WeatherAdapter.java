package com.example.final_bitm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by saran on 1/2/2018.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private Context MyContext;
    private WeatherForcastResponse weatherForcastResponse;
    String f_or_c;
    boolean internet;
    public static int count=14;

    public WeatherAdapter(Context myContext, WeatherForcastResponse weatherForcastResponse,
                          String f_or_c,boolean internet) {
        MyContext = myContext;
        this.weatherForcastResponse = weatherForcastResponse;
        this.f_or_c=f_or_c;
        this.internet=internet;
    }
    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(MyContext).inflate(R.layout.card,parent,false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        String icon=weatherForcastResponse.getList().get(position+1).getWeather().get(0).getIcon();
        Picasso.with(MyContext).load("http://openweathermap.org/img/w/"+icon+".png").into(holder.iconIV);
        WeatherForcastResponse.ForecastList forcastList=weatherForcastResponse.getList().get(position+1);
        String des=forcastList.getWeather().get(0).getDescription();
        String desUp=String.valueOf(des.charAt(0)).toUpperCase()+des.subSequence(1,des.length());
        holder.descriptionTV.setText(desUp);
        holder.dateTV.setText(forcastList.getDt());
        if(internet) {
            holder.minTempTV.setText("Min temp: " + forcastList.getTemp().getMin().toString() + f_or_c);
            holder.maxTempTV.setText("Max temp: " + forcastList.getTemp().getMax().toString() + f_or_c);
        }
        else {
            String tempMax;
            String tempMin;
            if(f_or_c.equals("°F")) {
                tempMax=String.format("%.2f",forcastList.getTemp().getMaxInFarren());
                tempMin=String.format("%.2f",forcastList.getTemp().getMinInFarren());
                holder.minTempTV.setText("Min temp: " + tempMin + f_or_c);
                holder.maxTempTV.setText("Max temp: " + tempMax + f_or_c);
            }
            else {
                holder.minTempTV.setText("Min temp: " + forcastList.getTemp().getMin().toString() + f_or_c);
                holder.maxTempTV.setText("Max temp: " + forcastList.getTemp().getMax().toString() + f_or_c);
            }
        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView dateTV;
        TextView descriptionTV;
        TextView minTempTV;
        TextView maxTempTV;
        ImageView iconIV;
        public WeatherViewHolder(View itemView) {
            super(itemView);
            dateTV=itemView.findViewById(R.id.date);
            descriptionTV=itemView.findViewById(R.id.descriptio);
            minTempTV=itemView.findViewById(R.id.fMinTemp);
            maxTempTV=itemView.findViewById(R.id.fMaxTemp);
            iconIV=itemView.findViewById(R.id.iconTV);
        }
    }
}
