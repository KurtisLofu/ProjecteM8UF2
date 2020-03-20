package com.example.projectem8uf2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static String pokeName;
    private GoogleMap mMap;
    Controller service;

    ArrayList<Raid> listaRaids = new ArrayList<>();
    ArrayList<Spawn> listaSpawn = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.allSpawn:
                mMap.clear();
                GetAllSpawnsFromAPI();
                return true;

            case R.id.allRaids:
                mMap.clear();
                GetAllRaidsFromAPI();
                return true;

            case R.id.both:
                mMap.clear();
                GetAllSpawnsFromAPI();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                GetAllRaidsFromAPI();
                return true;

            case R.id.insertSpawn:
              startActivityForResult(new Intent(this, AddSpawnActivity.class), 1);
            return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:44300/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(Controller.class);

        mMap.setMyLocationEnabled(true);

        /**
         * Al fer click en un marker, s'obrirà un dialog que pregunta si vols esborrar-lo. Ja sigui Raid o Spawn.
         *
         */

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String snippet = marker.getSnippet();
                String id = snippet.substring(snippet.indexOf(":") + 1).trim();
                boolean isRaid = marker.getTitle().contains("Raid");

                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Alert!")
                        .setMessage("Do you want to delete this marker?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (isRaid) {
                                    Call<Void> call = service.deleteRaid(Integer.parseInt(id));
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(MapsActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                            GetAllRaidsFromAPI();
                                            ColocarMarkersRaids();
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {

                                        }
                                    });

                                } else {

                                    Call<Void> call = service.deleteSpawn(Integer.parseInt(id));
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Toast.makeText(MapsActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                            GetAllSpawnsFromAPI();
                                            ColocarMarkersSpawns();
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {

                                        }
                                    });
                                }
                            }
                        })

                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


                return false;
            }
        });

        /**
         * Es dispara fent un longClick en el marker. Farà el PUT a la API amb la nova geolocalització del marker.
         */
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                /**
                 * Obté quina Raid/Spawn és a partir d'un GET de l'ID que apareix al snippet del marker
                  */
                String snippet = marker.getSnippet();
                String id = snippet.substring(snippet.indexOf(":") + 1).trim();
                boolean isRaid = marker.getTitle().contains("Raid");

                if (isRaid) {
                    Call<Raid> call = service.getRaidByID(Integer.parseInt(id));

                    call.enqueue(new Callback<Raid>() {
                        @Override
                        public void onResponse(Call<Raid> call, Response<Raid> response) {
                            Raid raid = response.body();
                            UpdateRaid(raid, marker);
                        }

                        @Override
                        public void onFailure(Call<Raid> call, Throwable t) {
                            Log.i("ggegege", "Error getRaidById: " + t.getMessage());
                        }
                    });
                } else {

                    Call<Spawn> call = service.getSpawnByID(Integer.parseInt(id));
                    call.enqueue(new Callback<Spawn>() {
                        @Override
                        public void onResponse(Call<Spawn> call, Response<Spawn> response) {
                            Spawn spawn = response.body();
                            try {
                                UpdateSpawn(spawn, marker);
                            } catch (Exception ex) {
                                Log.i("ggegege", ex.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<Spawn> call, Throwable t) {
                            Log.i("ggegege", "Error getSpawnById: " + t.getMessage());
                        }
                    });
                }
            }
        });
    }

    private void UpdateRaid(Raid raid, Marker marker) {

        raid.setLat((float) marker.getPosition().latitude);
        raid.setLng((float) marker.getPosition().longitude);
        Call<Raid> call = service.updateRaid(raid);

        call.enqueue(new Callback<Raid>() {
            @Override
            public void onResponse(Call<Raid> call, Response<Raid> response) {
                Toast.makeText(MapsActivity.this, "Updated position successfully", Toast.LENGTH_SHORT).show();
                //GetAllRaidsFromAPI();
                Log.i("ggegege", "Error getSpawnById: " + response);
            }

            @Override
            public void onFailure(Call<Raid> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateSpawn(Spawn spawn, Marker marker) {

        spawn.setLat((float) marker.getPosition().latitude);
        spawn.setLng((float) marker.getPosition().longitude);

        Call<Spawn> call = service.updateSpawn(spawn);

        call.enqueue(new Callback<Spawn>() {
            @Override
            public void onResponse(Call<Spawn> call, Response<Spawn> response) {
                Toast.makeText(MapsActivity.this, "Updated position successfully", Toast.LENGTH_SHORT).show();
                Log.i("ggegege", "Error getSpawnById: " + response);
                //GetAllSpawnsFromAPI();
            }

            @Override
            public void onFailure(Call<Spawn> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Obté la llista d'Spawns
     */
    private void GetAllSpawnsFromAPI() {

        try {
            Call<List<Spawn>> call = service.getAllSpawns("spawns");

            call.enqueue(new Callback<List<Spawn>>() {
                @Override
                public void onResponse(Call<List<Spawn>> call, Response<List<Spawn>> response) {
                    listaSpawn.clear();
                    listaSpawn.addAll(response.body());
                    ColocarMarkersSpawns();
                }

                @Override
                public void onFailure(Call<List<Spawn>> call, Throwable t) {
                    Log.i("ggegege", "Error getAllRaids: " + t.getMessage());
                }
            });
        } catch (Exception ex) {
            Log.i("ggegege", "Errorrrrrrr: " + ex.getMessage());
        }
    }

    /**
     * Obté el nom del Poké amb un GET de la foreing key del Spawn y el marca al mapa
     */
    public void ColocarMarkersSpawns() {

        Spawn aux = null;

        try {
            for (Spawn spawn : listaSpawn) {

                aux = spawn;

                Call<String> call = service.getPokeByID(spawn.getPokeId());
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        pokeName = response.body();
                        Log.i("ggegege", pokeName);

                        MarkerOptions marker = new MarkerOptions().
                                position(new LatLng(spawn.getLat(), spawn.getLng())).
                                title("\uD83D\uDC1B Spawn").
                                snippet(pokeName + " ID: " + spawn.getId())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .draggable(true);

                        mMap.addMarker(marker);

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        pokeName = "failure";
                        Log.i("ggegege", t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            Log.i("ggegege", "Error en colocar foreach markers spawn " + ex.getStackTrace()[0].getLineNumber()
                    +  " - aux: " + aux.toString());
        }
    }

    /**
     * Obté totes les Raids
     */
    private void GetAllRaidsFromAPI() {

        Call<List<Raid>> call = service.getAllRaids("raids");

        call.enqueue(new Callback<List<Raid>>() {
            @Override
            public void onResponse(Call<List<Raid>> call, Response<List<Raid>> response) {
                listaRaids.clear();
                listaRaids.addAll(response.body());
                Log.i("ggegege", "Response raids: " + listaRaids.size());
                ColocarMarkersRaids();
            }

            @Override
            public void onFailure(Call<List<Raid>> call, Throwable t) {
                Log.i("ggegege", "Error getAllRaids: " + t.getMessage());
            }
        });
    }

    /**
     * Obté el nom del Poké amb un GET de la foreing key del Raid y el marca al mapa
     */

    public void ColocarMarkersRaids() {

        for (Raid raid : listaRaids) {

            Call<String> call = service.getPokeByID(raid.getPokeId());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    pokeName = response.body();
                    Log.i("ggegege", pokeName);

                    MarkerOptions marker = new MarkerOptions().
                            position(new LatLng(raid.getLat(), raid.getLng())).
                            title("\uD83E\uDD5A Raid - " + raid.getNivel() + " \u2605").
                            snippet(pokeName + " ID: " + raid.getId())
                            .draggable(true);

                    switch (raid.getNivel()) {
                        case 3:
                            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                            break;
                        case 4:
                            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                            break;
                        case 5:
                            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            break;
                    }
                    mMap.addMarker(marker);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    pokeName = "failure";
                    Log.i("ggegege", t.getMessage());
                }
            });
        }
    }

    /**
     * Demana permissos d'ubicació (el primer cop crec que fa petar l'aplicació)
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                } else {
                }
                return;
            }
        }
    }

    /**
     * On result de la AddSpawnActivity, fa el POST
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                try {
                    Spawn spawn = new Spawn();
                    spawn.setPokeId(data.getIntExtra("pokeid", -1));
                    spawn.setLng(data.getFloatExtra("lng", -1));
                    spawn.setLat(data.getFloatExtra("lat", -1));

                    Call<Spawn> call = service.insertSpawn(spawn);
                    call.enqueue(new Callback<Spawn>() {
                        @Override
                        public void onResponse(Call<Spawn> call, Response<Spawn> response) {
                            Toast.makeText(MapsActivity.this, "Inserted successfully.", Toast.LENGTH_SHORT).show();
                            GetAllSpawnsFromAPI();
                        }

                        @Override
                        public void onFailure(Call<Spawn> call, Throwable t) {
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    });

                } catch (Exception ex) {
                    Log.i("ErrorAPI", "Sense webService petarà");
                }

            }
        }
    }
}
