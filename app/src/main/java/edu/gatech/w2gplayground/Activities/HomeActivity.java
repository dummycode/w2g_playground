package edu.gatech.w2gplayground.Activities;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;

import edu.gatech.w2gplayground.Fragments.Home.PickListItemAdaptor;
import edu.gatech.w2gplayground.Models.PickList;
import edu.gatech.w2gplayground.R;


/**
 * Activity for home screen
 */
public class HomeActivity extends AppCompatActivity {
    PickList[] pickLists = {
            new PickList("id1", new LinkedList<>()),
            new PickList("id2", new LinkedList<>()),
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        PickListItemAdaptor adapter = new PickListItemAdaptor(this, pickLists);

        ListView listView = findViewById(R.id.picklists);
        listView.setAdapter(adapter);
    }
}
