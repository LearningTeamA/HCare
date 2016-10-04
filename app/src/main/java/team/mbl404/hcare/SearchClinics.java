package team.mbl404.hcare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchClinics extends AppCompatActivity{

    ArrayList<String> clinics = new ArrayList<>();
    ListView list;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setControls();
    }

    private void setControls(){
        setContentView(R.layout.main_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        list = (ListView) findViewById(R.id.clinicList);
        addClinics();
        ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_list_item_1, clinics);
        list.setAdapter(ad);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int entryID, long arg3) {
                Intent intent = new Intent(getApplicationContext(),ShowClinic.class);
                intent.putExtra("entry", entryID);
                startActivity(intent);
            }
        });
    }

    private void addClinics(){
        clinics.add("Clinic 1");
        clinics.add("Clinic 2");
        clinics.add("Clinic 3");
        clinics.add("Clinic 4");
        clinics.add("Clinic 5");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.viewFavorites:
                break;
            case R.id.viewAll:
                break;
        }
        return true;
    }
}
