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

        return listItemView;
    }
}