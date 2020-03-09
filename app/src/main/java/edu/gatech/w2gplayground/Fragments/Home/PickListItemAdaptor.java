package edu.gatech.w2gplayground.Fragments.Home;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.stream.Collectors;

import edu.gatech.w2gplayground.Models.PickList;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Utilities.CustomToast;

public class PickListItemAdaptor extends ArrayAdapter<String> {

    private final Activity context;
    private final PickList[] pickLists;

    public PickListItemAdaptor(Activity context, PickList[] pickLists) {
        super(context, R.layout.fragment_picklist_item, Arrays.stream(pickLists)
                .map(PickList::getId)
                .collect(Collectors.toList()));

        this.context = context;
        this.pickLists = pickLists;
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.fragment_picklist_item, null,true);

        TextView titleText = rowView.findViewById(R.id.title);
        TextView orderCountText = rowView.findViewById(R.id.order_count);

        PickList pickList = pickLists[position];

        titleText.setText(pickList.getId());
        orderCountText.setText(
            String.format(
                context.getString(R.string.activity_home__order_count),
                pickList.getOrderCount()
            )
        );

        return rowView;
    };

    /**
     * Handle click on view
     */
    private void onClickListener(PickList pickList) {
        CustomToast.showTopToast(this.context,"Selected picklist with id " + pickList.getId());
    }
}
