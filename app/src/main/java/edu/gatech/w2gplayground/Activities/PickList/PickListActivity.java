package edu.gatech.w2gplayground.Activities.PickList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.gatech.w2gplayground.Activities.Interfaces.VoiceCommandActivity;
import edu.gatech.w2gplayground.Activities.PickList.Fragments.BinConfigurationFragment;
import edu.gatech.w2gplayground.Activities.PickList.Fragments.LocationInfoFragment;
import edu.gatech.w2gplayground.Activities.PickList.Fragments.NextLocationFragment;
import edu.gatech.w2gplayground.Activities.PickList.Fragments.ScanItemsFragment;
import edu.gatech.w2gplayground.Activities.PickList.Fragments.ScanLocationFragment;
import edu.gatech.w2gplayground.Models.Generators.LineGenerator;
import edu.gatech.w2gplayground.Models.Line;
import edu.gatech.w2gplayground.Models.Location;
import edu.gatech.w2gplayground.Models.Order;
import edu.gatech.w2gplayground.Models.PickList;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Utilities.CustomToast;
import edu.gatech.w2gplayground.Voice.PickListVoiceCommandReceiver;


/**
 * Activity for a pick list
 */
public class PickListActivity extends AppCompatActivity implements VoiceCommandActivity {
    public static final String LOG_TAG = PickListActivity.class.getSimpleName();
    public static final String keyDownAction = "KEY_DOWN";

    public TextView instructions, secondaryInstructions;
    private ImageView listeningStatus;

    PickList pickList;
    Order[] orders;

    Location currLocation;
    public String currItemName;
    public int currQuantity;

    PickListVoiceCommandReceiver voiceCommandReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picklist);

        instructions = findViewById(R.id.instructions);
        secondaryInstructions = findViewById(R.id.secondary_instructions);


        // Handle passed in arguments
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            this.pickList = (PickList) getIntent().getSerializableExtra("pickList");
        }

        CustomToast.showTopToast(this, pickList.getId());

        // Not listening
        listeningStatus = findViewById(R.id.listening);
        listeningStatus.setVisibility(View.GONE);

        try {
            // Create the voice command receiver class
            voiceCommandReceiver = new PickListVoiceCommandReceiver(this);
        } catch (RuntimeException re) {
            Log.d(LOG_TAG, re.getMessage());
            CustomToast.showTopToast(this, getString(R.string.only_on_mseries));
        }

        voiceCommandReceiver.binConfig();

        // Start out on bin configuration
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainer.getId(), new BinConfigurationFragment())
                .addToBackStack(null)
                .commit();

        instructions.setText(R.string.activity_picklist__bin_instructions);
        secondaryInstructions.setText(R.string.activity_picklist__bin_instructions__secondary);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(keyDownAction);
        intent.putExtra("KEY_CODE", keyCode);

        this.sendBroadcast(intent);

        Log.d(LOG_TAG, "Received key down " + keyCode);

        return super.onKeyDown(keyCode, event);
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

    /*
     * Handlers for when different stages of the pick process are completed
     */

    /**
     * Handler for when bin configuration is complete
     */
    public void binConfiguationDone() {
        CustomToast.showTopToast(this, "Bins configured!");

        // Move on to next location
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainer.getId(), new NextLocationFragment())
                .addToBackStack(null)
                .commit();

        instructions.setText(R.string.activity_picklist__next_location_instructions);
        secondaryInstructions.setText(R.string.activity_picklist__next_location_instructions__secondary);
    }

    /**
     * Handler for when next location fragment is done
     */
    public void nextLocationDone() {
        CustomToast.showTopToast(this, "At location");

        // Move to scan location
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainer.getId(), new ScanLocationFragment())
                .addToBackStack(null)
                .commit();

        instructions.setText(R.string.activity_picklist__scan_location_instructions);
        secondaryInstructions.setText(R.string.activity_picklist__scan_location_instructions__secondary);
    }

    /**
     * Handler for when a location has been scanned
     */
    public void scanLocationDone() {
        CustomToast.showTopToast(this, "Scanned Location");

        // Move to location info
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainer.getId(), new LocationInfoFragment())
                .addToBackStack(null)
                .commit();

        // Set info for current location
        Line[] currLines = { LineGenerator.line(), LineGenerator.line() };
        currQuantity = 0;
        for (Line line: currLines) {
            currQuantity += line.getQuantity();
        }
        currItemName = currLines[0].getItem().getName();

        instructions.setText(R.string.activity_picklist__location_info_instructions);
        secondaryInstructions.setText(R.string.activity_picklist__location_info_instructions__secondary);
    }

    /**
     * Handler for when item information is done
     */
    public void locationInfoDone() {
        CustomToast.showTopToast(this, "Location info done");

        // Move to scan items
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainer.getId(), new ScanItemsFragment())
                .addToBackStack(null)
                .commit();

        instructions.setText(String.format(getString(R.string.activity_picklist__scan_items_instructions), currQuantity, currItemName));
        secondaryInstructions.setText(R.string.activity_picklist__scan_items_instructions__secondary);
    }

    /**
     * Handler for when items have been scanned
     */
    public void scanItemsDone() {
        CustomToast.showTopToast(this, "Items have been scanned!");

        // If done, go to results screen

        // Else, go to next location
    }

    /*
     * Helper methods for different fragments
     */

    /**
     * Bin configuration help
     */
    public void binConfigurationHelp() {
        CustomToast.showTopToast(this, "Bin configuration help");
    }

    /*
     * Next location help
     */
    public void nextLocationHelp() {
        CustomToast.showTopToast(this, "Next location help");
    }
}
