package edu.gatech.w2gplayground.Fragments.Home;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import edu.gatech.w2gplayground.Models.PickList;
import edu.gatech.w2gplayground.R;

public class PickListItemAdaptor extends ArrayAdapter<String> {

    private final Activity context;
    private final PickList[] pickLists;

    public PickListItemAdaptor(Activity context, PickList[] pickLists) {
        super(context, R.layout.activity_listview, Arrays.stream(pickLists)
                .map(PickList::getId)
                .collect(Collectors.toList()));

        this.context = context;
        this.pickLists = pickLists;
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.activity_listview, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);

        PickList pickList = pickLists[position];

        titleText.setText(pickList.getId());

        return rowView;

    };
}
