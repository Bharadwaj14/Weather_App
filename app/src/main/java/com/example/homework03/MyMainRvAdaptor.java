package com.example.homework03;

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

import java.util.ArrayList;

public class MyMainRvAdaptor extends RecyclerView.Adapter<MyMainRvAdaptor.ViewHolder> {
    ArrayList<City> cityList;
    static final String LOG_TAG = "Recycler View";
    public static InteractWithCityList interact;
    Context ctx;

    public MyMainRvAdaptor(Context activity, ArrayList<City> cityList){
        this.cityList = cityList;
        this.ctx = activity;
        interact = (InteractWithCityList) ctx;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //Views included in ViewHolder row
        TextView tvCityName;
        TextView tvTemp;
        TextView tvLastUpdated;
        ConstraintLayout constraintLayout;
        ImageView ivFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assign views to variables from this specific view
            tvCityName = (TextView) itemView.findViewById(R.id.idMainRecTvCityName);
            tvTemp = (TextView) itemView.findViewById(R.id.idMainRecTvTemp);
            tvLastUpdated = (TextView) itemView.findViewById(R.id.idMainRecLastUpdated);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.idWeatherRecCl);
            ivFavorite = itemView.findViewById(R.id.idWeatherRecIvDayIcon);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rvLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_city, parent, false);
        ViewHolder viewHolder = new ViewHolder(rvLayout);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyMainRvAdaptor.ViewHolder holder, final int position) {
        final City city = cityList.get(position);

        holder.tvCityName.setText(city.getCityname() + ", " + city.getCountry());
        holder.tvTemp.setText(ctx.getResources().getString(R.string.resMainRecTvTemp)+" "+city.getTemperature() + " F");
        holder.tvLastUpdated.setText(ctx.getResources().getString(R.string.resMainRecLastUpdated) + " " + city.getLastUpdated());

        //If city marked favorite, change the image
        if (city.isFavorite()){
            holder.ivFavorite.setImageDrawable(ctx.getResources().getDrawable(android.R.drawable.btn_star_big_on));
        }else {
            holder.ivFavorite.setImageDrawable(ctx.getResources().getDrawable(android.R.drawable.btn_star_big_off));
        }

        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("Recycler View Adaptor", "Clicked on " + city.toString());
                interact.receiveDelete(position);
                return true;
            }
        });

        holder.tvCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interact.receiveSelectedCity(position);
            }
        });

        holder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (city.isFavorite()){
                    holder.ivFavorite.setImageDrawable(ctx.getResources().getDrawable(android.R.drawable.btn_star_big_off));
                    city.setFavorite(false);
                }else{
                    holder.ivFavorite.setImageDrawable(ctx.getResources().getDrawable(android.R.drawable.btn_star_big_on));
                    city.setFavorite(true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public interface InteractWithCityList{
        void receiveSelectedCity(int position);
        void receiveDelete(int position);
    }
}