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

import com.example.fixit.Adapter.MedicineA;
import com.example.fixit.Authentication.LoginActivity;
import com.example.fixit.Model.MedicineModel;
import com.example.fixit.R;
import com.example.fixit.databinding.ActivityViewMedicineBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewMedicineActivity extends AppCompatActivity {

    ActivityViewMedicineBinding activityViewMedicineBinding;
    RecyclerView recyclerView;
    MedicineA medicineA;
    List<MedicineModel> medicineAList;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;
    String uid1;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityViewMedicineBinding = ActivityViewMedicineBinding.inflate(getLayoutInflater());
        setContentView(activityViewMedicineBinding.getRoot());

        initViews(activityViewMedicineBinding);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid1 = firebaseUser.getUid();
        } else {
            //   startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(medicineA);

        initBottomNavView();

        DocumentReference userRef = firestore.collection("users").document(uid1);
        userRef.collection("Medicine").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Retrieve the data from the document snapshot
                         /*   String name = documentSnapshot.getString("mname");
                            String category = documentSnapshot.getString("mcategory");
                            int price = Objects.requireNonNull(documentSnapshot.getLong("mprice")).intValue();
                            String id = documentSnapshot.getString("mid");
                            String timestamp = documentSnapshot.getString("timestamp");
                            String uid = documentSnapshot.getString("uid");
                            String image = documentSnapshot.getString("image");*/

                            // Create a Medicine object and add it to the list
                            MedicineModel medicine = documentSnapshot.toObject(MedicineModel.class);
                                    //= new MedicineModel(name, category, price, id, timestamp, uid, image);
                            medicineAList.add(medicine);
                        }

                        // Update the adapter with the retrieved medicine list
                        medicineA.submitList(medicineAList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure in retrieving medicine items from Firestore
                        Toast.makeText(getApplicationContext(), "Failed to retrieve medicine items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

                    //   Toast.makeText(PatientMainActivity.this, "Cart Item deleted", Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        }).attachToRecyclerView(activityViewMedicineBinding.recyclerView);

    }

    private void initViews(ActivityViewMedicineBinding activityViewMedicineBinding) {
        recyclerView = activityViewMedicineBinding.recyclerView;
        medicineA = new MedicineA();
        medicineAList = new ArrayList<>();
        bottomNavigationView = activityViewMedicineBinding.bottomNavigationView;
    }

    private void initBottomNavView() {
        bottomNavigationView.setSelectedItemId(R.id.item_medicine);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(MenuItem item) {
                if (item.getItemId() == R.id.item_doctors) {
                    Intent x = new Intent(ViewMedicineActivity.this, ViewDoctorsActivity.class);
                    x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x);
                } else if (item.getItemId() == R.id.item_home) {
                    Intent x6 = new Intent(ViewMedicineActivity.this, AdminMainActivity.class);
                    x6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(x6);
                } else {
                    Toast.makeText(getApplicationContext(), "Under development", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}