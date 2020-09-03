package com.example.homework03;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class CityWeatherActivity extends AppCompatActivity implements MyWeatherRvAdaptor.InteractWithForecastList {
    ArrayList<Forecast> forecastsList;
    City city = null;

    private TextView tvCityName, tvHeadline, tvForecastDate, tvTemperature, tvDayDesc, tvNightDesc, tvWeatherTvLink;
    private ImageView ivDayIcon, ivNightIcon;
    private Button btnSaveCity, btnSetCurrent;
    private ConstraintLayout clLoading, clWeatherScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);

        tvCityName = findViewById(R.id.idWeatherTvCityName);
        tvHeadline = findViewById(R.id.idWeatherTvHeadline);
        tvForecastDate = findViewById(R.id.idWeatherTvForecastDate);
        tvTemperature = findViewById(R.id.idWeatherTvTemperature);
        tvDayDesc = findViewById(R.id.idWeatherTvDayDesc);
        tvNightDesc = findViewById(R.id.idWeatherTvNightDesc);
        tvWeatherTvLink = findViewById(R.id.idWeatherTvLink);
        ivDayIcon = findViewById(R.id.idWeatherIvDay);
        ivNightIcon = findViewById(R.id.idWeatherIvNight);
        btnSaveCity = findViewById(R.id.idWeatherBtnSaveCity);
        btnSetCurrent = findViewById(R.id.idWeatherBtnSetCurrentCity);
        clLoading = findViewById(R.id.idWeatherClLoading);
        clWeatherScreen = findViewById(R.id.idWeatherClScreen);

        if (getIntent().getExtras() != null) {
            city = (City) getIntent().getExtras().getSerializable(MainActivity.I_CITY_KEY);
            tvCityName.setText(city.getCityname() + ", " + city.getCountry());
            new GetForecastAsync().execute(city);
        } else {
            Intent i = new Intent();
            setResult(RESULT_CANCELED, i);
            finish();
        }

        btnSaveCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra(MainActivity.I_RESULT_KEY, MainActivity.I_RESULT_SAVE_KEY);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        btnSetCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra(MainActivity.I_RESULT_KEY, MainActivity.I_RESULT_SET_CURRENT_KEY);
                setResult(RESULT_OK, i);
                finish();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void receiveSelectedForecast(int position) {
        final Forecast forecastSelected = forecastsList.get(position);
        tvHeadline.setText(forecastSelected.getHeadline());

        tvForecastDate.setText(getResources().getString(R.string.resWeatherTvForecastDate)
                + " " + forecastSelected.getDateMonth()
                + " " + forecastSelected.getDateDay()
                + ", " + forecastSelected.getDateYear());

        tvTemperature.setText(getResources().getString(R.string.resWeatherTvTemperature)
                + " " + forecastSelected.getHighTemp()
                + "/" + forecastSelected.getLowTemp()
                + " F");

        tvDayDesc.setText(forecastSelected.getDayWeather());
        tvNightDesc.setText(forecastSelected.getNightWeather());

        tvWeatherTvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(forecastSelected.getLink()));
                startActivity(i);
            }
        });

        Picasso.get().load("http://developer.accuweather.com/sites/default/files/" + forecastSelected.getDayIcon() + "-s.png").into(ivDayIcon);
        Picasso.get().load("http://developer.accuweather.com/sites/default/files/" + forecastSelected.getNightIcon() + "-s.png").into(ivNightIcon);
    }

    class GetForecastAsync extends AsyncTask<City, Void, ArrayList<Forecast>> {
        @Override
        protected void onPreExecute() {
            clLoading.setVisibility(View.VISIBLE);
            clWeatherScreen.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<Forecast> doInBackground(City... cities) {
            HttpURLConnection connection = null;

            ArrayList<Forecast> forecast5day = new ArrayList<>();

            City currentCity = cities[0];
            Forecast currentForecast = null;

            try {
                URL url = new URL("http://dataservice.accuweather.com/forecasts/v1/daily/5day/"
                        + currentCity.getCitykey()
                        + "?"
                        + "apikey=" + getResources().getString(R.string.api_key));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    Log.d("City Weather Activity", json);

                    JSONObject rootObject = new JSONObject(json);

                    JSONObject headlineObject = rootObject.getJSONObject("Headline");
                    Forecast.headline = headlineObject.getString("Text");

                    JSONArray forecastsArray = rootObject.getJSONArray("DailyForecasts");
                    for (int i = 0; i < forecastsArray.length(); i++) {
                        JSONObject currentForecastObject = forecastsArray.getJSONObject(i);
                        currentForecast = new Forecast();

                        currentForecast.setDate(currentForecastObject.getString("Date"));

                        //Get temps
                        JSONObject tempObj = currentForecastObject.getJSONObject("Temperature");
                        //Min temps
                        JSONObject minTempObj = tempObj.getJSONObject("Minimum");
                        currentForecast.setLowTemp(minTempObj.getString("Value"));
                        //Get max temp
                        JSONObject maxTempObj = tempObj.getJSONObject("Maximum");
                        currentForecast.setHighTemp(maxTempObj.getString("Value"));

                        //Get day info
                        tempObj = currentForecastObject.getJSONObject("Day");
                        currentForecast.setDayIcon(tempObj.getString("Icon"));
                        currentForecast.setDayWeather(tempObj.getString("IconPhrase"));

                        //Get night info
                        tempObj = currentForecastObject.getJSONObject("Night");
                        currentForecast.setNightIcon(tempObj.getString("Icon"));
                        currentForecast.setNightWeather(tempObj.getString("IconPhrase"));

                        //Get Link
                        currentForecast.setLink(currentForecastObject.getString("MobileLink"));

                        forecast5day.add(currentForecast);
                        Log.v("RuntimeCheck", currentForecast.toString());
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return forecast5day;

    }

    @Override
    protected void onPostExecute(ArrayList<Forecast> forecasts) {
        clLoading.setVisibility(View.GONE);
        clWeatherScreen.setVisibility(View.VISIBLE);

        forecastsList = forecasts;
        tvHeadline.setText(Forecast.getHeadline());
        receiveSelectedForecast(0);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(CityWeatherActivity.this, LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.idWeatherRv);
        MyWeatherRvAdaptor adaptor = new MyWeatherRvAdaptor(CityWeatherActivity.this, forecastsList);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(layoutManager);
    }

}
}
