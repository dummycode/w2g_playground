package edu.gatech.w2gplayground.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.gatech.w2gplayground.Activities.Interfaces.VoiceCommandActivity;
import edu.gatech.w2gplayground.Activities.PickList.PickListActivity;
import edu.gatech.w2gplayground.Fragments.Home.PickListItemAdaptor;
import edu.gatech.w2gplayground.Models.Generators.PickListGenerator;
import edu.gatech.w2gplayground.Models.PickList;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Utilities.CustomToast;
import edu.gatech.w2gplayground.Voice.HomeVoiceCommandReceiver;


/**
 * Activity for home screen
 */
public class HomeActivity extends AppCompatActivity implements VoiceCommandActivity {

    // UI components
    private ImageView listeningStatus;

    List<PickList> pickLists = new ArrayList<>();
    HomeVoiceCommandReceiver voiceCommandReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        pickLists.add(PickListGenerator.pickList());
        pickLists.add(PickListGenerator.pickList());
        pickLists.add(PickListGenerator.pickList());

        PickListItemAdaptor adapter = new PickListItemAdaptor(this, pickLists.toArray(new PickList[0]));

        ListView listView = findViewById(R.id.picklists);
        listView.setAdapter(adapter);

        // Focus on list view
        listView.requestFocus();

        listView.setOnItemClickListener((parent, view, position, id) -> handleItemClick(position));

        // Not listening
        listeningStatus = findViewById(R.id.listening);
        listeningStatus.setVisibility(View.GONE);

        try {
            // Create the voice command receiver class
            voiceCommandReceiver = new HomeVoiceCommandReceiver(this);
        } catch (RuntimeException re) {
            Log.d(LOG_TAG, re.getMessage());
            CustomToast.showTopToast(this, getString(R.string.only_on_mseries));
        }
    }

    /**
     * Handles row item being clicked
     *
     * @param position that was clicked
     */
    public void handleItemClick(int position) {
        Intent myIntent = new Intent(HomeActivity.this, PickListActivity.class);
        myIntent.putExtra("pickList", pickLists.get(position));

        startActivity(myIntent);
    }

    /**
     * Handles a refresh command
     */
    public void refresh() {
        pickLists = new ArrayList<>();
        pickLists.add(PickListGenerator.pickList());

        PickListItemAdaptor adapter = new PickListItemAdaptor(this, pickLists.toArray(new PickList[0]));

        ListView listView = findViewById(R.id.picklists);
        listView.setAdapter(adapter);
    }

    /**
     * Update the text from "Listening..." to "Not listening" based on the state
     */
    private void updateListeningStatus(boolean isRecognizerActive) {
        if (isRecognizerActive) {
            listeningStatus.setVisibility(View.VISIBLE);
        } else {
            listeningStatus.setVisibility(View.GONE);
        }
    }

    /**
     * A callback for the SDK to notify us if the recognizer starts or stop listening
     *
     * @param isRecognizerActive boolean - true when listening
     */
    public void RecognizerChangeCallback(final boolean isRecognizerActive) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateListeningStatus(isRecognizerActive);
            }
        });
    }

    /**
     * Utility to get the name of the current method for logging
     *
     * @return String name of the current method
     */
    public String getMethodName() {
        return LOG_TAG + ":" + this.getClass().getSimpleName() + "." + new Throwable().getStackTrace()[1].getMethodName();
    }
}
