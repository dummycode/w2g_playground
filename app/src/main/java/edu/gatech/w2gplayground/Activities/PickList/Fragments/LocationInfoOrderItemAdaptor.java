package edu.gatech.w2gplayground.Activities.PickList.Fragments;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.stream.Collectors;

import edu.gatech.w2gplayground.Models.Line;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Utilities.CustomToast;

public class LocationInfoOrderItemAdaptor extends ArrayAdapter<String> {

    private final Activity context;
    private final Line[] lines;

    public LocationInfoOrderItemAdaptor(Activity context, Line[] lines) {
        super(context, R.layout.location_info_order_item, Arrays.stream(lines)
                .map(Line::getId)
                .collect(Collectors.toList()));

        this.context = context;
        this.lines = lines;
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.location_info_order_item, null,true);

        TextView orderTitleText = rowView.findViewById(R.id.order_title);
        TextView orderCountText = rowView.findViewById(R.id.order_count);

        Line line = lines[position];

        orderTitleText.setText(
                String.format(
                        getContext().getString(R.string.activity_picklist__location_info__order_title),
                        position + 1
                )
        );

        orderCountText.setText(
                String.format(
                        getContext().getString(R.string.activity_picklist__location_info__order_quantity),
                        line.getQuantity()
                )
        );

        return rowView;
    };

    /**
     * Handle click on view
     */
    private void onClickListener(Line line) {
        CustomToast.showTopToast(this.context,"Selected line with id " + line.getItem());
    }
}
