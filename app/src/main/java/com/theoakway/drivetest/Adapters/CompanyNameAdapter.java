package com.theoakway.drivetest.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.theoakway.drivetest.ModalClasses.CompanyModal;
import com.theoakway.drivetest.R;

import java.util.List;

public class CompanyNameAdapter extends ArrayAdapter<CompanyModal> {


    public CompanyNameAdapter(@NonNull Context context, int resource, @NonNull List<CompanyModal> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.single_company_name, parent, false);
        }

        final CompanyModal gymListModal = getItem(position);
        TextView name = convertView.findViewById(R.id.singleCompanyName);
        name.setText(gymListModal.getCompanyName());

        return convertView;
    }
}
