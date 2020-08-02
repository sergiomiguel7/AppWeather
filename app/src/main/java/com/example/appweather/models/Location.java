package com.example.appweather.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.List;

@Entity(tableName = "location_table")
public class Location {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;
    private double latitude, longitude;
    private String nome;
    private boolean favorito;
    private boolean atualLocation;
    private String lastCheck;
    @Ignore
    private List<Forecast> forecasts;


    public Location(double latitude, double longitude, String nome, int id, boolean favorito) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.nome = nome;
        this.id = id;
        this.favorito = favorito;
        this.atualLocation=false;
        this.lastCheck= String.valueOf(LocalDateTime.now());
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    public boolean isAtualLocation() {
        return atualLocation;
    }

    public void setAtualLocation(boolean atualLocation) {
        this.atualLocation = atualLocation;
    }

    public String getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(String lastCheck) {
        this.lastCheck = lastCheck;
    }
}
