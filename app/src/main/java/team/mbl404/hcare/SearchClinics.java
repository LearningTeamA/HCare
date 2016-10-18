package team.mbl404.hcare;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
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
    DBHelper db;
    boolean isFavorites = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setControls();
    }

    private void setControls(){
        setContentView(R.layout.main_container);
        Toolbar   toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (db==null) {
            db = new DBHelper(this);
            showAll();
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        //refreshes favorites if one was removed.
        if(isFavorites) showFavorites();
        else showAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);

        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                clinics = DBHelper.searchFor(query);
                setList();
                return true;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.viewFavorites:
                showFavorites();
                break;
            case R.id.viewAll:
                showAll();
                break;
        }
        return true;
    }

    //Return predictive results for display
    private void showAll(){
        isFavorites = false;
        clinics = db.getAllClinics();
        setList();
    }
    private void showFavorites(){
        isFavorites = true;
        clinics = db.getFavorites();
        setList();
    }
    private void setList(){
        list = (ListView) findViewById(R.id.clinicList);
        ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_list_item_1, clinics);
        list.setAdapter(ad);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(getApplicationContext(),ShowClinic.class);
                //depends on unique name, big weakness but is ok here.
                intent.putExtra("clinic", db.getClinic(list.getItemAtPosition(position).toString()));
                startActivity(intent);
            }
        });
    }
}
