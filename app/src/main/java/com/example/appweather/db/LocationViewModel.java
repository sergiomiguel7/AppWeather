package com.example.appweather.db;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.appweather.models.LocationForecastsAssociation;

import java.util.List;

public class LocationViewModel extends AndroidViewModel {

    private LocationRepository repository;
    private LiveData<List<LocationForecastsAssociation>> locationsList;

    public LocationViewModel(Application application) {
        super(application);
        repository=new LocationRepository(application);
        locationsList=repository.getLocationList();
    }

    public LiveData<List<LocationForecastsAssociation>> getLocationsList(){
        return locationsList;
    }

    public void updateLocation(LocationForecastsAssociation locationForecastsAssociation){
        repository.updateLocation(locationForecastsAssociation);
    }

    public void insert(LocationForecastsAssociation locationForecastsAssociation){
        repository.insert(locationForecastsAssociation);
    }

    public void deleteLocation(int locationId){
        repository.deleteLocation(locationId);
    }

    public void updateForecasts(LocationForecastsAssociation locationForecastsAssociation){ repository.updateForecasts(locationForecastsAssociation);}
}
