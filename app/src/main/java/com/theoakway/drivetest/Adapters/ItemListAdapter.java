package com.theoakway.drivetest.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.theoakway.drivetest.ModalClasses.ItemListModal;
import com.theoakway.drivetest.R;

import java.util.List;

public class ItemListAdapter extends ArrayAdapter<ItemListModal> {

    public ItemListAdapter(@NonNull Context context, int resource, @NonNull List<ItemListModal> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.single_item_list, parent, false);
        }

        ItemListModal modal = getItem(position);
        TextView name = convertView.findViewById(R.id.singleItemName);
        TextView barcode = convertView.findViewById(R.id.singleItemBarcode);
        TextView cost = convertView.findViewById(R.id.singleCostPrice);
        TextView retail = convertView.findViewById(R.id.singleRetailPrice);

        name.setText(modal.getName());
        barcode.setText(modal.getBarcode());
        cost.setText(modal.getCostPrice());
        retail.setText(modal.getRetailPrice());

        return convertView;
    }
}
