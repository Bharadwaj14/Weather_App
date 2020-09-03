/**
 * @title: Homework 03
 * @author: Bharadwaj Tirunagaru
 */

package com.example.homework03;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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

public class MainActivity extends AppCompatActivity implements MyMainRvAdaptor.InteractWithCityList {
    private ConstraintLayout clCityIsSet, clCityNotSet, clNoSavedCities, clSavedCities, clLoadingScreen, clMainScreen;
    private TextView tvLocationName, tvWeather, tvTemperature, tvLastUpdated;
    private EditText etCityEntered, etCountryEntered;
    private Button btnSetCurrentCity, btnSearch;
    private ImageView ivWeatherImage;
    private RecyclerView recyclerView;
    private MyMainRvAdaptor adaptor;


    private SharedPreferences sharedPref;

    static final String PREF_KEY = "com.example.homework03.shared_preferences";
    static final String SP_CURRENT_CITY_KEY = "Current City Key";
    static final String SP_SAVED_CITIES_LIST_KEY = "Saved Cities List Key";
    static final String I_CITY_KEY = "Intent extra City key";
    static final String I_RESULT_KEY = "Weather Detail Button Result";
    static final String I_RESULT_SET_CURRENT_KEY = "Weather Detail Set Current";
    static final String I_RESULT_SAVE_KEY = "Weather Detail Save City";
    static final int REQ_CODE = 0x01;

    private ArrayList<City> citiesList = new ArrayList<>();
    private City citySentToWeatherActivity = null;
    private City currentSetCity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clCityIsSet = findViewById(R.id.idMainClCityIsSet);
        clCityNotSet = findViewById(R.id.idMainClCityNotSet);
        clNoSavedCities = findViewById(R.id.idMainClNoSavedCities);
        clSavedCities = findViewById(R.id.idMainClSavedCities);
        clLoadingScreen = findViewById(R.id.idMainClLoading);
        clMainScreen = findViewById(R.id.idMainClMainScreen);
        tvLocationName = findViewById(R.id.idMainTvLocationName);
        tvWeather = findViewById(R.id.idMainTvLocationWeather);
        tvTemperature = findViewById(R.id.idMainTvTemp);
        tvLastUpdated = findViewById(R.id.idMainTvUpdatedTime);
        etCityEntered = findViewById(R.id.idMainEtCityName);
        etCountryEntered = findViewById(R.id.idMainEtCountry);
        btnSetCurrentCity = findViewById(R.id.idMainBtnSetCurrentCity);
        btnSearch = findViewById(R.id.idMainBtnSearchCity);
        ivWeatherImage = findViewById(R.id.idMainIvWeatherImage);
        recyclerView = findViewById(R.id.idMainRvSavedCitiesList);

        sharedPref = this.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        citiesList = new ArrayList<>();

        //If there is no current city set, show clCityNotSet and hide clCityIsSet
        if(!sharedPref.contains(SP_CURRENT_CITY_KEY)){
            clCityIsSet.setVisibility(View.GONE);
            clCityNotSet.setVisibility(View.VISIBLE);
        }else{ //If there is a current city set, show clCityIsSet
            clCityIsSet.setVisibility(View.VISIBLE);
            clCityNotSet.setVisibility(View.GONE);

            Gson gson = new Gson();
            City currentCity;
            currentCity = gson.fromJson(sharedPref.getString(SP_CURRENT_CITY_KEY,""), City.class);

            new GetCurrentConditionsCurrentCityAsync().execute(currentCity);
        }

        //If there is no saved cities, show clNoSavedCities
        if(!sharedPref.contains(SP_SAVED_CITIES_LIST_KEY)){
            clSavedCities.setVisibility(View.GONE);
            clNoSavedCities.setVisibility(View.VISIBLE);
        }else{ //If there are saved cities, show clSavedCities
            clSavedCities.setVisibility(View.VISIBLE);
            clNoSavedCities.setVisibility(View.GONE);

            Gson gson = new Gson();
            City[] currentCities;
            currentCities = gson.fromJson(sharedPref.getString(SP_SAVED_CITIES_LIST_KEY,""), City[].class);
            citiesList.addAll(Arrays.asList(currentCities));

            new GetCurrentConditionsCitiesListAsync().execute(currentCities);
        }

        btnSetCurrentCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city ="";
                String country ="";

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


                LayoutInflater inflater = getLayoutInflater();
                final View alertView = inflater.inflate(R.layout.alert_dialog_search_city, null);
                builder.setView(alertView);
                final EditText alertEtCity = alertView.findViewById(R.id.idAlertEtCity);
                final EditText alertEtCountry = alertView.findViewById(R.id.idAlertEtCountry);

                builder.setTitle("Enter City Details")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String city = alertEtCity.getText().toString();
                                String country = alertEtCountry.getText().toString();

                                if (city.equals("") || country.equals("")){
                                    Toast.makeText(MainActivity.this, "City and Country must be entered", Toast.LENGTH_SHORT).show();
                                }else {
                                    new LocationSearchCurrentCityAsync().execute(country, city);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                builder.create().show();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = etCityEntered.getText().toString();
                String country = etCountryEntered.getText().toString();

                if (city.equals("") || country.equals("")){
                    Toast.makeText(MainActivity.this, "City and Country must be entered", Toast.LENGTH_SHORT).show();
                }else {
                    new LocationSearchMainSearchAsync().execute(country, city);
                }
            }
        });

        tvLocationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city ="";
                String country ="";

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


                LayoutInflater inflater = getLayoutInflater();
                final View alertView = inflater.inflate(R.layout.alert_dialog_search_city, null);
                builder.setView(alertView);
                final EditText alertEtCity = alertView.findViewById(R.id.idAlertEtCity);
                final EditText alertEtCountry = alertView.findViewById(R.id.idAlertEtCountry);

                builder.setTitle("Enter City Details")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String city = alertEtCity.getText().toString();
                                String country = alertEtCountry.getText().toString();

                                if (city.equals("") || country.equals("")){
                                    Toast.makeText(MainActivity.this, "City and Country must be entered", Toast.LENGTH_SHORT).show();
                                }else {
                                    new LocationSearchCurrentCityAsync().execute(country, city);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                builder.create().show();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (citiesList != null && citiesList.size() > 0) {
            City[] citiesSaved = new City[citiesList.size()];
            citiesList.toArray(citiesSaved);

            Gson gson = new Gson();
            String cityListGsonString = gson.toJson(citiesSaved);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SP_SAVED_CITIES_LIST_KEY, cityListGsonString);
            editor.apply();
        }else{
            if (sharedPref.contains(SP_SAVED_CITIES_LIST_KEY)){
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(SP_SAVED_CITIES_LIST_KEY);
                editor.apply();
            }
        }
    }

    private void setCurrentCity(City city){
        if (currentSetCity == null){
            Toast.makeText(this, "Current City Details Saved", Toast.LENGTH_SHORT).show();
        }else if (!currentSetCity.equals(city)){
            Toast.makeText(this, "Current City Details Saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Current City Updated", Toast.LENGTH_SHORT).show();
        }

        currentSetCity = city;

        tvLocationName.setText(city.getCityname() + ", " + city.getCountry());
        tvWeather.setText(city.getWeatherText());
        tvTemperature.setText(getResources().getString(R.string.resMainTvTemp) + " " + city.getTemperature() + "F");
        Picasso.get().load("http://developer.accuweather.com/sites/default/files/" + city.getWeatherIcon() + "-s.png").into(ivWeatherImage);

        tvLastUpdated.setText(getResources().getString(R.string.resMainTvUpdatedTime) + " " + city.getLastUpdated());
        etCityEntered.setText(city.getCityname());
        etCountryEntered.setText(city.getCountry());

        clCityNotSet.setVisibility(View.GONE);
        clCityIsSet.setVisibility(View.VISIBLE);

        Gson gson = new Gson();
        String currentCityGsonString = gson.toJson(city);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SP_CURRENT_CITY_KEY, currentCityGsonString);
        editor.apply();
    }

    private void setSavedCitiesList(){
        if(adaptor == null) {
            recyclerView = (RecyclerView) findViewById(R.id.idMainRvSavedCitiesList);
            adaptor = new MyMainRvAdaptor(MainActivity.this, citiesList);
            recyclerView.setAdapter(adaptor);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        }else{
            adaptor.notifyDataSetChanged();
        }

        clSavedCities.setVisibility(View.VISIBLE);
        clNoSavedCities.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE && resultCode == RESULT_OK){
            if(data.getExtras().getString(I_RESULT_KEY).equals(I_RESULT_SAVE_KEY)){
                if(citiesList.contains(citySentToWeatherActivity)){ //City already in list
                    //Update weather conditions
                    Toast.makeText(this, "City Updated", Toast.LENGTH_SHORT).show();
                    new GetCurrentConditionsCitiesListAsync().execute(citySentToWeatherActivity);
                }else{ //City needs to be added to list
                    citiesList.add(citySentToWeatherActivity);
                    Toast.makeText(this, "City Saved", Toast.LENGTH_SHORT).show();
                    new GetCurrentConditionsCitiesListAsync().execute(citySentToWeatherActivity);
                }
            }else if (data.getExtras().getString(I_RESULT_KEY).equals(I_RESULT_SET_CURRENT_KEY)){
                new GetCurrentConditionsCurrentCityAsync().execute(citySentToWeatherActivity);
            }
        }

    }

    @Override
    public void receiveSelectedCity(int position) {
        citySentToWeatherActivity = citiesList.get(position);

        Intent i = new Intent(MainActivity.this, CityWeatherActivity.class);
        i.putExtra(I_CITY_KEY, citySentToWeatherActivity);
        startActivityForResult(i, REQ_CODE);
    }

    @Override
    public void receiveDelete(int position) {
        citiesList.remove(position);

        if (citiesList.size() > 0 ) {
            adaptor.notifyDataSetChanged();
        }else{
            clCityNotSet.setVisibility(View.VISIBLE);
            clCityIsSet.setVisibility(View.GONE);
        }
    }

    class LocationSearchCurrentCityAsync extends AsyncTask<String, Void, ArrayList<City>>{


        @Override
        protected void onPreExecute() {
            clLoadingScreen.setVisibility(View.VISIBLE);
            clMainScreen.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<City> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            ArrayList<City> citiesArrayList = new ArrayList<>();
            String country = strings[0];
            String city = strings[1];

            try {
                URL url = new URL("http://dataservice.accuweather.com/locations/v1/cities/"
                        + country
                        + "/search?"
                        + "apikey=" + getResources().getString(R.string.api_key)
                        + "&q=" + city);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    Log.d("Main Activity", json);

                    JSONArray rootArray = new JSONArray(json);
                    for (int i = 0; i < rootArray.length(); i++) {
                        JSONObject mainObject = rootArray.getJSONObject(i);
                        City currentCity = new City();
                        currentCity.setCitykey(mainObject.getString("Key"));
                        currentCity.setCityname(mainObject.getString("EnglishName"));

                        JSONObject subObject = mainObject.getJSONObject("Country");
                        currentCity.setCountry(subObject.getString("ID"));

                        subObject = mainObject.getJSONObject("AdministrativeArea");
                        currentCity.setState(subObject.getString("ID"));

                        citiesArrayList.add(currentCity);

                        Log.v("RuntimeCheck",currentCity.toString());
                    }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
            }

            return citiesArrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<City> cities) {
            clLoadingScreen.setVisibility(View.GONE);
            clMainScreen.setVisibility(View.VISIBLE);

            if (cities.size() == 0){
                Toast.makeText(MainActivity.this, "City Not Found", Toast.LENGTH_SHORT).show();
            }else if (cities.size() == 1){
                    setCurrentCity(cities.get(0));
                    citiesList.add(cities.get(0));
            }else{ //More than 1 city is returned
                new GetCurrentConditionsCurrentCityAsync().execute(cities.get(0));
            }
        }
    }

    class LocationSearchMainSearchAsync extends AsyncTask<String, Void, ArrayList<City>>{
        @Override
        protected void onPreExecute() {
            clLoadingScreen.setVisibility(View.VISIBLE);
            clMainScreen.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<City> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            ArrayList<City> citiesArrayList = new ArrayList<>();
            String country = strings[0];
            String city = strings[1];

            try {
                URL url = new URL("http://dataservice.accuweather.com/locations/v1/cities/"
                        + country
                        + "/search?"
                        + "apikey=" + getResources().getString(R.string.api_key)
                        + "&q=" + city);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    Log.d("Main Activity", json);

                    JSONArray rootArray = new JSONArray(json);
                    for (int i = 0; i < rootArray.length(); i++) {
                        JSONObject mainObject = rootArray.getJSONObject(i);
                        City currentCity = new City();
                        currentCity.setCitykey(mainObject.getString("Key"));
                        currentCity.setCityname(mainObject.getString("EnglishName"));

                        JSONObject subObject = mainObject.getJSONObject("Country");
                        currentCity.setCountry(subObject.getString("ID"));

                        subObject = mainObject.getJSONObject("AdministrativeArea");
                        currentCity.setState(subObject.getString("ID"));

                        citiesArrayList.add(currentCity);

                        Log.v("RuntimeCheck",currentCity.toString());
                    }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
            }

            return citiesArrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<City> cities) {
            clLoadingScreen.setVisibility(View.GONE);
            clMainScreen.setVisibility(View.VISIBLE);

            if (cities.size() == 0){
                Toast.makeText(MainActivity.this, "City Not Found", Toast.LENGTH_SHORT).show();
            }else if (cities.size() == 1){
                citySentToWeatherActivity = cities.get(0);
                Intent i = new Intent(MainActivity.this, CityWeatherActivity.class);
                i.putExtra(I_CITY_KEY, cities.get(0));
                startActivityForResult(i, REQ_CODE);
            }else{ //More than 1 city is returned
                CharSequence[] cityNames = new CharSequence[cities.size()];
                for (int i = 0; i < cities.size(); i++){
                    cityNames[i] = (cities.get(i).getCityname() + ", " + cities.get(i).getState());
                }

                AlertDialog.Builder multiResultsBuilder = new AlertDialog.Builder(MainActivity.this);
                multiResultsBuilder.setTitle("Select City")
                        .setItems(cityNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Alert City Selection", cities.get(which).toString());
                                citySentToWeatherActivity = cities.get(which);

                                Intent i = new Intent(MainActivity.this, CityWeatherActivity.class);
                                i.putExtra(I_CITY_KEY, cities.get(which));
                                startActivityForResult(i, REQ_CODE);
                            }
                        });

                multiResultsBuilder.create().show();
            }
        }
    }

    /**
     * Async method to get the current conditions for the set current city
     */
    class GetCurrentConditionsCurrentCityAsync extends AsyncTask<City, Void, ArrayList<City>>{
        @Override
        protected void onPreExecute() {
            clLoadingScreen.setVisibility(View.VISIBLE);
            clMainScreen.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<City> doInBackground(City... cities) {
            HttpURLConnection connection = null;

            ArrayList<City> currentCityList = new ArrayList<>();
            currentCityList.addAll(Arrays.asList(cities));

            for (City currentCity:currentCityList) {
                try {
                    URL url = new URL("http://dataservice.accuweather.com/currentconditions/v1/"
                            + currentCity.getCitykey()
                            + "?"
                            + "apikey=" + getResources().getString(R.string.api_key));
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                        Log.d("Main Activity", json);

                        //Get icon, weatherText, temperature, LocalObservationDateTime
                        JSONArray rootArray = new JSONArray(json);
                        for (int i = 0; i < rootArray.length(); i++) {
                            JSONObject mainObject = rootArray.getJSONObject(i);

                            currentCity.setLocalObservationDateTime(mainObject.getString("LocalObservationDateTime"));
                            currentCity.setEpochTime(mainObject.getLong("EpochTime"));
                            currentCity.setWeatherText(mainObject.getString("WeatherText"));
                            currentCity.setWeatherIcon(mainObject.getString("WeatherIcon"));

                            JSONObject subObject = mainObject.getJSONObject("Temperature");
                            JSONObject internalSubObject = subObject.getJSONObject("Imperial");
                            currentCity.setTemperature(internalSubObject.getString("Value"));

                            Log.v("RuntimeCheck", currentCity.toString());
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
            }

            return currentCityList;

        }

        @Override
        protected void onPostExecute(ArrayList<City> cities) {
            clLoadingScreen.setVisibility(View.GONE);
            clMainScreen.setVisibility(View.VISIBLE);
            setCurrentCity(cities.get(0));
        }

    }

    class GetCurrentConditionsCitiesListAsync extends AsyncTask<City, Void, ArrayList<City>> {
        @Override
        protected void onPreExecute() {
            clLoadingScreen.setVisibility(View.VISIBLE);
            clMainScreen.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<City> doInBackground(City... cities) {
            HttpURLConnection connection = null;

            ArrayList<City> currentCityList = new ArrayList<>();
            currentCityList.addAll(Arrays.asList(cities));

            for (City currentCity:currentCityList) {
                try {
                    URL url = new URL("http://dataservice.accuweather.com/currentconditions/v1/"
                            + currentCity.getCitykey()
                            + "?"
                            + "apikey=" + getResources().getString(R.string.api_key));
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                        Log.d("Main Activity", json);

                        //Get icon, weatherText, temperature, LocalObservationDateTime
                        JSONArray rootArray = new JSONArray(json);
                        for (int i = 0; i < rootArray.length(); i++) {
                            JSONObject mainObject = rootArray.getJSONObject(i);

                            currentCity.setLocalObservationDateTime(mainObject.getString("LocalObservationDateTime"));
                            currentCity.setWeatherText(mainObject.getString("WeatherText"));
                            currentCity.setWeatherIcon(mainObject.getString("WeatherIcon"));

                            currentCity.setEpochTime(mainObject.getLong("EpochTime"));
                            JSONObject subObject = mainObject.getJSONObject("Temperature");
                            JSONObject internalSubObject = subObject.getJSONObject("Imperial");
                            currentCity.setTemperature(internalSubObject.getString("Value"));

                            Log.v("RuntimeCheck", currentCity.toString());
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
            }

            return currentCityList;

        }

        @Override
        protected void onPostExecute(ArrayList<City> cities) {
            clLoadingScreen.setVisibility(View.GONE);
            clMainScreen.setVisibility(View.VISIBLE);
            //citiesList = cities;
            setSavedCitiesList();
        }
    }
}
