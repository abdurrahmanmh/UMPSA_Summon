package com.cb20034.umpsas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SummonListAdapter extends ArrayAdapter<Summon> {

    private static class ViewHolder {
        TextView plateNoTextView;
        TextView offenceTextView;
    }

    private Context context;

    public SummonListAdapter(@NonNull Context context, @NonNull List<Summon> summons) {
        super(context, 0, summons);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        ViewHolder holder;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item_summon, parent, false);

            holder = new ViewHolder();
            holder.plateNoTextView = listItemView.findViewById(R.id.plateNoTextView);
            holder.offenceTextView = listItemView.findViewById(R.id.offenceTextView);

            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }

        // Get the current summon
        Summon currentSummon = getItem(position);

        // Set the data to the views in the list item layout
        if (currentSummon != null) {
            holder.plateNoTextView.setText("Plate Number: " + currentSummon.getPlateNumber());
            holder.offenceTextView.setText("Offence: " + currentSummon.getOffence());
        }

        return listItemView;
    }
}
