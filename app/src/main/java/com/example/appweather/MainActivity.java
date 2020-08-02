package com.example.appweather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.appweather.controllers.LocationFragmentController;
import com.example.appweather.controllers.LocationsController;
import com.example.appweather.db.LocationViewModel;
import com.example.appweather.models.Forecast;
import com.example.appweather.models.Location;
import com.example.appweather.models.LocationForecastsAssociation;
import com.example.appweather.views.ChooseLocationActivity;
import com.example.appweather.views.LocationFragment;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static Context context;

    private List<LocationFragment> fragmentList = new ArrayList<>();
    private String[] citiesNames;

    private LocationViewModel locationViewModel;
    private LocationsController locationsController;
    private LocationFragmentController fragmentController;

    private ViewPager viewPager;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        viewPager = findViewById(R.id.view_pager_fragment1);
        progressBar = findViewById(R.id.progressbar);
        locationsController = LocationsController.getInstance();
        fragmentController = new LocationFragmentController(getSupportFragmentManager(), fragmentList);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        viewPager.setAdapter(fragmentController);


        //when the app is created citiesName will be equal to the citiesAvailable
        locationsController.beginGetLocations();
        locationsController.getLocationsAvailable().observe(this, new Observer<List<Location>>() {
            @Override
            public void onChanged(List<Location> locations) {
                if (locations != null) {
                    if (locations.size() > 0) {
                        citiesNames = new String[locations.size()];
                        int i = 0;
                        for (Location l : locations) {
                            citiesNames[i] = l.getNome();
                            i++;
                        }
                        getCurrentLocation();
                    }
                }
            }
        });


        //when the gps return the actual location
        locationsController.getAtualLocation().observe(MainActivity.this, new Observer<String>() {
            @Override
            public void onChanged(final String s) {
                boolean exists = false;
                if (locationViewModel.getLocationsList().getValue() != null) {
                    for (LocationForecastsAssociation l : locationViewModel.getLocationsList().getValue()) {
                        if (l.getLocation().isAtualLocation()) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists)
                        showLocation(s);
                }
            }
        });

        //always the db is changed it will update the ui
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        locationViewModel.getLocationsList().observe(this, new Observer<List<LocationForecastsAssociation>>() {
            @Override
            public void onChanged(List<LocationForecastsAssociation> locationForecastsAssociations) {
                updateViewPager();
            }
        });
    }


    public static Context getContext() {
        return context;
    }

    public static RequestQueue getRequestQueue() {
        return Volley.newRequestQueue(getContext());
    }

    //always the activity main is opened it check if the last update was 1 hour ago, if it more
    //it update the data
    @Override
    protected void onStart() {
        super.onStart();
        if (locationViewModel.getLocationsList().getValue() != null) {
            for (LocationForecastsAssociation l : locationViewModel.getLocationsList().getValue()) {
                if (l.getLocation().getLastCheck() != null) {
                    LocalDateTime lastCheck = LocalDateTime.parse(l.getLocation().getLastCheck());
                    if (LocalDateTime.now().isAfter(lastCheck.plusHours(1))) {
                        onRefresh();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    // manage the actions of the user (add or remove location)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_adicionar:
                if (citiesNames != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(this, ChooseLocationActivity.class);
                    intent.putExtra("citiesAvailable", citiesNames);
                    startActivityForResult(intent, 1);
                    return true;
                } else
                    Toast.makeText(getContext(), "Restart the app.", Toast.LENGTH_SHORT).show();
            case R.id.action_remove:
                if (fragmentList.size() > 0) {
                    int index = viewPager.getCurrentItem();
                    locationViewModel.deleteLocation(((LocationFragment) fragmentController.getItem(index)).getLocationForecastAssociation().getLocation().getId());
                    fragmentController.removeFragment(index);
                    viewPager.setAdapter(fragmentController);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //when the user choose one location, here is inserted a new location and request the forecasts to the api
    // and insert it at the db
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.INVISIBLE);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                final String reply = data.getStringExtra("locationName");
                Location l = locationsController.getLocationAvailable(reply);
                if (l != null) {
                    final LocationForecastsAssociation newLocation = new LocationForecastsAssociation();
                    newLocation.setLocation(l);
                    locationsController.updateForecasts(newLocation);
                    locationsController.getLocationForecast().observe(this, new Observer<List<Forecast>>() {
                        @Override
                        public void onChanged(List<Forecast> forecasts) {
                            if (forecasts != null) {
                                if (forecasts.size() > 0) {
                                    String updateHour = String.valueOf(LocalDateTime.now());
                                    newLocation.setForecasts(forecasts);
                                    newLocation.getLocation().setFavorito(true);
                                    newLocation.getLocation().setLastCheck(updateHour);
                                    locationViewModel.insert(newLocation);
                                    locationsController.getLocationForecast().removeObserver(this);
                                }
                            }
                        }
                    });
                }
            } else if (resultCode == 240)
                Toast.makeText(getContext(), "wifi not connected", Toast.LENGTH_SHORT).show();

        }
    }

    //when the db suffer modifications all the ui is updated
    public void updateViewPager() {
        boolean exists = false;
        if (locationViewModel.getLocationsList().getValue() != null) {
            for (LocationForecastsAssociation l : locationViewModel.getLocationsList().getValue()) {
                for (LocationFragment locationFragment1 : fragmentList) {
                    if (locationFragment1.getLocationForecastAssociation().getLocation().getNome().equals(l.getLocation().getNome())) {
                        locationFragment1.setLocation(l);
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    LocationFragment locationFragment = new LocationFragment(l);
                    fragmentController.addFragment(locationFragment);
                }
                exists = false;
            }
        }
        viewPager.setAdapter(fragmentController);
    }

    //this method is called by the refresh layout and updates all the forecasts and respective fragments
    @Override
    public void onRefresh() {
        if (locationViewModel.getLocationsList().getValue() != null) {
            if (i < fragmentList.size()) {
                updateForecast();
            } else {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                    i = 0;
                }
            }
        }

    }

    //update in the db all the forecasts and store the current local hour
    public void updateForecast() {
        final LocationForecastsAssociation l = ((LocationFragment) fragmentController.getItem(i)).getLocationForecastAssociation();
        locationsController.updateForecasts(l);
        locationsController.getLocationForecast().observe(this, new Observer<List<Forecast>>() {
            @Override
            public void onChanged(List<Forecast> forecasts) {
                if (forecasts != null) {
                    if (forecasts.size() > 0) {
                        String updateHour = String.valueOf(LocalDateTime.now());
                        l.getLocation().setLastCheck(updateHour);
                        locationViewModel.updateLocation(l);
                        locationViewModel.updateForecasts(l);
                        i++;
                        locationsController.getLocationForecast().removeObserver(this);
                        onRefresh();
                    }
                }
            }
        });
    }

    //location methods
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                1);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    public void getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                final LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(3000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                LocationServices.getFusedLocationProviderClient(this)
                        .requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                        .removeLocationUpdates(this);
                                if (locationResult != null && locationResult.getLocations().size() > 0) {
                                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                                    locationsController.askAtualLocation(locationResult.getLocations().get(latestLocationIndex).getLatitude(), locationResult.getLocations().get(latestLocationIndex).getLongitude());
                                }
                            }
                        }, Looper.getMainLooper());
            } else {
                Toast.makeText(MainActivity.getContext(), "Ligue a localização", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else
            requestPermissions();
    }

    public void showLocation(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Pretende adicionar a sua localização atual?").setCancelable(false).setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                {

                    if (s != null) {
                        boolean found = false;
                        for (LocationForecastsAssociation l : Objects.requireNonNull(locationViewModel.getLocationsList().getValue())) {
                            if (l.getLocation().getNome().equals(s)) {
                                l.getLocation().setAtualLocation(true);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            Location l = locationsController.getLocationAvailable(s);
                            if (l != null) {
                                final LocationForecastsAssociation newLocation = new LocationForecastsAssociation();
                                newLocation.setLocation(l);
                                locationsController.updateForecasts(newLocation);
                                locationsController.getLocationForecast().observe(MainActivity.this, new Observer<List<Forecast>>() {
                                    @Override
                                    public void onChanged(List<Forecast> forecasts) {
                                        if (forecasts != null) {
                                            if (forecasts.size() > 0) {
                                                String updateHour = String.valueOf(LocalDateTime.now());
                                                newLocation.setForecasts(forecasts);
                                                newLocation.getLocation().setFavorito(true);
                                                newLocation.getLocation().setLastCheck(updateHour);
                                                newLocation.getLocation().setAtualLocation(true);
                                                locationViewModel.insert(newLocation);
                                                locationsController.getLocationForecast().removeObserver(this);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
                ;
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

}
