package com.example.projectem8uf2;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddSpawnActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Controller service;
    public String spawnPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spawn);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(41.609, 2.2873)));

        /**
         * Al fer click en el retorna al result la FK del Poke i la geolocalització per fer el PUT al onActivityResult de la MapsActivity
         */
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddSpawnActivity.this);
                builder.setTitle("Choose a Poké ID");

                final Spinner input = new Spinner(AddSpawnActivity.this);
                String[] arrayPokeID = new String[] {"8", "9", "10", "11" , "12", "13"};

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddSpawnActivity.this,
                        android.R.layout.simple_spinner_item, arrayPokeID);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                input.setAdapter(adapter);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_OK);
                        try {
                            Intent i = new Intent();
                            i.putExtra("pokeid", spawnPokemon);
                            i.putExtra("lat", latLng.latitude);
                            i.putExtra("lng", latLng.longitude);
                        } catch (Exception ex) {
                            Log.i("errorAPI", "Sense web service petarà");
                            Intent i = new Intent();
                            i.putExtra("pokeid", "");
                            i.putExtra("lat", "");
                            i.putExtra("lng", "");
                        }
                        finally {
                            finish();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }
}
