package com.example.fixit.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fixit.Model.DoctorModel;
import com.example.fixit.R;
import com.example.fixit.databinding.ActivityPharmacyBinding;

public class PharmacyActivity extends AppCompatActivity {
    DoctorModel doctorModel;
    ActivityPharmacyBinding activityPharmacyBinding;
    String phoneNumber, name, location;
    TextView txtlocation, txtname;

    Button buttoncall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityPharmacyBinding = ActivityPharmacyBinding.inflate(getLayoutInflater());
        setContentView(activityPharmacyBinding.getRoot());

        initViews(activityPharmacyBinding);

        // Retrieve the intent that started the activity
        Intent intent = getIntent();

        // Check if the intent contains the extra data
        if (intent.hasExtra("doctorModel")) {
            // Extract the DoctorModel object from the intent's extra data
            doctorModel = intent.getParcelableExtra("doctorModel");
            phoneNumber = doctorModel.getContact();
            name = doctorModel.getName();
            location = doctorModel.getLocation();
        }

        buttoncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phoneNumber))));
              //  Toast.makeText(getApplicationContext(), "" + phoneNumber, Toast.LENGTH_SHORT).show();
            }
        });

        txtlocation.setText(location);
        txtname.setText(name);
    }

    private void initViews(ActivityPharmacyBinding activityPharmacyBinding) {
        buttoncall = activityPharmacyBinding.buttonTalkToPhysician;
        txtlocation = activityPharmacyBinding.textLocation;
        txtname = activityPharmacyBinding.textHello;
    }
}
