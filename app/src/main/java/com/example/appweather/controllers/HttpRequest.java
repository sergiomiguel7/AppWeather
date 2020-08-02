package com.example.appweather.controllers;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.appweather.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class HttpRequest extends Observable {
    private String url;
    private List<JSONObject> objects;

    private static HttpRequest INSTANCE = new HttpRequest();

    public HttpRequest() {
        this.objects=new ArrayList<>();
    }

    public static HttpRequest getINSTANCE() {
        return INSTANCE;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void clearAll(){
        this.objects=new ArrayList<>();
    }

    void doRequest(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, INSTANCE.url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<JSONObject> result = new ArrayList<>();
                        try {
                            JSONArray values = response.getJSONArray("data");
                            List<JSONObject> temp = new ArrayList<>();
                            for (int i = 0; i < values.length(); i++) {
                                temp.add(values.getJSONObject(i));
                            }
                            result.addAll(temp);
                            INSTANCE.objects.addAll(result);
                            setChanged();
                            notifyObservers(objects);
                            Log.d("json size", String.valueOf(temp.size()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MainActivity.getRequestQueue().add(request);

    }

}
