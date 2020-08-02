package com.example.appweather.controllers;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.appweather.MainActivity;
import com.example.appweather.interfaces.Provider;
import com.example.appweather.models.Forecast;
import com.example.appweather.models.Location;
import com.example.appweather.models.LocationForecastsAssociation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class LocationsController {
    private Provider defaultProvider;
    private MutableLiveData<List<Location>> locationsAvailable;
    private MutableLiveData<List<Forecast>> locationForecast;
    private MutableLiveData<String> atualLocation;
    private static LocationsController instance = new LocationsController();


    //constructor
    public LocationsController() {
        this.defaultProvider = new ProviderIPMA();
        this.locationsAvailable = new MutableLiveData<>();
        locationForecast=new MutableLiveData<>();
        atualLocation=new MutableLiveData<>();
    }

    //getters and setters
    public static LocationsController getInstance() {
        return instance;
    }

    public Provider getDefaultProvider() {
        return defaultProvider;
    }

    public void setDefaultProvider(Provider defaultProvider) {
        this.defaultProvider = defaultProvider;
    }

    public MutableLiveData<List<Location>> getLocationsAvailable() {
        return locationsAvailable;
    }

    public MutableLiveData<List<Forecast>> getLocationForecast() {
        return locationForecast;
    }

    public MutableLiveData<String> getAtualLocation() {
        return atualLocation;
    }

    public void setAtualLocation(String atualLocation) {
        this.atualLocation.setValue(atualLocation);
    }

    //when the activity result return a cityname, try to get the location associated
    public Location getLocationAvailable(String name) {
        for (Location l : locationsAvailable.getValue()) {
            if (l.getNome().equals(name))
                return l;
        }
        return null;
    }

    //methods
    public void beginGetLocations() {
        new getLocationsAPIasync(defaultProvider).execute();
    }

    public void updateForecasts(LocationForecastsAssociation locationToUpdate){
        locationForecast = new MutableLiveData<>();
        new getForecastasync(defaultProvider,locationToUpdate).execute(locationToUpdate);
    }

    public void askAtualLocation(double latitude, double longitude) {
        new AtualLocation().execute(latitude, longitude);
    }

    //asyncTask Classes


    //request to api the list of available cities
    private static class getLocationsAPIasync extends AsyncTask<Void, Void, Void> implements Observer {
        private Provider provider;

        public getLocationsAPIasync(Provider provider) {
            this.provider = provider;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (provider instanceof ProviderIPMA) {
                ((ProviderIPMA) provider).addObserver(this);
                this.provider.getLocationsAvailable();
            }
            return null;
        }

        @Override
        public void update(Observable o, Object arg) {
            List<JSONObject> toParse = (List<JSONObject>) arg;
            ((ProviderIPMA) provider).deleteObservers();
            List<Location> locationsAvailable = new ArrayList<>();
            try {
                for (JSONObject object : toParse) {
                    Location location = new Location(object.getDouble("latitude"), object.getDouble("longitude"), object.getString("local"), object.getInt("globalIdLocal"), false);
                    locationsAvailable.add(location);
                }
                LocationsController.getInstance().locationsAvailable.setValue(locationsAvailable);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private static class getForecastasync extends AsyncTask<LocationForecastsAssociation, Void, Void> implements Observer {

        private Provider provider;
        private LocationForecastsAssociation locationToUpdate;

        public getForecastasync(Provider provider, LocationForecastsAssociation locationToUpdate) {
            this.provider = provider;
            this.locationToUpdate = locationToUpdate;
        }

        @Override
        protected Void doInBackground(LocationForecastsAssociation... locationForecastsAssociations) {
            if (provider instanceof ProviderIPMA) {
                ((ProviderIPMA) provider).addObserver(this);
                this.provider.getForecasts(locationForecastsAssociations[0].getLocation().getId());
            }
            return null;
        }

        @Override
        public void update(Observable o, Object arg) {
            List<JSONObject> toParse = (List<JSONObject>) arg;
            ((ProviderIPMA) provider).deleteObserver(this);
            List<Forecast> forecasts = new ArrayList<>();

            try {
                for (JSONObject object : toParse) {
                    Forecast forecast = new Forecast(object.getString("forecastDate"), object.getInt("idWeatherType"), object.getDouble("tMin"), object.getDouble("tMax"), object.getDouble("precipitaProb"));
                    forecast.setLocationId(locationToUpdate.getLocation().getId());
                    forecasts.add(forecast);
                }
                locationToUpdate.setForecasts(forecasts);
                LocationsController.getInstance().locationForecast.setValue(forecasts);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static class AtualLocation extends AsyncTask<Double, Void, String> {

        @Override
        protected String doInBackground(Double... doubles) {

            double latitude = doubles[0];
            double longitude = doubles[1];

            Geocoder geocoder = new Geocoder(MainActivity.getContext());
            List<Address> addresses = null;
            Address address;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            address = addresses.get(0);
            Log.d("Cidade", address.getAdminArea());
            return address.getAdminArea();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            LocationsController.getInstance().setAtualLocation(s);
        }
    }
}


