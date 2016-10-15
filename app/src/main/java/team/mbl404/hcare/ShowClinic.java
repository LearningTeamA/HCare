package team.mbl404.hcare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
    private ImageView favoriteSrc;

    //Sets all GUI controls and content
    private void setControls() {
        setContentView(R.layout.activity_show_clinic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        clinic = (Clinic) getIntent().getExtras().getSerializable("clinic");
        EditText header;
        ImageView website, email, phone, contact;

        //Calls Api client for map
        GoogleApiClient mApi = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mApi.connect();
        //Creates a fragment now that we are connected to a map
        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        //Claims text field handles
        header      = (EditText) findViewById(R.id.header);
        phone       = (ImageView) findViewById(R.id.phone);
        website     = (ImageView) findViewById(R.id.website);
        email       = (ImageView) findViewById(R.id.email);
        contact     = (ImageView) findViewById(R.id.contact);
        favoriteSrc = (ImageView) findViewById(R.id.favoriteStar);

        String headText = clinic.name+" - ("+clinic.address+" "+
                clinic.city+", "+clinic.state+" "+clinic.zip+")";
        //Sets content through direct reference to the object's static values
        header.setText(headText);

        favoriteCheck();

        //Listeners declaration
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Copies the number to the dialer
                Intent link=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + clinic.phone));
                startActivity(link);

            }
        });
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Opens a url
                Intent link=new Intent(Intent.ACTION_VIEW, Uri.parse(clinic.web));
                startActivity(link);
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Opens email application
                Intent link=new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto",clinic.email, null));
                link.putExtra(Intent.EXTRA_SUBJECT, clinic.name);
                startActivity(Intent.createChooser(link,"Send email"));
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Opens the contacts add activity
                Intent link=new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                //Adds various details
                link.putExtra(ContactsContract.Intents.Insert.NAME,clinic.name);
                link.putExtra(ContactsContract.Intents.Insert.PHONE,clinic.phone);
                link.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);
                link.putExtra(ContactsContract.Intents.Insert.EMAIL,clinic.email);
                link.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);
                link.putExtra(ContactsContract.Intents.Insert.POSTAL,
                        clinic.address+" "+clinic.city+", "+clinic.state+" "+clinic.zip);
                link.putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);

                startActivity(link);
            }
        });
        favoriteSrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ternary statement to flip the local clinic flag since we don't really have
                //read access to the database in this activity.
                clinic.favorite = (clinic.favorite==1)?0:1;
                DBHelper.toggleFavorite(clinic.id, clinic.favorite);
                favoriteCheck();
            }
        });
    }

    //Changes the icon accordingly.
    private void favoriteCheck() {
        if (clinic.favorite==1)
            favoriteSrc.setImageDrawable(getDrawable(android.R.drawable.star_big_on));
        else if (clinic.favorite==0)
            favoriteSrc.setImageDrawable(getDrawable(android.R.drawable.star_big_off));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setControls();
    }


    @Override
    //Called after successful onConnect()
    public void onMapReady(GoogleMap googleMap) {
        LatLng ll = new LatLng(clinic.lat, clinic.lon);
        googleMap.addMarker(new MarkerOptions().position(ll));
        //Higher values for zoom are a closer-to-street zoom. Lower zooms out.
        CameraPosition cameraPosition =
                new CameraPosition.Builder().target(ll).zoom(18).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //Custom object constructor to store Clinic data.
    public static class Clinic  implements Serializable {
        public String name, address, state, city, phone, email, web;
        public Double lat, lon;
        public int zip, id, favorite;
        public Clinic(int id, String name, String addr, String city, String state, int zip,
                      String phone, String email, String web, Double lat, Double lon, int favorite){
            this.id = id;   this.name = name;   this.address = addr; this.city = city;
            this.zip = zip; this.phone = phone; this.web = web;      this.email = email;
            this.lat = lat; this.lon = lon;     this.state = state;  this.favorite=favorite;
        }
    }
}
