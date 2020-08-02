package com.example.appweather.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "forecast_table", foreignKeys = @ForeignKey(entity = Location.class, parentColumns = "id",
        childColumns = "locationId", onDelete = CASCADE))
public class Forecast {
    //id==posiçao
    @PrimaryKey(autoGenerate = true)
    private int forecastid;
    private String forecastDate;
    private int weathertype;
    private double min;
    private double max;
    private double precipitation;

    private int locationId;


    public Forecast() {
    }

    @Ignore
    public Forecast(String forecastDate, int weathertype, double min, double max, double precipitation) {
        this.forecastDate = forecastDate;
        this.weathertype = weathertype;
        this.min = min;
        this.max = max;
        this.precipitation = precipitation;
    }

    public int getForecastid() {
        return forecastid;
    }

    public void setForecastid(int forecastid) {
        this.forecastid = forecastid;
    }

    public String getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(String forecastDate) {
        this.forecastDate = forecastDate;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public void setWeathertype(int weathertype) {
        this.weathertype = weathertype;
    }

    public int getWeathertype() {
        return weathertype;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    @Ignore
    public String returnWeatherType() {
        if (this.weathertype == 1)
            return "Céu Limpo";
        else if(this.weathertype==2)
            return "Céu pouco nublado";
        else if ((weathertype > 2 && weathertype<=5))
            return "Céu nublado com aberturas de sol";
        else if (weathertype<=23)
            return "Chuvoso";
        else
            return "Céu nublado";


    }

}