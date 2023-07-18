package com.example.fixit.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fixit.Adapter.DoctorAd;
import com.example.fixit.Interface.OnMoveToDets;
import com.example.fixit.Model.DoctorModel;
import com.example.fixit.Patient.PharmacyActivity;
import com.example.fixit.R;
import com.example.fixit.databinding.ActivityDoctorViewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DoctorViewActivity extends AppCompatActivity implements OnMoveToDets {

    ActivityDoctorViewBinding activityDoctorViewBinding;
    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    public List<DoctorModel> doctorModelList;
    DoctorAd doctorAdapter;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityDoctorViewBinding = ActivityDoctorViewBinding.inflate(getLayoutInflater());
        setContentView(activityDoctorViewBinding.getRoot());

        initViews(activityDoctorViewBinding);

        initBottomNavView();

        firestore = FirebaseFirestore.getInstance();

        doctorAdapter.setOnMoveToDets(this);

        retrieveDoctors();
    }

    private void retrieveDoctors() {
        String desiredAccountType = "Admin";

        CollectionReference usersRef = firestore.collection("users");

        usersRef.whereEqualTo("accounttype", desiredAccountType)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        doctorModelList.clear(); // Clear the list before adding new data

                        for (QueryDocumentSnapshot userDocument : queryDocumentSnapshots) {
                            String userId = userDocument.getId();

                            CollectionReference doctorRef = usersRef.document(userId)
                                    .collection("Doctor");

                            doctorRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot querySnapshot) {
                                    for (QueryDocumentSnapshot doctorDocument : querySnapshot) {
                                        DoctorModel doctor = doctorDocument.toObject(DoctorModel.class);
                                        doctorModelList.add(doctor);
                                    }

                                    doctorAdapter.setDoctorList(doctorModelList); // Set the retrieved doctor list to the adapter
                                    doctorAdapter.notifyDataSetChanged(); // Update the RecyclerView


                                 //   Toast.makeText(getApplicationContext(), ""+doctorModelList.size(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error getting doctors: " + e.getMessage());
                    }
                });
    }

    private void initBottomNavView() {
        bottomNavigationView.setSelectedItemId(R.id.item_home);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(MenuItem item) {
                if (item.getItemId() == R.id.item_home) {
                    Intent x = new Intent(DoctorViewActivity.this, PatientMainActivity.class);
                    x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x);
                } else if (item.getItemId() == R.id.item_cart) {
                    Intent x6 = new Intent(DoctorViewActivity.this, OrdersActivity.class);
                    x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x6);
                } else {
                    Toast.makeText(getApplicationContext(), "Option Not available at the moment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViews(ActivityDoctorViewBinding activityDoctorViewBinding) {
        recyclerView = activityDoctorViewBinding.recyclerView0;
        doctorModelList = new ArrayList<>();
        doctorAdapter = new DoctorAd(getApplicationContext(), doctorModelList);
        recyclerView.setAdapter(doctorAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bottomNavigationView = activityDoctorViewBinding.bottomNavigationView;
    }

    @Override
    public void onMoveToDets(DoctorModel doctorModel) {
        // Handle the click event, for example, navigate to patient details screen
        Intent intent = new Intent(getApplicationContext(), PharmacyActivity.class);
        intent.putExtra("doctorModel", doctorModel);
        startActivity(intent);
    }
}
