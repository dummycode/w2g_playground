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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import edu.gatech.w2gplayground.Activities.PickList.PickListActivity;
import edu.gatech.w2gplayground.Models.Generators.LineGenerator;
import edu.gatech.w2gplayground.Models.Generators.OrderGenerator;
import edu.gatech.w2gplayground.Models.Line;
import edu.gatech.w2gplayground.Models.Order;
import edu.gatech.w2gplayground.Models.PickList;
import edu.gatech.w2gplayground.R;

/**
 * A fragment to show the summary
 */
public class SummaryFragment extends Fragment {
    private PickListActivity activity;

    TextView title;

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

        return inflater.inflate(R.layout.fragment_picklist_summary, container, false);
    }

    /**
     * Once our view is created, we will show the summary page
     *
     * @param view - The new view
     * @param savedInstanceState - required argument that we ignore
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        activity = (PickListActivity) getActivity();

        if (activity == null) {
            throw new RuntimeException("Activity cannot be null");
        }

        PickList pickList = (PickList) getArguments().getSerializable("pickList");

        title = activity.findViewById(R.id.summary_title);
        title.setText(String.format(getString(R.string.activity_picklist__summary__title), pickList.getId()));

        Order[] orders = { OrderGenerator.withLines(1), OrderGenerator.withLines(2) };
        OrderItemAdaptor adapter = new OrderItemAdaptor(activity, orders);

        ListView listView = activity.findViewById(R.id.orders);
        listView.setAdapter(adapter);

    }

    public class KeyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Ignore if this fragment is not on the screen
            if (!(getFragmentManager().findFragmentById(R.id.fragment_container) instanceof SummaryFragment)) {
                return;
            }

            int keyCode = intent.getIntExtra("KEY_CODE", 0);

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                ((PickListActivity) getActivity()).summaryDone();
            }
        }
    }
}


