package edu.gatech.w2gplayground.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import edu.gatech.w2gplayground.Activities.PickList.PickListActivity;
import edu.gatech.w2gplayground.Fragments.Home.PickListItemAdaptor;
import edu.gatech.w2gplayground.Models.Generators.PickListGenerator;
import edu.gatech.w2gplayground.Models.PickList;
import edu.gatech.w2gplayground.R;


/**
 * Activity for home screen
 */
public class HomeActivity extends AppCompatActivity {
    PickList[] pickLists = {
            PickListGenerator.pickList(),
            PickListGenerator.pickList(),
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        PickListItemAdaptor adapter = new PickListItemAdaptor(this, pickLists);

        ListView listView = findViewById(R.id.picklists);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> handleItemClick(position));
    }

    /**
     * Handles row item being clicked
     *
     * @param position that was clicked
     */
    public void handleItemClick(int position) {
        Intent myIntent = new Intent(HomeActivity.this, PickListActivity.class);
        myIntent.putExtra("pickList", pickLists[position]);

        startActivity(myIntent);
    }
}
