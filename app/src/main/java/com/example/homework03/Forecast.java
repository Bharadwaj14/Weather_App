package com.example.homework03;

public class Forecast {
    String highTemp, lowTemp, dateDay, dateMonth, dateYear, dayWeather, nightWeather, link, dayIcon, nightIcon;
    static String headline;

    public Forecast() {
    }

    public String getDateDay() {
        return dateDay;
    }

    public String getDateMonth() {
        return dateMonth;
    }

    public String getDateYear() {
        return dateYear;
    }

    public static String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        Forecast.headline = headline;
    }

    public String getHighTemp() {
        return highTemp;
    }

    public void setHighTemp(String highTemp) {
        this.highTemp = highTemp;
    }

    public String getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(String lowTemp) {
        this.lowTemp = lowTemp;
    }


    public void setDate(String date) {
        //2020-03-16
        int monthInt;
        dateYear = date.substring(0,4);
        dateMonth = date.substring(5,7);
        monthInt = Integer.parseInt(dateMonth);
        dateDay = date.substring(8,10);

        switch (monthInt){
            case 1: dateMonth = "January";
                break;
            case 2: dateMonth = "Febuary";
                break;
            case 3: dateMonth = "March";
                break;
            case 4: dateMonth = "April";
                break;
            case 5: dateMonth = "May";
                break;
            case 6: dateMonth = "June";
                break;
            case 7: dateMonth = "July";
                break;
            case 8: dateMonth = "August";
                break;
            case 9: dateMonth = "September";
                break;
            case 10: dateMonth = "October";
                break;
            case 11: dateMonth = "November";
                break;
            case 12: dateMonth = "December";
                break;
        }
    }

    public String getDayWeather() {
        return dayWeather;
    }

    public void setDayWeather(String dayWeather) {
        this.dayWeather = dayWeather;
    }

    public String getNightWeather() {
        return nightWeather;
    }

    public void setNightWeather(String nightWeather) {
        this.nightWeather = nightWeather;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDayIcon() {
        return dayIcon;
    }

    public void setDayIcon(String icon) {
        int weatherIconId = Integer.parseInt(icon);

        if (weatherIconId < 10){
            this.dayIcon = "0" + icon;
        }else {
            this.dayIcon = icon;
        }
    }

    public String getNightIcon() {
        return nightIcon;
    }

    public void setNightIcon(String icon) {
        int weatherIconId = Integer.parseInt(icon);

        if (weatherIconId < 10){
            this.nightIcon = "0" + icon;
        }else {
            this.nightIcon = icon;
        }
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "highTemp='" + highTemp + '\'' +
                ", lowTemp='" + lowTemp + '\'' +
                ", dateDay='" + dateDay + '\'' +
                ", dateMonth='" + dateMonth + '\'' +
                ", dateYear='" + dateYear + '\'' +
                ", dayWeather='" + dayWeather + '\'' +
                ", nightWeather='" + nightWeather + '\'' +
                ", link='" + link + '\'' +
                ", dayIcon='" + dayIcon + '\'' +
                ", nightIcon='" + nightIcon + '\'' +
                '}';
    }
}
