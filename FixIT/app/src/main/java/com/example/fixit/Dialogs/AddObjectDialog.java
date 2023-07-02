package com.example.fixit.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.fixit.Admin.AddDoctorActivity;
import com.example.fixit.Admin.AddMedicineActivity;
import com.example.fixit.R;


public class AddObjectDialog extends DialogFragment {

    private RelativeLayout doctor, medicine;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.add_object_dialog, null);
        doctor = view.findViewById(R.id.doctorRelLayout);
        medicine = view.findViewById(R.id.MedicineRelLayout);

        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = new Intent(getActivity(), AddDoctorActivity.class);
                startActivity(x);
            }
        });

        medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x1 = new Intent(getActivity(), AddMedicineActivity.class);
                startActivity(x1);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Add Object")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setView(view);

        return builder.create();
    }
}
