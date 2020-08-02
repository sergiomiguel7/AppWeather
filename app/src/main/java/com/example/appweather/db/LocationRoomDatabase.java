package com.example.appweather.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.appweather.controllers.LocationDAO;
import com.example.appweather.models.Forecast;
import com.example.appweather.models.Location;

@Database(entities = {Location.class, Forecast.class}, version = 2, exportSchema = false)
public abstract class LocationRoomDatabase extends RoomDatabase {
    public abstract LocationDAO locationDAO();

    private static LocationRoomDatabase INSTANCE;

    public static LocationRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocationRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LocationRoomDatabase.class, "location_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallBack)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallBack =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    if (!db.isReadOnly()) {
                        db.execSQL("PRAGMA foreign_keys=ON;");}
                    new PopulateDbAsync(INSTANCE).execute();
                }

            };



    private static class PopulateDbAsync extends AsyncTask<Void,Void,Void> {
        private final LocationDAO locationDAO;

        PopulateDbAsync(LocationRoomDatabase db){
            locationDAO=db.locationDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}