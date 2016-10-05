package team.mbl404.hcare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

public class ShowClinic extends AppCompatActivity implements Serializable, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Clinic clinic;
    private GoogleMap map;

    private void setControls() {
        setContentView(R.layout.activity_show_clinic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        clinic = (Clinic) getIntent().getExtras().getSerializable("clinic");
        EditText header, phone, website, email;

        GoogleApiClient mApi = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mApi.connect();
        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        header  = (EditText) findViewById(R.id.header);
        phone   = (EditText) findViewById(R.id.phone);
        website = (EditText) findViewById(R.id.website);
        email   = (EditText) findViewById(R.id.email);

        header.setText(clinic.name+" - ("+clinic.address+" "+
                clinic.city+", "+clinic.state+" "+clinic.zip+")");
        phone.setText(clinic.phone);
        email.setText(clinic.email);
        website.setText(clinic.web);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.clinic, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addFav:
                break;
            case R.id.remFav:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setControls();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // your code goes here
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //your code goes here
    }

    @Override
    public void onConnectionSuspended(int cause) {
        //your code goes here
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng ll = new LatLng(clinic.lat, clinic.lon);
        map.addMarker(new MarkerOptions().position(ll));
        CameraPosition cameraPosition =
                new CameraPosition.Builder().target(ll).zoom(18).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.getUiSettings().setAllGesturesEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.getUiSettings().setZoomControlsEnabled(true);
    }


    public static class Clinic  implements Serializable {
        public String name, address, state, city, phone, email, web;
        public Double lat, lon;
        public int zip, id;
        public Clinic(int id, String name, String addr, String city, String state, int zip,
                      String phone, String email, String web, Double lat, Double lon){
            this.id = id;   this.name = name;   this.address = addr; this.city = city;
            this.zip = zip; this.phone = phone; this.web = web;      this.email = email;
            this.lat = lat; this.lon = lon;     this.state = state;
        }
    }
}
