package edu.gatech.w2gplayground.Activities.PickList.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import edu.gatech.w2gplayground.Activities.PickList.PickListActivity;
import edu.gatech.w2gplayground.Models.Generators.LineGenerator;
import edu.gatech.w2gplayground.Models.Line;
import edu.gatech.w2gplayground.Models.Location;
import edu.gatech.w2gplayground.R;

/**
 * A fragment to show the item information for a given location
 */
public class LocationInfoFragment extends Fragment {

    private PickListActivity activity;
    private Location location;

    private TextView title;
    private TextView itemName;

    String currItemName;

    /**
     * Inflate the correct layout upon creation
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState  If non-null, this fragment is being re-constructed from a previous saved state as given here.

     * @return - Returns the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Register receiver for key down event
        BroadcastReceiver receiver = new KeyBroadcastReceiver();
        getContext().registerReceiver(receiver, new IntentFilter(PickListActivity.keyDownAction));

        return inflater.inflate(R.layout.fragment_location_info, container, false);
    }

    /**
     * Once our view is created, we will show the image with the scan result
     *
     * @param view - The new view
     * @param savedInstanceState - required argument that we ignore
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = (PickListActivity) getActivity();

        if (activity == null) {
            throw new RuntimeException("Activity cannot be null");
        }

        Bundle args = getArguments();
        if (args != null) {
            location = (Location) args.getSerializable("location");
            currItemName = args.getString("itemName");
        }

        title = activity.findViewById(R.id.location_info_title);
        title.setText(String.format(getString(R.string.activity_picklist__location_info__title), location.getName()));

        itemName = activity.findViewById(R.id.item_id);
        itemName.setText(String.format(getString(R.string.activity_picklist__location_info__item_name), currItemName));


        Line[] lines = { LineGenerator.line(), LineGenerator.withQuantity(2) };
        LocationInfoOrderItemAdaptor adapter = new LocationInfoOrderItemAdaptor(activity, lines);

        ListView listView = activity.findViewById(R.id.orders);
        listView.setAdapter(adapter);

        // Focus on list view
//        listView.requestFocus();

        listView.setOnItemClickListener((parent, _view, position, id) -> handleItemClick(position));
    }

    public class KeyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Ignore if this fragment is not on the screen
            if (!(getFragmentManager().findFragmentById(R.id.fragment_container) instanceof LocationInfoFragment)) {
                return;
            }

            int keyCode = intent.getIntExtra("KEY_CODE", 0);

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                activity.locationInfoDone();
            }
        }
    }

    /**
     * Handles row item being clicked
     *
     * @param position that was clicked
     */
    private void handleItemClick(int position) {
        // Do nothing
    }
}



