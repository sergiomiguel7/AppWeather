package com.example.appweather.views;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.appweather.MainActivity;
import com.example.appweather.R;
import com.example.appweather.models.Forecast;
import com.example.appweather.models.LocationForecastsAssociation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment {

    private LocationForecastsAssociation location;
    private ForecastListAdapter recyclerviewadapter;
    private RecyclerView recyclerView;
    private ImageView weather_image;
    private TextView max_temp, min_temp;
    private TextView textView;
    private TextView localtime;
    private TextView weatherType;
    private TextView update;


    public LocationFragment(LocationForecastsAssociation location) {
        this.location = location;
        recyclerviewadapter = new ForecastListAdapter();
        recyclerviewadapter.updateForecasts(location.getForecasts());
    }

    public void setLocation(LocationForecastsAssociation location) {
        this.location = location;
        if (update != null)
            updateTextUpdate();
        recyclerviewadapter.updateForecasts(location.getForecasts());
    }

    public LocationForecastsAssociation getLocationForecastAssociation() {
        return location;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        weather_image = view.findViewById(R.id.weather_image);
        max_temp = view.findViewById(R.id.max_temp);
        min_temp = view.findViewById(R.id.min_temp);
        textView = view.findViewById(R.id.city_name);
        localtime = view.findViewById(R.id.localdate);
        weatherType = view.findViewById(R.id.weather_type);
        update = view.findViewById(R.id.update);


        updateView();

        return view;
    }

    private void updateView() {
        if (location.getLocation().getLastCheck() != null)
            updateTextUpdate();


        textView.setText(location.getLocation().getNome());

        int weathertype = location.getForecasts().get(0).getWeathertype();

        if (weathertype <= 2)
            weather_image.setBackgroundResource(R.drawable.sun);
        else if (weathertype <= 5)
            weather_image.setBackgroundResource(R.drawable.sun_with_clouds);
        else if (weathertype <= 23)
            weather_image.setBackgroundResource(R.drawable.rain);
        else
            weather_image.setBackgroundResource(R.drawable.cloud);

        weather_image.setLayoutParams(new RelativeLayout.LayoutParams(400, 400));
        weatherType.setText(location.getForecasts().get(0).returnWeatherType());
        localtime.setText(LocalDateTime.now().toString());
        localtime.setText(String.format("%s", LocalDate.now()));
        max_temp.setText(String.format("%sº", String.valueOf(Math.round(location.getForecasts().get(0).getMax()))));
        min_temp.setText(String.format("%sº", String.valueOf(Math.round(location.getForecasts().get(0).getMin()))));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerViewSpaceDecoration itemDecorator = new RecyclerViewSpaceDecoration(20);

        recyclerView.addItemDecoration(itemDecorator);
        recyclerView.setAdapter(recyclerviewadapter);
        recyclerView.setLayoutManager(layoutManager);

    }

    public void updateTextUpdate() {
        LocalDateTime lastCheck = LocalDateTime.parse(location.getLocation().getLastCheck());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String checked = formatter.format(lastCheck);
        String timeago = "Atualizado às: " + checked;
        this.update.setText(timeago);
    }

    public static class RecyclerViewSpaceDecoration extends RecyclerView.ItemDecoration {
        private final int horizontalSpace;

        public RecyclerViewSpaceDecoration(int horizontalSpace) {
            this.horizontalSpace = horizontalSpace;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.right = horizontalSpace;
        }
    }
}





class ForecastListAdapter extends RecyclerView.Adapter<ForecastListAdapter.ForecastViewHolder> {

    private List<Forecast> forecasts;

    public ForecastListAdapter() {
    }

    public void updateForecasts(List<Forecast> newForecasts) {
        forecasts = new ArrayList<>();
        forecasts.addAll(newForecasts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.getContext());
        View itemView = inflater.inflate(R.layout.forecast_item, parent, false);
        return new ForecastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        String dateInPortugal = formatData(position);
        if (position == 0)
            holder.forecastDate.setText(R.string.Dia);
        else if (dateInPortugal.contains("-feira")) {
            dateInPortugal = dateInPortugal.replace("-feira", "");
            holder.forecastDate.setText(dateInPortugal);
        } else
            holder.forecastDate.setText(dateInPortugal);

        holder.tempMin.setText(String.valueOf(Math.round(forecasts.get(position).getMin())) + "º");
        holder.tempMax.setText(String.valueOf(Math.round(forecasts.get(position).getMax())) + "º");

        double precipitation = (int) forecasts.get(position).getPrecipitation();
        String prob_precipit = precipitation + "%";
        holder.text_prob.setText(prob_precipit);

        int weathertype = forecasts.get(position).getWeathertype();

        if (weathertype <= 2)
            holder.tempo.setBackgroundResource(R.drawable.sun_tiny);
        else if (weathertype <= 5)
            holder.tempo.setBackgroundResource(R.drawable.sun_clouds_tiny2);
        else if (weathertype <= 23)
            holder.tempo.setBackgroundResource(R.drawable.rain_tiny2);
        else
            holder.tempo.setBackgroundResource(R.drawable.cloud_tiny2);


        if (precipitation == 0)
            holder.precipit_prob.setBackgroundResource(R.drawable.gota_vazia_image);
        else if (precipitation <= 25)
            holder.precipit_prob.setBackgroundResource(R.drawable.gota_25_image);
        else if (precipitation <= 50)
            holder.precipit_prob.setBackgroundResource(R.drawable.gota_50_image);
        else if (precipitation <= 75)
            holder.precipit_prob.setBackgroundResource(R.drawable.gota_75_image);
        else
            holder.precipit_prob.setBackgroundResource(R.drawable.gota_100_image);
    }


    @Override
    public int getItemCount() {
        if (forecasts != null)
            return forecasts.size();
        else return 0;
    }

    private String formatData(int position) {
        LocalDate localDate;
        String[] datas = forecasts.get(position).getForecastDate().split("-");
        localDate = LocalDate.of(Integer.parseInt(datas[0]), Integer.parseInt(datas[1]), Integer.parseInt(datas[2]));
        Locale portugueseLocale = new Locale("pt", "PT");
        return localDate.format(DateTimeFormatter.ofPattern("EEEE", portugueseLocale));
    }


    class ForecastViewHolder extends RecyclerView.ViewHolder {

        TextView forecastDate, tempMin, tempMax, text_prob;
        ImageView tempo, precipit_prob;

        public ForecastViewHolder(View itemView) {
            super(itemView);
            forecastDate = itemView.findViewById(R.id.previsao_date);
            text_prob = itemView.findViewById(R.id.text_prob);
            tempMin = itemView.findViewById(R.id.temp_min);
            tempMax = itemView.findViewById(R.id.temp_max);
            tempo = itemView.findViewById(R.id.temp);
            precipit_prob = itemView.findViewById(R.id.precipit_prob);

        }
    }

}


