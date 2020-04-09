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

import edu.gatech.w2gplayground.Activities.LoginActivity;
import edu.gatech.w2gplayground.Activities.PickList.Fragments.BinConfigurationFragment;
import edu.gatech.w2gplayground.Activities.PickList.Fragments.NextLocationFragment;
import edu.gatech.w2gplayground.Activities.PickList.Fragments.ScanLocationFragment;
import edu.gatech.w2gplayground.Models.Order;
import edu.gatech.w2gplayground.Models.PickList;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Utilities.CustomToast;


/**
 * Activity for a pick list
 */
public class PickListActivity extends AppCompatActivity {
    public static final String LOG_TAG = PickListActivity.class.getSimpleName();
    public static final String keyDownAction = "KEY_DOWN";

    TextView instructions, secondaryInstructions;

    PickList pickList;
    Order[] orders;

    private ImageView listeningStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picklist);

        instructions = findViewById(R.id.instructions);
        secondaryInstructions = findViewById(R.id.secondary_instructions);

        // Not listening
        listeningStatus = findViewById(R.id.listening);
        listeningStatus.setVisibility(View.GONE);

        // Handle passed in arguments
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            this.pickList = (PickList) getIntent().getSerializableExtra("pickList");
        }

        CustomToast.showTopToast(this, pickList.getId());

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
        // Move to scan location
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainer.getId(), new ScanLocationFragment())
                .addToBackStack(null)
                .commit();

        instructions.setText(R.string.activity_picklist__next_location_instructions);
        secondaryInstructions.setText(R.string.activity_picklist__next_location_instructions__secondary);

        CustomToast.showTopToast(this, "At location!");
    }

    public void scanLocationDone() {
        CustomToast.showTopToast(this, "Scanned Location");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(keyDownAction);
        intent.putExtra("KEY_CODE", keyCode);

        this.sendBroadcast(intent);

        Log.d(LOG_TAG, "Received key down " + keyCode);

        return super.onKeyDown(keyCode, event);
    }
}
