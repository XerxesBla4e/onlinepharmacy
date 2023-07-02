package com.example.fixit.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fixit.Adapter.Doctor;
import com.example.fixit.Interface.OnMoveToDets;
import com.example.fixit.Model.DoctorModel;
import com.example.fixit.R;
import com.example.fixit.databinding.ActivityDoctorViewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorViewActivity extends AppCompatActivity {

    ActivityDoctorViewBinding activityDoctorViewBinding;
    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    List<DoctorModel> doctorModelList;
    Doctor doctoradapter;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityDoctorViewBinding = ActivityDoctorViewBinding.inflate(getLayoutInflater());
        setContentView(activityDoctorViewBinding.getRoot());

        initViews(activityDoctorViewBinding);

        initBottomNavView();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        doctoradapter.setOnMoveToDets(new OnMoveToDets() {
            @Override
            public void onMoveToDets(DoctorModel doctorModel) {
                // Handle the click event, for example, navigate to patient details screen
                Intent intent = new Intent(getApplicationContext(), PharmacyActivity.class);
                intent.putExtra("doctorModel", doctorModel);
                startActivity(intent);
            }
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("users")
                .whereEqualTo("accounttype", "Admin")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                         /*   String name = documentSnapshot.getString("name");
                            String location = documentSnapshot.getString("location");
                            String specialization = documentSnapshot.getString("specialization");
                            String contact = documentSnapshot.getString("contact");
                            String did = documentSnapshot.getString("did");
                            String timestamp = documentSnapshot.getString("timestamp");
                            String uid = documentSnapshot.getString("uid");
                            String image = documentSnapshot.getString("image")*/

                            // Create a DoctorModel object and do further processing
                            DoctorModel doctor = documentSnapshot.toObject(DoctorModel.class);
                            //= new DoctorModel(name, location, specialization, contact, did, timestamp, uid, image);
                            // Process the retrieved doctor object as needed
                            doctorModelList.add(doctor);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure in retrieving doctors from Firestore
                        Toast.makeText(getApplicationContext(), "No Doctors Available: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void initBottomNavView() {
        bottomNavigationView.setSelectedItemId(R.id.item_doctors);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(MenuItem item) {
                if (item.getItemId() == R.id.item_home) {
                    Intent x = new Intent(DoctorViewActivity.this, DoctorViewActivity.class);
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
        recyclerView = activityDoctorViewBinding.recyclerView;
        doctoradapter = new Doctor();
        doctorModelList = new ArrayList<>();
        bottomNavigationView = activityDoctorViewBinding.bottomNavigationView;
    }
}