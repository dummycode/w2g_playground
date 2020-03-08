package edu.gatech.w2gplayground.Activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import edu.gatech.w2gplayground.R;


/**
 * Activity for home screen
 */
public class HomeActivity extends AppCompatActivity {
    String[] picklists = { "Order 1", "Order 2" };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.activity_listview, picklists);

        ListView listView = findViewById(R.id.picklists);
        listView.setAdapter(adapter);
    }
}
