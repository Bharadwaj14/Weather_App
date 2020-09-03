package com.example.homework03;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyWeatherRvAdaptor extends RecyclerView.Adapter<MyWeatherRvAdaptor.ViewHolder> {
    ArrayList<Forecast> forecastList;
    static final String LOG_TAG = "Recycler View";
    public static InteractWithForecastList interact;
    Context ctx;

    public MyWeatherRvAdaptor(Context activity, ArrayList<Forecast> forecastList){
        this.forecastList = forecastList;
        this.ctx = activity;
        interact = (InteractWithForecastList) ctx;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //Views included in ViewHolder row
        TextView tvDate;
        ImageView ivDayIcon;
        ConstraintLayout cl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assign views to variables from this specific view
            tvDate = (TextView)itemView.findViewById(R.id.idWeatherRecTvDate);
            ivDayIcon = (ImageView)itemView.findViewById(R.id.idWeatherRecIvDayIcon);
            cl = (ConstraintLayout)itemView.findViewById(R.id.idWeatherRecCl);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rvLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_forecast, parent, false);
        ViewHolder viewHolder = new ViewHolder(rvLayout);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyWeatherRvAdaptor.ViewHolder holder, final int position) {
        final Forecast forecast = forecastList.get(position);

        holder.tvDate.setText(forecast.getDateDay() +" " + forecast.getDateMonth().substring(0,3) + "'" + forecast.getDateYear().substring(2,4));
        Picasso.get().load("http://developer.accuweather.com/sites/default/files/"+forecast.getDayIcon()+"-s.png").into(holder.ivDayIcon);

        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Weather Recycler View Adaptor",forecast.toString());
                interact.receiveSelectedForecast(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public interface InteractWithForecastList {
        void receiveSelectedForecast(int position);
    }
}
