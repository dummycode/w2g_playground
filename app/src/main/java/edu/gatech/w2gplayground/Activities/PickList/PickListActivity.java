package edu.gatech.w2gplayground.Activities.PickList;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainer;

import edu.gatech.w2gplayground.Activities.PickList.Fragments.BinConfigurationFragment;
import edu.gatech.w2gplayground.Models.Order;
import edu.gatech.w2gplayground.Models.PickList;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Utilities.CustomToast;


/**
 * Activity for home screen
 */
public class PickListActivity extends AppCompatActivity {
    PickList pickList;
    Order[] orders;

    private ImageView listeningStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picklist);

        // Not listening
        listeningStatus = findViewById(R.id.listening);
        listeningStatus.setVisibility(View.GONE);

        // Handle passed in arguments
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            this.pickList = (PickList) getIntent().getSerializableExtra("pickList");
        }

        CustomToast.showTopToast(this, pickList.getId());

        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainer.getId(), new BinConfigurationFragment())
                .addToBackStack(null)
                .commit();
    }
}
