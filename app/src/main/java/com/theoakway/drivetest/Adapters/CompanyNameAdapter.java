package com.theoakway.drivetest.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.theoakway.drivetest.ModalClasses.CompanyModal;

import java.util.List;

public class CompanyNameAdapter extends ArrayAdapter<CompanyModal> {


    public CompanyNameAdapter(@NonNull Context context, int resource, @NonNull List<CompanyModal> objects) {
        super(context, resource, objects);
    }


}
