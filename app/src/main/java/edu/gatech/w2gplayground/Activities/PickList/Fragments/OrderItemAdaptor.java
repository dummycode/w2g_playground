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
import edu.gatech.w2gplayground.Models.Order;
import edu.gatech.w2gplayground.R;
import edu.gatech.w2gplayground.Utilities.CustomToast;

public class OrderItemAdaptor extends ArrayAdapter<String> {

    private final Activity context;
    private final Order[] orders;

    public OrderItemAdaptor(Activity context, Order[] orders) {
        super(context, R.layout.picklist_summary_item, Arrays.stream(orders)
                .map(Order::getId)
                .collect(Collectors.toList()));

        this.context = context;
        this.orders = orders;
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.picklist_summary_item, null,true);

        TextView orderIdText = rowView.findViewById(R.id.order_id);
        TextView itemCountText = rowView.findViewById(R.id.item_count);

        Order order = orders[position];

        orderIdText.setText(
                String.format(
                        getContext().getString(R.string.activity_picklist__summary__order_id),
                        position + 1
                )
        );

        itemCountText.setText(
                String.format(
                        getContext().getString(R.string.activity_picklist__summary__item_quantity),
                        order.getQuantity()
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
