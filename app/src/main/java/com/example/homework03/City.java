package com.example.homework03;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class City implements Serializable {
    private String citykey, cityname, country, temperature, state;
    private boolean favorite;
    private String localObservationDateTime, weatherText, weatherIcon;
    private long epochTime;

    public City() {
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        City compareCity = ((City)obj);

        if (citykey.equals(compareCity.getCitykey()))
            return true;
        else
            return false;
    }

    public long getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(long epochTime) {
        this.epochTime = epochTime;
    }

    public String getLastUpdated(){
        long currentEpochTime = (System.currentTimeMillis() / 1000);
        long timeDifference = currentEpochTime - epochTime;
        String result = "";
        long hours;
        long minutes;

        hours = TimeUnit.SECONDS.toHours(timeDifference);
        minutes = TimeUnit.SECONDS.toMinutes(timeDifference) - 60*hours;

        if(hours > 0){
            result = hours + " hours, " + minutes +" minutes ago";
        }else{
            result = minutes + " minutes ago";
        }

        return  (result);
    }

    public String getLocalObservationDateTime() {
        return localObservationDateTime;
    }

    public void setLocalObservationDateTime(String localObservationDateTime) {
        this.localObservationDateTime = localObservationDateTime;
    }

    public String getWeatherText() {
        return weatherText;
    }

    public void setWeatherText(String weatherText) {
        this.weatherText = weatherText;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        int weatherIconId = Integer.parseInt(weatherIcon);

        if (weatherIconId < 10){
            this.weatherIcon = "0" + weatherIcon;
        }else {
            this.weatherIcon = weatherIcon;
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCitykey() {
        return citykey;
    }

    public void setCitykey(String citykey) {
        this.citykey = citykey;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "City{" +
                "citykey='" + citykey + '\'' +
                ", cityname='" + cityname + '\'' +
                ", country='" + country + '\'' +
                ", temperature='" + temperature + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}
