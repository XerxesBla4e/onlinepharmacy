package com.example.fixit.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fixit.Adapter.DoctorAdapter;
import com.example.fixit.Authentication.LoginActivity;
import com.example.fixit.Model.DoctorModel;
import com.example.fixit.Model.Medicine;
import com.example.fixit.Model.MedicineModel;
import com.example.fixit.Patient.PatientMainActivity;
import com.example.fixit.R;
import com.example.fixit.databinding.ActivityViewDoctorsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewDoctorsActivity extends AppCompatActivity {

    ActivityViewDoctorsBinding activityViewDoctorsBinding;
    RecyclerView recyclerView;
    DoctorAdapter doctorAdapter;
    List<DoctorModel> doctorModelList;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid1;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityViewDoctorsBinding = ActivityViewDoctorsBinding.inflate(getLayoutInflater());
        setContentView(activityViewDoctorsBinding.getRoot());

        initViews(activityViewDoctorsBinding);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid1 = firebaseUser.getUid();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(doctorAdapter);

        initBottomNavView();

        DocumentReference userRef = firestore.collection("users").document(uid1);
        userRef.collection("Doctor").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Retrieve the data from the document snapshot
                            String name = documentSnapshot.getString("name");
                            String location = documentSnapshot.getString("location");
                            String specialization = documentSnapshot.getString("specialization");
                            String contact = documentSnapshot.getString("contact");
                            String did = documentSnapshot.getString("did");
                            String timestamp = documentSnapshot.getString("timestamp");
                            String uid = documentSnapshot.getString("uid");
                            String image = documentSnapshot.getString("image");

                            // Create a DoctorModel object and add it to the list
                            DoctorModel doctorModel = new DoctorModel(name, location, specialization, contact, did, timestamp, uid, image);
                            doctorModelList.add(doctorModel);
                        }

                        // Update the adapter with the retrieved doctor list
                        doctorAdapter.setDoctorList(doctorModelList);
                        if (doctorModelList.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "No doctors available", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure in retrieving doctor information from Firestore
                        Toast.makeText(getApplicationContext(), "Failed to retrieve Doctor Information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    deleteDoctor(viewHolder);
                    //Toast.makeText(PatientMainActivity.this, "Cart Item deleted", Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        }).attachToRecyclerView(activityViewDoctorsBinding.recyclerView);
    }

    private void deleteDoctor(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            DoctorModel doctorModel = doctorModelList.get(position);

            DocumentReference medicineRef = firestore.collection("users")
                    .document(uid1)
                    .collection("Doctor")
                    .document(doctorModel.getDid());

            medicineRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ViewDoctorsActivity.this, "Doctor deleted", Toast.LENGTH_SHORT).show();
                            doctorModelList.remove(position);
                            doctorAdapter.notifyItemRemoved(position);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ViewDoctorsActivity.this, "Failed to delete doctor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void initBottomNavView() {
        bottomNavigationView.setSelectedItemId(R.id.item_doctors);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(MenuItem item) {
                if (item.getItemId() == R.id.item_home) {
                    Intent x6 = new Intent(ViewDoctorsActivity.this, AdminMainActivity.class);
                    x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x6);
                } else if (item.getItemId() == R.id.item_medicine) {
                    Intent x6 = new Intent(ViewDoctorsActivity.this, ViewMedicineActivity.class);
                    x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x6);
                } else {
                    Toast.makeText(getApplicationContext(), "under development", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViews(ActivityViewDoctorsBinding activityViewDoctorsBinding) {
        recyclerView = activityViewDoctorsBinding.recyclerView;
        doctorModelList = new ArrayList<>();
        doctorAdapter = new DoctorAdapter();
        bottomNavigationView = activityViewDoctorsBinding.bottomNavigationView;
    }
}
