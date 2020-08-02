package com.example.appweather.controllers;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.appweather.models.Forecast;
import com.example.appweather.models.Location;
import com.example.appweather.models.LocationForecastsAssociation;

import java.util.List;

@Dao
public abstract class LocationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract void insert(Location location);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract void insert(List<Forecast> forecasts);

    @Update
    public abstract void updateLocation(Location location);

    @Transaction
    public void updateForecasts(LocationForecastsAssociation locationForecastsAssociation) {
        deleteForecasts(locationForecastsAssociation.getLocation().getId());
        insert(locationForecastsAssociation.getForecasts());
    }

    @Transaction
    public void insert(LocationForecastsAssociation locationForecastsAssociation) {
        insert(locationForecastsAssociation.getLocation());
        insert(locationForecastsAssociation.getForecasts());
    }

    //when remove location is selected, remove also the locations because cascade
    @Query("DELETE FROM location_table WHERE id=:locationId")
    public abstract void deleteLocation(int locationId);

    //delete forecast to update
    @Query("DELETE FROM forecast_table WHERE locationId=:locationId")
    public abstract void deleteForecasts(int locationId);

    //when the app starts this retrive the locations that exists in db
    @Query("SELECT * from location_table ORDER BY atualLocation DESC")
    public abstract LiveData<List<LocationForecastsAssociation>> getAllLocations();


}