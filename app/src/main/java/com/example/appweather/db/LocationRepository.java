package com.example.appweather.db;


import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.appweather.controllers.LocationDAO;
import com.example.appweather.models.LocationForecastsAssociation;

import java.util.List;

public class LocationRepository {
    private LocationDAO locationDAO;
    private LiveData<List<LocationForecastsAssociation>> locationList;


    public LocationRepository(Application application) {
        LocationRoomDatabase db = LocationRoomDatabase.getDatabase(application);
        locationDAO = db.locationDAO();
        locationList = locationDAO.getAllLocations();
    }

    LiveData<List<LocationForecastsAssociation>> getLocationList() {
        return locationList;
    }

    public void insert(LocationForecastsAssociation location) {
        new insertAsyncTask(locationDAO).execute(location);
    }

    public void deleteLocation(int locationId) {
        new deleteAsyncTask(locationDAO).execute(locationId);
    }

    public void updateForecasts(LocationForecastsAssociation locationForecastsAssociation){
        new updateAsyncTask(locationDAO).execute(locationForecastsAssociation);
    }

    public void updateLocation(LocationForecastsAssociation locationForecastsAssociation){
        new updateLocationAsyncTask(locationDAO).execute(locationForecastsAssociation);
    }

    private static class insertAsyncTask extends AsyncTask<LocationForecastsAssociation, Void, Void> {

        private LocationDAO asyncTaskDao;

        insertAsyncTask(LocationDAO dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(LocationForecastsAssociation... locationForecastsDetails) {
            asyncTaskDao.insert(locationForecastsDetails[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Integer, Void, Void> {

        private LocationDAO asyncTaskDao;

        deleteAsyncTask(LocationDAO dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            asyncTaskDao.deleteLocation(integers[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<LocationForecastsAssociation,Void,Void>{

        private LocationDAO asyncTaskDao;

        public updateAsyncTask(LocationDAO asyncTaskDao) {
            this.asyncTaskDao = asyncTaskDao;
        }

        @Override
        protected Void doInBackground(LocationForecastsAssociation... locationForecastsAssociations) {
            asyncTaskDao.updateForecasts(locationForecastsAssociations[0]);
            return null;
        }
    }

    private static class updateLocationAsyncTask extends AsyncTask<LocationForecastsAssociation,Void,Void>{

        private LocationDAO asyncTaskDao;

        public updateLocationAsyncTask(LocationDAO asyncTaskDao) {
            this.asyncTaskDao = asyncTaskDao;
        }

        @Override
        protected Void doInBackground(LocationForecastsAssociation... locationForecastsAssociations) {
            asyncTaskDao.updateLocation(locationForecastsAssociations[0].getLocation());
            return null;
        }
    }
}
