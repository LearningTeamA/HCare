package team.mbl404.hcare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

//This will shit the splash screen until the app is done loading and then switch intents.
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, SearchClinics.class));
        finish();
    }
}
