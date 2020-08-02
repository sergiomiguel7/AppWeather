package com.example.appweather.models;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class LocationForecastsAssociation {

    public LocationForecastsAssociation(){}

    @Embedded
    private Location location;

    @Relation(parentColumn = "id", entityColumn = "locationId", entity = Forecast.class)
    private List<Forecast> forecasts;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<Forecast> forecasts) {
        this.forecasts=new ArrayList<>();
        this.forecasts = forecasts;
    }


}