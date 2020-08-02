package com.example.appweather.controllers;

import com.example.appweather.interfaces.Provider;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class ProviderIPMA extends Observable implements Provider, Observer {
    private final String LOCATION_URL = "https://api.ipma.pt/open-data/distrits-islands.json";
    private final String PREVISAO_URL = "https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/%d.json";
    private List<JSONObject> objects;
    private HttpRequest httpRequest = HttpRequest.getINSTANCE();

    public ProviderIPMA() {
    }

    @Override
    public void getLocationsAvailable() {
        HttpRequest.getINSTANCE().setUrl(LOCATION_URL);
        httpRequest.addObserver(this);
        httpRequest.doRequest();
    }

    @Override
    public void getForecasts(int globalIdLocal){
        HttpRequest.getINSTANCE().setUrl(String.format(Locale.getDefault(), PREVISAO_URL, globalIdLocal));
        httpRequest.addObserver(this);
        httpRequest.doRequest();
    }

    @Override
    public void update(Observable o, Object arg) {
        objects=(List<JSONObject>) arg;
        HttpRequest.getINSTANCE().clearAll();
        httpRequest.deleteObservers();
        setChanged();
        notifyObservers(objects);
    }
}