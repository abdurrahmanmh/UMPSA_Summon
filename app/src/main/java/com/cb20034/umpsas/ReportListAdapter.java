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
public class ReportListAdapter extends ArrayAdapter<Report>{
    private static class ViewHolder {
        TextView reportIDtextView;
        TextView reportDetail;
    }

    private Context context;

    public ReportListAdapter(@NonNull Context context, @NonNull List<Report> reports){
        super(context, 0, reports);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        ViewHolder holder;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item_report, parent, false);

            holder = new ViewHolder();
            holder.reportIDtextView = listItemView.findViewById(R.id.reportIDtextView);
            holder.reportDetail = listItemView.findViewById(R.id.reportDetail);

            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }


        Report currentReport = getItem(position);

        // Set the data to the views in the list item layout
        if (currentReport != null) {
            holder.reportIDtextView.setText("Report ID : " + currentReport.getReportId());
            holder.reportDetail.setText("Report Detail " + currentReport.getReportDetail());
        }

        return listItemView;
    }

}
