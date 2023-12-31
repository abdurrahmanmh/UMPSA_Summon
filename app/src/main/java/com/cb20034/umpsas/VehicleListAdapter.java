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

public class VehicleListAdapter extends ArrayAdapter<Vehicle> {

    private static class ViewHolder {
        TextView plateNoTV;
        TextView vehicleBrandTV;
    }

    private Context context;

    public VehicleListAdapter(@NonNull Context context, @NonNull List<Vehicle> vehicles) {
        super(context, 0, vehicles);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        ViewHolder holder;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.vehicle_list_item, parent, false);

            holder = new ViewHolder();
            holder.plateNoTV = listItemView.findViewById(R.id.plateNoTV);
            holder.vehicleBrandTV = listItemView.findViewById(R.id.vehicleBrandTV);

            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }

        // Get the current vehicle
        Vehicle currentVehicle = getItem(position);

        // Set the data to the views in the list item layout
        if (currentVehicle != null) {
            holder.plateNoTV.setText(currentVehicle.getPlateNo());
            String vehicleBrandText = currentVehicle.getBrand() + " " + currentVehicle.getModel();
            holder.vehicleBrandTV.setText(vehicleBrandText);
        }

        // Set background color based on position
        int backgroundColor = (position % 2 == 0) ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.colorPrimary);
        listItemView.setBackgroundColor(backgroundColor);

        // Set text color based on position
        int textColor = (position % 2 == 0) ? context.getResources().getColor(android.R.color.white) : context.getResources().getColor(android.R.color.black);
        holder.plateNoTV.setTextColor(textColor);
        holder.vehicleBrandTV.setTextColor(textColor);

        return listItemView;
    }
}
