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
public class ReceiptListAdapter extends ArrayAdapter<Receipt> {

    private static class ViewHolder {
        TextView receiptIDtextView, receiptDetail;
    }
    private Context context;
    public ReceiptListAdapter(@NonNull Context context, @NonNull List<Receipt> receipts) {
        super(context, 0, receipts);
        this.context = context;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        ViewHolder holder;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item_receipt, parent, false);

            holder = new ViewHolder();
            holder.receiptIDtextView = listItemView.findViewById(R.id.receiptIDtextView);
            holder.receiptDetail = listItemView.findViewById(R.id.receiptDetail);

            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }

        // Get the current summon
        Receipt currentReceipt = getItem(position);

        // Set the data to the views in the list item layout
        if (currentReceipt != null) {
            holder.receiptIDtextView.setText("Plate Number: " + currentReceipt.getReceiptId());
            holder.receiptDetail.setText("Offence: " + currentReceipt.getOffence());
        }

        return listItemView;
    }
}
