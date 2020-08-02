package com.example.appweather.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.appweather.R;

public class ChooseLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);

        final boolean[] netValida = {false};
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = null;
        if (connMgr != null) {
            network = connMgr.getActiveNetwork();
        }
        ConnectivityManager.NetworkCallback cm = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                netValida[0] = true;
            }
        };
        cm.onAvailable(network);

        if(!netValida[0]){
            setResult(240);
            finish();
        }

        final AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autotext_complete);
        Intent intent = getIntent();
        final String[] locationsNames = intent.getStringArrayExtra("citiesAvailable");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ChooseLocationActivity.this, android.R.layout.simple_list_item_1, locationsNames);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                returnReply(item);
            }
        });

    }


    public void returnReply(String reply) {
        Intent replyIntent = new Intent();
        replyIntent.putExtra("locationName", reply);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

}

